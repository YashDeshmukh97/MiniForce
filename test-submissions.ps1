$jwt = ""
try {
    $jwt = (Invoke-RestMethod -Method Post -Uri "http://127.0.0.1:8080/api/auth/register" -ContentType "application/json" -Body '{"username":"pytestuser","email":"py@test.com","password":"password123"}').token
} catch {
    $jwt = (Invoke-RestMethod -Method Post -Uri "http://127.0.0.1:8080/api/auth/login" -ContentType "application/json" -Body '{"username":"pytestuser","password":"password123"}').token
}

$headers = @{ "Authorization" = "Bearer $jwt"; "Content-Type" = "application/json" }

function Submit-Code($prob, $lang, $code) {
    $body = @{ problemId = $prob; language = $lang; code = $code } | ConvertTo-Json
    $res = Invoke-RestMethod -Method Post -Uri "http://127.0.0.1:8080/api/submissions/submit" -Headers $headers -Body $body
    $id = $res.id
    while ($true) {
        Start-Sleep -Seconds 1
        $chk = Invoke-RestMethod -Uri "http://127.0.0.1:8080/api/submissions/$id" -Headers $headers
        if ($chk.status -notin @('QUEUED','COMPILING','RUNNING')) {
            Write-Host "Problem $prob -> $($chk.status)"
            return
        }
    }
}

Submit-Code 1 "PYTHON_3" "class Solution:`n    def twoSum(self, nums, target):`n        d = {}`n        for i, n in enumerate(nums):`n            if target - n in d:`n                return [d[target - n], i]`n            d[n] = i"
Submit-Code 2 "PYTHON_3" "class Solution:`n    def isValid(self, s):`n        stack = []`n        d = {')': '(', ']': '[', '}': '{'}`n        for c in s:`n            if c in d:`n                if not stack or stack.pop() != d[c]: return False`n            else:`n                stack.append(c)`n        return not stack"
Submit-Code 8 "PYTHON_3" "class Solution:`n    def maxProfit(self, prices):`n        m = float('inf')`n        p = 0`n        for x in prices:`n            m = min(m, x)`n            p = max(p, x - m)`n        return p"
Submit-Code 1 "PYTHON_3" "class Solution:`n    def twoSum(self, nums, target):`n        return []"
