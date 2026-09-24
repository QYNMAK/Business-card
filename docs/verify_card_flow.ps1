$ErrorActionPreference = "Continue"
$base = "http://localhost:8080"

Write-Output "=== 1. dev-login ==="
$l = Invoke-RestMethod -Method Get -Uri "$base/user/dev-login?unionId=wx-demo&username=演示"
$tok = $l.data.jwt
Write-Output ("TOKEN OK, len=" + $tok.Length + ", register=" + $l.data.register)

$h = @{ Authorization = $tok }

Write-Output "=== 2. GET /card (read) ==="
$c = Invoke-RestMethod -Method Get -Uri "$base/card" -Headers $h
Write-Output ("CARD success=" + $c.success + " saved=" + $c.data.saved + " platform=[" + $c.data.platform + "]")

Write-Output "=== 3. POST /card/update ==="
$body = @{ username = "张三"; bizType = "餐饮"; bizName = "老王牛肉面"; platform = "微信"; phone = "13800000000"; wechatId = "wx_demo"; email = "demo@ex.com" }
$r = Invoke-RestMethod -Method Post -Uri "$base/card/update" -Headers $h -Body ($body | ConvertTo-Json) -ContentType "application/json"
Write-Output ("UPDATE success=" + $r.success + " message=" + $r.message + " saved=" + $r.data.saved)

Write-Output "=== 4. GET /card (verify) ==="
$c2 = Invoke-RestMethod -Method Get -Uri "$base/card" -Headers $h
Write-Output ("CARD2 name=" + $c2.data.username + " bizType=" + $c2.data.bizType + " bizName=" + $c2.data.bizName + " platform=" + $c2.data.platform + " saved=" + $c2.data.saved)

Write-Output "=== 5. GET /qr/create (expect fail in local, no OSS) ==="
try {
  $q = Invoke-RestMethod -Method Get -Uri "$base/qr/create" -Headers $h
  Write-Output ("QR success=" + $q.success + " qrUrl=[" + $q.data.qrUrl + "]")
} catch {
  Write-Output ("QR expected-fail: " + $_.Exception.Message)
}

Write-Output "=== 6. admin stats (verify MySQL data) ==="
$ah = @{ "X-Admin-Key" = "admin-secret-key" }
$s = Invoke-RestMethod -Method Get -Uri "$base/admin/stats/platform" -Headers $ah
Write-Output ("STATS: " + ($s.data | ConvertTo-Json -Compress))
