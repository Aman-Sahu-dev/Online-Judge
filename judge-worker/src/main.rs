mod judge;

use judge::{TestCase, judge};

#[tokio::main]
async fn main() {
    let code = r#"
        use std::io::{self, BufRead};
        fn main() {
            let stdin = io::stdin();
            let line = stdin.lock().lines().next().unwrap().unwrap();
            let nums: Vec<i32> = line.split_whitespace().map(|s| s.parse().unwrap()).collect();
            println!("{}", nums[0] + nums[1]);
        }
    "#;

    let test_cases = vec![
        TestCase { input: "2 3\n".into(), expected_output: "5".into() },
        TestCase { input: "-5 15\n".into(), expected_output: "10".into() },
    ];

    println!("Judging...");
    let result = judge(code, &test_cases).await;

    println!("Verdict: {:?}", result.verdict);
    
    // To see a Compilation Error, change the code above to "fn main() { let x: u32 = \"string\"; }"
    // To see a TimeLimit Exceeded, change the code to "loop {}"
    // To see a Runtime Error, change the code to "fn main() { panic!(\"crash\"); }"
}
