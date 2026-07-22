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
