$ErrorActionPreference = "Stop"

$baseUrl = "http://localhost:8080/api"
$token = ""
$username = "e2e_test_user"
$password = "test_pass"

Write-Host "============================="
Write-Host "MINIFORCE E2E TEST SCRIPT"
Write-Host "============================="

# 1. SIGNUP & LOGIN
Write-Host "TEST 1: SIGNUP & LOGIN"
$signupBody = @{ fullName = "E2E Test User"; username = $username; email = "e2e@test.com"; password = $password } | ConvertTo-Json
try {
    $resp = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $signupBody -ContentType "application/json"
    $token = $resp.token
    Write-Host "Signup Success! Token obtained."
} catch {
    # Maybe already exists, try login
    $loginBody = @{ identity = $username; password = $password } | ConvertTo-Json
    $resp = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
    $token = $resp.token
    Write-Host "Login Success! Token obtained."
}
$headers = @{ "Authorization" = "Bearer $token" }

# 2. PROBLEM OPEN
Write-Host "TEST 2: PROBLEM OPEN"
$problem = Invoke-RestMethod -Uri "$baseUrl/problems/1" -Method Get -Headers $headers
if ($problem.title -eq "Two Sum") {
    Write-Host "Problem 1 loaded successfully."
} else {
    Write-Host "Failed to load problem 1."
}

# Helper to submit and poll
function Submit-And-Poll($code, $language, $expectedStatus) {
    $subBody = @{ problemId = 1; language = $language; code = $code } | ConvertTo-Json
    $sub = Invoke-RestMethod -Uri "$baseUrl/submissions/async" -Method Post -Body $subBody -ContentType "application/json" -Headers $headers
    $subId = $sub.id
    Write-Host "Submitted! ID = $subId. Polling..."
    
    for ($i = 0; $i -lt 30; $i++) {
        Start-Sleep -Seconds 2
        $status = Invoke-RestMethod -Uri "$baseUrl/submissions/$subId" -Method Get -Headers $headers
        Write-Host "  Status: $($status.status)"
        if ($status.status -eq $expectedStatus) {
            Write-Host "SUCCESS: Reached expected status $expectedStatus" -ForegroundColor Green
            return $status
        }
        if ($status.status -in @("ACCEPTED", "WRONG_ANSWER", "COMPILATION_ERROR", "RUNTIME_ERROR", "TIME_LIMIT_EXCEEDED")) {
            Write-Host "FAIL: Reached terminal status $($status.status) instead of $expectedStatus" -ForegroundColor Red
            return $status
        }
    }
    Write-Host "FAIL: Timeout waiting for $expectedStatus" -ForegroundColor Red
    return $null
}

# 3. COMPILATION ERROR
Write-Host "TEST 3: COMPILATION ERROR"
$ceCode = "public class Solution { public int[] twoSum(int[] nums, int target) { return syntax_error; } }"
$ceSub = Submit-And-Poll $ceCode "JAVA_17" "COMPILATION_ERROR"
if ($ceSub.compilerOutput) { Write-Host "Compiler output is present." }

# 4. WRONG ANSWER
Write-Host "TEST 4: WRONG ANSWER"
$waCode = "public class Solution { public int[] twoSum(int[] nums, int target) { return new int[]{0, 0}; } }"
$waSub = Submit-And-Poll $waCode "JAVA_17" "WRONG_ANSWER"

# 5. ACCEPTED
Write-Host "TEST 5: ACCEPTED"
$acCode = @"
public class Solution {
    public int[] twoSum(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{};
    }
}
"@
$acSub = Submit-And-Poll $acCode "JAVA_17" "ACCEPTED"

# 6. SUBMISSION HISTORY
Write-Host "TEST 6: SUBMISSION HISTORY"
$history = Invoke-RestMethod -Uri "$baseUrl/submissions" -Method Get -Headers $headers
$historyIds = $history | Select-Object -ExpandProperty id
if ($historyIds -contains $ceSub.id -and $historyIds -contains $waSub.id -and $historyIds -contains $acSub.id) {
    Write-Host "History contains all 3 submissions."
} else {
    Write-Host "History is missing some submissions!" -ForegroundColor Red
}

# 7. PROFILE/STATS
Write-Host "TEST 7: PROFILE/STATS"
$me = Invoke-RestMethod -Uri "$baseUrl/users/me" -Method Get -Headers $headers
Write-Host "Solved Count: $($me.user.solvedCount)"
Write-Host "Streak: $($me.user.streak)"
if ($me.user.solvedCount -ge 1) {
    Write-Host "Stats updated correctly."
} else {
    Write-Host "FAIL: Stats did NOT update." -ForegroundColor Red
}

# 8. LEADERBOARD
Write-Host "TEST 8: LEADERBOARD"
$leaderboard = Invoke-RestMethod -Uri "$baseUrl/leaderboard" -Method Get -Headers $headers
$inLeaderboard = $leaderboard | Where-Object { $_.user.username -eq $username }
if ($inLeaderboard) {
    Write-Host "User found in leaderboard with score $($inLeaderboard.user.rating)"
} else {
    Write-Host "FAIL: User NOT found in leaderboard." -ForegroundColor Red
}

Write-Host "DONE."
