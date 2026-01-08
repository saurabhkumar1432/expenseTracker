# PowerShell script to set up git hooks on Windows
# Run this script once after cloning the repository

Write-Host "🔧 Setting up git hooks..." -ForegroundColor Cyan

# Set the hooks directory
git config core.hooksPath .githooks

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Git hooks configured successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host "The following hooks are now active:" -ForegroundColor Yellow
    Write-Host "  • pre-commit  - Scans for secrets before each commit"
    Write-Host "  • pre-push    - Final safety check before pushing"
    Write-Host ""
    Write-Host "To bypass hooks in emergencies: git commit --no-verify" -ForegroundColor DarkYellow
} else {
    Write-Host "❌ Failed to configure git hooks" -ForegroundColor Red
    exit 1
}
