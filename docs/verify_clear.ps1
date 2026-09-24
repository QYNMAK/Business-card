$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"

Write-Output "=== 1. dev-login (unionId=wx-clear-test) ==="
$l = Invoke-RestMethod -Method Get -Uri "$base/user/dev-login?unionId=wx-clear-test&username=清空测试"
$tok = $l.data.jwt
$h = @{ Authorization = $tok }
Write-Output ("token len=" + $tok.Length + " register=" + $l.data.register)

Write-Output "=== 2. 先填名片 ==="
$body = @{ username = "清空测试"; bizType = "测试"; bizName = "测试业务"; platform = "微信"; phone = "13900000000"; wechatId = "wx_clear"; email = "clear@ex.com" }
$r = Invoke-RestMethod -Method Post -Uri "$base/card/update" -Headers $h -Body ($body | ConvertTo-Json) -ContentType "application/json"
Write-Output ("update success=" + $r.success)

Write-Output "=== 3. 读一次 (clear 前) ==="
$before = Invoke-RestMethod -Method Get -Uri "$base/card" -Headers $h
Write-Output ("before: saved=" + $before.data.saved + " phone=[" + $before.data.phone + "] bizName=[" + $before.data.bizName + "] username=[" + $before.data.username + "]")

Write-Output "=== 4. 调 /card/clear ==="
try {
  $clear = Invoke-RestMethod -Method Post -Uri "$base/card/clear" -Headers $h
  Write-Output ("clear success=" + $clear.success + " message=" + $clear.message)
} catch {
  Write-Output ("clear FAILED: " + $_.Exception.Message)
}

Write-Output "=== 5. 再读 (clear 后) ==="
$after = Invoke-RestMethod -Method Get -Uri "$base/card" -Headers $h
Write-Output ("after: saved=" + $after.data.saved + " phone=[" + $after.data.phone + "] bizName=[" + $after.data.bizName + "] username=[" + $after.data.username + "]")
