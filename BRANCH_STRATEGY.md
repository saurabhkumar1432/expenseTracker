# Branching Strategy & Release Workflow

This repository follows a strict **Protected Branch Workflow** to ensure stability and security for automated deployments.

## 🌳 Branch Structure

```
main (production)     ← Protected, triggers Production release
  ↑
develop (staging)     ← Protected, triggers Internal/Alpha release  
  ↑
feature/* or fix/*    ← Development branches
```

| Branch | Role | Protection | Play Store Track |
|--------|------|------------|------------------|
| `main` | **Production Code** | 🔒 Protected | **Production** |
| `develop` | **Staging / Beta** | 🔒 Protected | **Internal** |
| `feature/*` | Development | Open | None |
| `fix/*` | Bug Fixes | Open | None |

## 🚀 Automated Deployment Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    DEVELOPMENT WORKFLOW                          │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  feature/xyz ──PR──> develop ──PR──> main ──Tag──> Release      │
│       │                 │              │              │          │
│       ▼                 ▼              ▼              ▼          │
│   CI Tests         Internal        GitHub         Production    │
│   Lint/Detekt      Track           Release        Track         │
│                    (Auto)          (Draft)        (Auto)        │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

| Trigger | Play Store Track | When |
|---------|------------------|------|
| Push to `develop` | **Internal** | Automatic after PR merge |
| GitHub Prerelease | **Beta** | When prerelease is published |
| GitHub Release | **Production** | When full release is published |
| Manual workflow | Any track | Via workflow_dispatch |

## 📱 Play Store Deployment Setup

### Required GitHub Secrets

Go to **Repository Settings > Secrets and variables > Actions** and add:

| Secret Name | Description | How to Get |
|-------------|-------------|------------|
| `KEYSTORE_FILE` | Base64-encoded release keystore | See below |
| `KEYSTORE_PASSWORD` | Keystore password | Your keystore password |
| `KEY_ALIAS` | Key alias name | Your key alias |
| `KEY_PASSWORD` | Key password | Your key password |
| `PLAY_STORE_SERVICE_ACCOUNT` | Play Console API JSON | See below |

### Step 1: Encode Your Keystore

```powershell
# Windows PowerShell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("path\to\your-release.keystore")) | Set-Clipboard
# Paste the clipboard content into KEYSTORE_FILE secret
```

```bash
# macOS/Linux
base64 -i path/to/your-release.keystore | pbcopy  # macOS
base64 path/to/your-release.keystore | xclip      # Linux
```

### Step 2: Create Play Store Service Account

1. Go to [Google Play Console](https://play.google.com/console)
2. Navigate to **Settings > API access**
3. Click **Create new service account**
4. Follow the link to Google Cloud Console
5. Create a service account with these settings:
   - Name: `play-store-deploy`
   - Role: None (we'll set permissions in Play Console)
6. Create a JSON key and download it
7. Back in Play Console, grant the service account these permissions:
   - **Release to production, exclude devices, and use Play App Signing**
   - **Manage testing tracks and edit tester lists**
8. **Base64 encode** the JSON file and add to `PLAY_STORE_SERVICE_ACCOUNT` secret:
   ```powershell
   # Windows PowerShell
   [Convert]::ToBase64String([IO.File]::ReadAllBytes("service-account.json")) | Set-Clipboard
   ```
   ```bash
   # macOS/Linux
   base64 -i service-account.json | pbcopy
   ```

### Step 3: Verify Setup

Run the workflow manually:
1. Go to **Actions > Deploy to Play Store**
2. Click **Run workflow**
3. Select `internal` track
4. Monitor the run for any errors

## 🛡️ Branch Protection Rules

### For `main` branch:
- ✅ Require pull request before merging
- ✅ Require 1 approval
- ✅ Require status checks: `Build & Test`, `PR CI`
- ✅ Require branches to be up to date
- ✅ Do not allow bypassing settings
- ⬜ Require signed commits (recommended)

### For `develop` branch:
- ✅ Require pull request before merging  
- ✅ Require status checks: `Build & Test`
- ⬜ Require 1 approval (optional for small teams)

## 🔄 Developer Workflow

### 1. Start a Feature
```bash
git checkout develop
git pull origin develop
git checkout -b feature/amazing-feature
```

### 2. Develop & Commit
```bash
git add .
git commit -m "feat: add amazing feature"
git push -u origin feature/amazing-feature
```

### 3. Create PR to `develop`
- Open PR: `feature/amazing-feature` → `develop`
- Wait for CI checks
- Get code review
- Merge (squash recommended)
- 🚀 **Automatic deployment to Internal track!**

### 4. Release to Production
```bash
# When ready for production
git checkout main
git pull origin main
git merge develop
git push origin main

# Create release tag
git tag -a v1.2.0 -m "Release v1.2.0"
git push origin v1.2.0
```

Then go to GitHub and **Create Release** from the tag.
🚀 **Automatic deployment to Production track!**
