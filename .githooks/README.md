# Git Hooks for Security

This directory contains git hooks that help prevent accidental commits of secrets and sensitive files.

## Hooks Included

| Hook | Purpose |
|------|---------|
| `pre-commit` | Scans staged files for secrets, API keys, passwords, and sensitive file types |
| `pre-push` | Final safety check before pushing to remote repository |

## Setup

### Windows (PowerShell)
```powershell
.\scripts\setup-hooks.ps1
```

### macOS / Linux
```bash
chmod +x scripts/setup-hooks.sh
./scripts/setup-hooks.sh
```

### Manual Setup
```bash
git config core.hooksPath .githooks
```

## What Gets Checked

### Sensitive Files
- `*.jks`, `*.keystore` - Android signing keystores
- `*.p12`, `*.pem`, `*.key` - Certificates and private keys
- `google-services.json` - Firebase configuration
- `local.properties` - Local SDK paths and secrets
- `.env`, `.env.*` - Environment variables
- `*credentials*.json` - Service account files

### Secret Patterns
- API keys (Google, AWS, Firebase, etc.)
- Hardcoded passwords and tokens
- Private keys (RSA, DSA, EC, OpenSSH)
- Android signing credentials

## Bypassing Hooks

If you're **absolutely certain** a detection is a false positive:

```bash
# Bypass pre-commit
git commit --no-verify

# Bypass pre-push  
git push --no-verify
```

⚠️ **Warning:** Only bypass if you've manually verified no secrets are exposed!

## Troubleshooting

### Hooks not running?
Ensure the hooks directory is configured:
```bash
git config core.hooksPath
# Should output: .githooks
```

### Permission denied (macOS/Linux)?
Make hooks executable:
```bash
chmod +x .githooks/*
```
