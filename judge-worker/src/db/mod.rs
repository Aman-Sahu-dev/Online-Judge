// src/db/mod.rs
use crate::judge::TestCase;
use tokio_postgres::{Error, NoTls};
use uuid::Uuid;

pub struct ProblemData {
    pub id: Uuid,
    pub time_limit_ms: i32,
    pub memory_limit_mb: i32,
}

pub async fn fetch_problem_and_tests(
    problem_id: Uuid,
) -> Result<(ProblemData, Vec<TestCase>), Error> {
    let connection_string = "host=localhost user=postgres password=postgres dbname=judge";
    let (client, connection) = tokio_postgres::connect(connection_string, NoTls).await?;

    tokio::spawn(async move {
        if let Err(e) = connection.await {
            eprintln!("Database connection error: {}", e);
        }
    });

    let problem_row = client
        .query_one(
            "SELECT id, time_limit_ms, memory_limit_mb FROM problem WHERE id = $1",
            &[&problem_id],
        )
        .await?;

    let problem = ProblemData {
        id: problem_row.get(0),
        time_limit_ms: problem_row.get(1),
        memory_limit_mb: problem_row.get(2),
    };

    let rows = client
        .query(
            "SELECT id, input, expected_output FROM test_case WHERE problem_id = $1 ORDER BY created_at ASC",
            &[&problem_id],
        )
        .await?;

    let test_cases: Vec<TestCase> = rows
        .iter()
        .map(|row| TestCase {
            input: row.get(1),
            expected_output: row.get(2),
        })
        .collect();

    Ok((problem, test_cases))
}
