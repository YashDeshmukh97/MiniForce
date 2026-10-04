$ErrorActionPreference = 'Stop'

function Request-Api {
    param([string]$Path, [string]$Method='GET', [object]$Body=$null, [string]$Token=$null)
    $uri = "http://localhost:8080/api" + $Path
    $headers = @{}
    if ($Token) { $headers["Authorization"] = "Bearer $Token" }
    
    $params = @{
        Uri = $uri
        Method = $Method
        Headers = $headers
    }
    
    if ($Body) {
        $params.Body = $Body | ConvertTo-Json
        $params.ContentType = "application/json"
    }
    
    try {
        $resp = Invoke-RestMethod @params
        return @{ status = 200; data = $resp }
    } catch {
        return @{ status = $_.Exception.Response.StatusCode.value__; data = $null }
    }
}

Write-Host "1. Creating user and logging in..."
$rnd = Get-Random -Maximum 100000
$body = @{ fullName="Smoke Test"; username="smoke$rnd"; email="smoke$rnd@test.com"; password="password" }
$auth = Request-Api -Path "/auth/register" -Method "POST" -Body $body
if ($auth.status -ne 200) {
    $auth = Request-Api -Path "/auth/login" -Method "POST" -Body @{ identity=$body.username; password=$body.password }
}
$token = $auth.data.token
Write-Host "Auth data: $($auth.data | ConvertTo-Json -Depth 2)"; Write-Host "Logged in. Token length: $($token.Length)"

Write-Host "2. Fetching problems..."
$prob = Request-Api -Path "/problems"
$list = $prob.data
Write-Host "Fetched $($list.Length) problems."

$realIds = @(1, 2, 3, 8, 12, 13, 14, 20)
foreach ($id in $realIds) {
    $p = Request-Api -Path "/problems/$id"
    Write-Host "Problem $id loaded: $($p.data.title)"
}

$codes = @{
    "8" = "class Solution { public int maxProfit(int[] prices) { int m = 0, min = prices[0]; for(int p:prices){ if(p<min) min=p; else if(p-min>m) m=p-min;} return m; } }"
    "12" = "class Solution { public int climbStairs(int n) { if(n<=2) return n; int a=1,b=2; for(int i=3;i<=n;i++){int c=a+b;a=b;b=c;} return b; } }"
    "13" = "class Solution { public int[] productExceptSelf(int[] nums) { int[] r = new int[nums.length]; int p = 1; for(int i=0;i<nums.length;i++){ r[i] = p; p*=nums[i]; } p=1; for(int i=nums.length-1;i>=0;i--){ r[i]*=p; p*=nums[i]; } return r; } }"
    "14" = "class Solution { public int maxSubArray(int[] nums) { int max=nums[0], c=nums[0]; for(int i=1;i<nums.length;i++){ c=Math.max(nums[i], c+nums[i]); max=Math.max(max,c); } return max; } }"
    "20" = "class Solution { public int longestConsecutive(int[] nums) { if(nums.length==0) return 0; java.util.Arrays.sort(nums); int max=1,c=1; for(int i=1;i<nums.length;i++){ if(nums[i]==nums[i-1])continue; if(nums[i]==nums[i-1]+1) c++; else {if(c>max)max=c;c=1;} } return Math.max(max,c); } }"
}

foreach ($k in $codes.Keys) {
    $id = [int]$k
    $code = $codes[$k]
    Write-Host "Submitting problem $id..."
    $subBody = @{ problemId = $id; language = "JAVA_17"; code = $code }
    $sub = Request-Api -Path "/submissions/async" -Method "POST" -Body $subBody -Token $token
    $subId = $sub.data.id
    Write-Host "Submission for $id created: $subId"
    
    $status = "QUEUED"
    $limit = 20
    while (($status -eq "QUEUED" -or $status -eq "COMPILING" -or $status -eq "EXECUTING") -and $limit -gt 0) {
        Start-Sleep -Seconds 1
        $statReq = Request-Api -Path "/submissions/$subId" -Token $token
        $status = $statReq.data.status
        Write-Host "  -> Status: $status"
        $limit--
    }
    if ($status -ne "ACCEPTED") { Write-Host "FAILED: Expected ACCEPTED for $id, got $status, data: $($statReq.data | ConvertTo-Json -Depth 2)" } else { Write-Host "PASSED: $id" }
}

Write-Host "Submitting wrong answer to problem 8..."
$waBody = @{ problemId = 8; language = "JAVA_17"; code = "class Solution { public int maxProfit(int[] prices) { return 0; } }" }
$waSub = Request-Api -Path "/submissions/async" -Method "POST" -Body $waBody -Token $token
$waId = $waSub.data.id
$status = "QUEUED"
while ($status -eq "QUEUED" -or $status -eq "COMPILING" -or $status -eq "EXECUTING") {
    Start-Sleep -Seconds 1
    $statReq = Request-Api -Path "/submissions/$waId" -Token $token
    $status = $statReq.data.status
}
Write-Host "  -> WA Status: $status"
if ($status -ne "WRONG_ANSWER") { throw "Expected WRONG_ANSWER, got $status" }

Write-Host "Submitting compilation error to problem 12..."
$ceBody = @{ problemId = 12; language = "JAVA_17"; code = "class Solution { public int climbStairs(int n) { return x; } }" }
$ceSub = Request-Api -Path "/submissions/async" -Method "POST" -Body $ceBody -Token $token
$ceId = $ceSub.data.id
$status = "QUEUED"
while ($status -eq "QUEUED" -or $status -eq "COMPILING" -or $status -eq "EXECUTING") {
    Start-Sleep -Seconds 1
    $statReq = Request-Api -Path "/submissions/$ceId" -Token $token
    $status = $statReq.data.status
}
Write-Host "  -> CE Status: $status"
if ($status -ne "COMPILATION_ERROR") { throw "Expected COMPILATION_ERROR, got $status" }

Write-Host "All backend real judge flows verified."











