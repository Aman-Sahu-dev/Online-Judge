use bollard::container::{Config, CreateContainerOptions, HostConfig, LogOutput, RemoveContainerOptions};
use bollard::exec::{CreateExecOptions, StartExecResults};
use bollard::Docker;
use futures_util::StreamExt;
use std::path::Path;
use std::time::Duration;
use tempfile::TempDir;
use tokio::time::timeout;

// --- Domain Types ---

pub struct TestCase {
    pub input: String,
    pub expected_output: String,
}

#[derive(Debug, PartialEq)]
pub enum Verdict {
    Accepted,
    WrongAnswer,
    CompilationError(String),
    RuntimeError(String),
    TimeLimitExceeded,
}

pub struct JudgeResult {
    pub verdict: Verdict,
    pub execution_time_ms: u64,
}

// --- Core Logic ---

pub async fn judge(source_code: &str, test_cases: &[TestCase]) -> JudgeResult {
    let docker = Docker::connect_with_local_defaults().expect("Docker daemon not running");
    
    // Ensure image exists (in production, pre-pull this)
    let _ = docker.create_image(
        Some("rust:latest"), None, 
        bollard::image::CreateImageOptions { from_image: "rust:latest", ..Default::default() }
    ).await.map(|s| s.for_each(|_| async {}));

    let workspace = TempDir::new().expect("Failed to create workspace");
    std::fs::write(workspace.path().join("main.rs"), source_code)
        .expect("Failed to write source code");

    let workspace_path = workspace.path().to_str().unwrap().to_string();
    let container_name = format!("judge-{}", uuid::Uuid::new_v4()); // Needs `uuid = "1"` in Cargo.toml

    // 1. Start Sandbox
    let container = docker.create_container(
        Some(CreateContainerOptions { name: &container_name, ..Default::default() }),
        Config {
            image: Some("rust:latest"),
            cmd: Some(vec!["sleep", "30"]), 
            host_config: Some(HostConfig {
                binds: Some(vec![format!("{}:/workspace", workspace_path)]),
                ..Default::default()
            }),
            ..Default::default()
        },
    ).await.expect("Failed to create container");

    let cid = container.id.clone();
    docker.start_container::<String>(&cid, None).await.expect("Failed to start");

    // 2. Compile
    let (exit_code, _, stderr) = run_exec(&docker, &cid, "rustc /workspace/main.rs -o /workspace/main", None).await;

    if exit_code != 0 {
        cleanup(&docker, &cid).await;
        return JudgeResult { 
            verdict: Verdict::CompilationError(stderr.trim().to_string()), 
            execution_time_ms: 0 
        };
    }

    // 3. Run Test Cases
    let mut total_time = 0;

    for tc in test_cases {
        // Write input to host, instantly visible in container via bind mount
        std::fs::write(workspace.path().join("input.txt"), &tc.input).unwrap();

        // 5 second hard limit per test case (v1 constraint)
        let result = timeout(
            Duration::from_secs(5),
            run_exec(&docker, &cid, "sh -c '/workspace/main < /workspace/input.txt'", None)
        ).await;

        let (exit_code, stdout, stderr) = match result {
            Ok(r) => r,
            Err(_) => {
                cleanup(&docker, &cid).await;
                return JudgeResult { 
                    verdict: Verdict::TimeLimitExceeded, 
                    execution_time_ms: 5000 
                };
            }
        };

        if exit_code != 0 {
            cleanup(&docker, &cid).await;
            return JudgeResult { 
                verdict: Verdict::RuntimeError(stderr.trim().to_string()), 
                execution_time_ms: 0 
            };
        }

        if stdout.trim() != tc.expected_output.trim() {
            cleanup(&docker, &cid).await;
            return JudgeResult { 
                verdict: Verdict::WrongAnswer, 
                execution_time_ms: 0 
            };
        }

        total_time += 10; // Placeholder until we parse real Docker stats
    }

    cleanup(&docker, &cid).await;
    JudgeResult { verdict: Verdict::Accepted, execution_time_ms: total_time }
}

// --- Helpers ---

async fn run_exec(docker: &Docker, cid: &str, cmd: &str, _time_limit: Option<Duration>) -> (i64, String, String) {
    let exec = docker.create_exec(cid, CreateExecOptions {
        cmd: Some(vec!["sh", "-c", cmd]),
        attach_stdout: Some(true),
        attach_stderr: Some(true),
        ..Default::default()
    }).await.unwrap();

    let mut stream = docker.start_exec(&exec.id, None).await.unwrap();
    let (mut stdout_str, mut stderr_str) = (String::new(), String::new());

    if let Some(StartExecResults::Attached(mut out)) = stream {
        while let Some(Ok(frame)) = out.next().await {
            match frame {
                LogOutput::StdOut { message } => stdout_str.push_str(&String::from_utf8_lossy(&message)),
                LogOutput::StdErr { message } => stderr_str.push_str(&String::from_utf8_lossy(&message)),
                _ => {}
            }
        }
    }

    let exit_code = docker.inspect_exec(&exec.id).await.unwrap().exit_code.unwrap_or(-1);
    (exit_code, stdout_str, stderr_str)
}

async fn cleanup(docker: &Docker, cid: &str) {
    docker.remove_container(cid, Some(RemoveContainerOptions { force: true, ..Default::default() })).await.ok();
}
