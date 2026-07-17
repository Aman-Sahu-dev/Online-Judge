use bollard::container::{LogsOptions, WaitContainerOptions};
use bollard::Docker;
use futures_util::StreamExt;
use std::io;
use tempfile::tempdir;

#[tokio::main]
async fn main() -> io::Result<()> {
    // Hardcoded source code
    let source_code = r#"
        fn main() {
            println!("Hello from Docker!");
            eprintln!("This is stderr");
        }
    "#;

    // 1. Create workspace
    let workspace = tempdir()?;
    let workspace_path = workspace.path().to_str().unwrap();
    println!("Workspace: {}", workspace_path);

    // Write source code
    let source_path = workspace.path().join("main.rs");
    std::fs::write(&source_path, source_code)?;

    // 2. Connect to Docker
    //
    let docker = Docker::connect_with_local_defaults()
        .expect("Failed to connect to Docker daemon. Is Docker running?");

    // Ensure we have a Rust image (standard official image for v1)
    println!("Ensuring 'rust:latest' image exists...");
    let _ = docker.create_image(
        Some("rust:latest"),
        None,
        bollard::image::CreateImageOptions {
            from_image: "rust:latest",

            ..Default::default()
        },
    ).await.map(|stream| stream.for_each(|_| async {}));

    // 3. Create Container
    // We mount the workspace into /workspace in the container
    let bind_mount = format!("{}:/workspace:ro", workspace_path); 
    // Note: using :ro (read-only) is fine here because rustc outputs to /workspace,
    // but for compilation we need it to be writable. Let's remove :ro for now.
    let bind_mount_rw = format!("{}:/workspace", workspace_path);

    let config = bollard::container::Config {
        image: Some("rust:latest"),
        // Compile AND run in one command inside the container
        cmd: Some(vec![
            "sh", "-c", 
            "rustc /workspace/main.rs -o /workspace/main && /workspace/main"
        ]),
        host_config: Some(bollard::container::HostConfig {
            binds: Some(vec![bind_mount_rw]),
            ..Default::default()
        }),
        ..Default::default()
    };

    println!("Creating Docker container...");
    let container = docker.create_container(None::<String>, config).await
        .expect("Failed to create container");
    let container_id = container.id;

    // 4. Start Container
    println!("Starting container...");
    docker.start_container::<String>(&container_id, None).await
        .expect("Failed to start container");

    // 5. Wait for execution to finish
    println!("Waiting for execution...");
    let wait_result = docker.wait_container(&container_id, None::<WaitContainerOptions<String>>).await
        .expect("Failed to wait for container");
    
    let exit_code = wait_result.status_code;
    println!("Exit code: {}", exit_code);

    // 6. Capture Logs (stdout and stderr)
    let mut log_stream = docker.logs(
        &container_id,
        Some(LogsOptions {
            stdout: true,
            stderr: true,
            tail: "all".to_string(),
            ..Default::default()
        }),
    );

    let mut stdout_str = String::new();
    let mut stderr_str = String::new();

    while let Some(log_result) = log_stream.next().await {
        match log_result.expect("Failed to read log stream") {
            bollard::container::LogOutput::StdOut { message } => {
                stdout_str.push_str(&String::from_utf8_lossy(&message));
            }
            bollard::container::LogOutput::StdErr { message } => {
                stderr_str.push_str(&String::from_utf8_lossy(&message));
            }
            _ => {}
        }
    }

    println!("--- STDOUT ---\n{}", stdout_str.trim());
    println!("--- STDERR ---\n{}", stderr_str.trim());

    // 7. Cleanup Container
    println!("Cleaning up container...");
    docker.remove_container(&container_id, None).await
        .expect("Failed to remove container");

    // Temp workspace cleans up automatically when `workspace` goes out of scope
    println!("Done.");
    Ok(())
}
