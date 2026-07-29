# Kliq Push Notification & Deep-Linking Test Script (Kapitel 6.5)

Write-Host "==========================================================" -ForegroundColor Purple
Write-Host "      KLIQ PUSH NOTIFICATION & DEEP-LINK TEST ROUTINE     " -ForegroundColor Purple
Write-Host "==========================================================" -ForegroundColor Purple

Write-Host "`n[1/3] Testing Foreground State (App active)..." -ForegroundColor Yellow
adb shell am broadcast -a com.kliq.app.ACTION_SIMULATE_PUSH `
  --es "chat_id" "chat_usr_123" `
  --es "sender_id" "usr_123" `
  --es "sender_name" "Alice Miller" `
  --es "preview_text" "Hi! Foreground Push Test Message" `
  --es "notification_type" "direct_message"

Start-Sleep -Seconds 3

Write-Host "`n[2/3] Minimizing app for Background State Test..." -ForegroundColor Yellow
adb shell input keyevent 3
Start-Sleep -Seconds 2
adb shell am broadcast -a com.kliq.app.ACTION_SIMULATE_PUSH `
  --es "chat_id" "city_berlin" `
  --es "sender_id" "usr_456" `
  --es "sender_name" "Berlin Party Radar" `
  --es "preview_text" "@everyone Background City Chat Mention" `
  --es "notification_type" "city_chat_mention"

Start-Sleep -Seconds 3

Write-Host "`n[3/3] Killing app process for Killed State Deep-Link Test..." -ForegroundColor Yellow
adb shell am force-stop com.kliq.app
Start-Sleep -Seconds 2

Write-Host "Simulating Notification Click & Deep-Link Navigation..." -ForegroundColor Cyan
adb shell am start -W -a android.intent.action.VIEW `
  -d "kliq://chat/chat_usr_789?senderId=usr_789&type=direct_message" com.kliq.app

Write-Host "`n==========================================================" -ForegroundColor Green
Write-Host "  TEST COMPLETED: Verify chat screen opened for chat_usr_789 " -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
