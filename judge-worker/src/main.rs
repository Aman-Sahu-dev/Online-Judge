use std::io;
use std::process::Command;
use tempfile::tempdir;

fn main() -> io::Result<()> {
    // Hardcoded source code we want to judge
    let source_code = r#"
        fn main() {
            println!("Hello from judged code!");
        }
    "#;

    // Create a temporary workspace
    let workspace = tempdir()?;
    println!("Workspace: {}", workspace.path().display());

    // Write source code to main.rs
    let source_path = workspace.path().join("main.rs");
    std::fs::write(&source_path, source_code)?;
    println!("Source written to: {}", source_path.display());

    // Compile
    let binary_path = workspace.path().join("main");
    println!("Compiling...");

    let compile_output = Command::new("rustc")
        .arg(&source_path)
        .arg("-o")
        .arg(&binary_path)
        .output()?;

    if !compile_output.status.success() {
        println!("COMPILATION FAILED");
        println!("stderr: {}", String::from_utf8_lossy(&compile_output.stderr));
        return Ok(());
    }

    println!("Compilation succeeded.");

    // Run the compiled binary
    println!("Running...");
    let run_output = Command::new(&binary_path).output()?;

    let exit_code = run_output.status.code().unwrap_or(-1);
    let stdout = String::from_utf8_lossy(&run_output.stdout);
    let stderr = String::from_utf8_lossy(&run_output.stderr);

    println!("Exit code: {}", exit_code);
    println!("stdout: {}", stdout.trim());
    println!("stderr: {}", stderr.trim());

    // workspace is automatically deleted when it goes out of scope
    println!("Workspace cleaned up.");
    Ok(())
}