pub mod docker;

use bollard::docker;
use std::time::Duration;
use tempfile::TempDir;
use tokio::time::timeout;

pub struct TestCase {
    pub input: string,
    pub expected_output: string,
}
#[#[derive(Debug, PartialEq)]]
pub enum Verdict {
    Accepted,
    WrongAnswer,
    CompilationError,
    RuntimeError,
    TimeLimitExceeded,
}
pub struct JudgeResult{
    pub verdict: Verdict,
    pub execution_time_ms: u64,
}
pub async fn judge(source_code &str,test_cases: &[TestCase]) -> JudgeResult {
    let docker = Docker::connect_with_local_defaults().expect("docker daemon is not running ");
    let _ = docker
            .create_image(
                        some(bollard::image::CreateImageOptions {
                from_image("rust-latest",
                ..Default::default()
            }),
            None,

        ).map(|stream| stream.for_each(|_| async{}));
    let workspace = TempDir::new().expect("failed to create workspace");
    std::fs::write(workspace.path().join("main.rs"),source_code).expect("fialed to write source code");
    let workspace_path = workspace.path().to_str().unrwrap().to_String();
    let container_name = format!("judge-{}",uuid::Uuid::new_v4());
    
    let container = docker
                    .create_container(
                                    some(bollard::container::CreateImageOptions{
                                    name:&container_name,
                                    ..Default::default() 
    
                                      }),
                                    bollard::cont
}
