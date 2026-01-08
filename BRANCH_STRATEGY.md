# 🌳 Branching Strategy & Complete Setup Guide

This document provides a comprehensive guide for the branching strategy, CI/CD workflows, and GitHub repository configuration for this open-source Android project.

---

## Table of Contents

1. [Philosophy](#philosophy)
2. [Branch Structure](#branch-structure)
3. [When Workflows Trigger](#when-workflows-trigger)
4. [Complete Developer Workflow](#complete-developer-workflow)
5. [Release Process](#release-process)
6. [GitHub Repository Setup](#github-repository-setup)
7. [Secrets Configuration](#secrets-configuration)
8. [Branch Protection Rules](#branch-protection-rules)
9. [FAQ](#faq)

---

## Philosophy

### Core Principles

1. **No Unnecessary Releases**: Documentation, CI/CD, or config changes should NOT trigger Play Store deployments
2. **Explicit Releases**: Production releases only happen when intentionally triggered
3. **Quality Gates**: All code must pass CI before merging
4. **Clear Audit Trail**: Every release is tagged and documented

### What Triggers What?

| Change Type | CI Runs? | Play Store Deploy? |
|-------------|----------|-------------------|
| App code (`app/**`) | ✅ Yes | Only on main merge |
| Gradle files | ✅ Yes | Only on main merge |
| Documentation (`**.md`) | ⚪ Docs CI only | ❌ No |
| GitHub Actions (`.github/**`) | ⚪ Docs CI only | ❌ No |
| Fastlane config | ⚪ Docs CI only | ❌ No |
| Play Store metadata | ⚪ Docs CI only | ❌ No |

---

## Branch Structure

```
main (production)
  ↑
  │ PR (only from develop, requires validation)
  │
develop (integration/staging)
  ↑
  │ PR (from any feature/fix branch)
  │
├── feature/xyz     ← New features
├── fix/bug-123     ← Bug fixes
├── docs/update     ← Documentation only
├── ci/improvement  ← CI/CD changes only
└── chore/cleanup   ← Maintenance tasks
```

### Branch Naming Convention

| Prefix | Purpose | Triggers App CI? |
|--------|---------|------------------|
| `feature/*` | New features | ✅ Yes (if app code changes) |
| `fix/*` | Bug fixes | ✅ Yes (if app code changes) |
| `docs/*` | Documentation updates | ❌ No (docs CI only) |
| `ci/*` | CI/CD pipeline changes | ❌ No (docs CI only) |
| `chore/*` | Maintenance, cleanup | Depends on files changed |

### Branch Roles

| Branch | Purpose | Who Merges | Deploys To |
|--------|---------|------------|------------|
| `main` | Production-ready code | Maintainers only | GitHub Release + Play Store Production |
| `develop` | Integration branch | Core team | Nothing automatic |
| `feature/*` | Development | Anyone | Nothing |

---

## When Workflows Trigger

### CI Workflow (`ci.yml`)
**Purpose**: Build and test app code changes

| Trigger | Condition |
|---------|-----------|
| Push to `develop` | Only if `app/**`, `*.gradle*`, or `gradle/**` changed |
| PR to `develop` | Only if `app/**`, `*.gradle*`, or `gradle/**` changed |
| PR to `main` | Only if `app/**`, `*.gradle*`, or `gradle/**` changed |

**What it does**:
- Runs Detekt (static analysis)
- Runs ktlint (code style)
- Builds Debug APK
- Runs unit tests
- Runs Android Lint
- Uploads debug APK as artifact

### Docs Workflow (`docs.yml`)
**Purpose**: Validate documentation and config changes

| Trigger | Condition |
|---------|-----------|
| Push to `develop` or `main` | Only if `**.md`, `.github/**`, `fastlane/**`, etc. changed |
| PR to `develop` or `main` | Only if docs/config files changed |

**What it does**:
- Validates Markdown links
- Checks Play Store metadata length limits
- Validates YAML files

### Release Validation (`release-validation.yml`)
**Purpose**: Ensure release readiness before merging to main

| Trigger | Condition |
|---------|-----------|
| PR to `main` | Always (but blocks non-develop PRs) |

**What it does**:
- ❌ Blocks PRs not from `develop`
- Validates version not already released
- Checks changelog exists
- Runs full test suite
- Builds release APK

### Release Workflow (`release.yml`)
**Purpose**: Create GitHub Release and deploy to Play Store

| Trigger | Condition |
|---------|-----------|
| Push to `main` | Only if `app/**` or `*.gradle*` changed |
| Manual dispatch | Always available |

**What it does**:
- Creates Git tag
- Builds signed APK and AAB
- Creates GitHub Release with assets
- Deploys to Play Store Production

### Prepare Release (`prepare-release.yml`)
**Purpose**: Automate version bumping and PR creation

| Trigger | Condition |
|---------|-----------|
| Manual only | Via Actions UI |

**What it does**:
- Bumps version in `build.gradle.kts`
- Creates changelog file
- Updates `CHANGELOG.md`
- Creates PR from `develop` to `main`

### Deploy Testing (`deploy-testing.yml`)
**Purpose**: Manual deployment to testing tracks

| Trigger | Condition |
|---------|-----------|
| Manual only | Via Actions UI |

**What it does**:
- Deploys current `develop` to Internal/Alpha/Beta track
- Does NOT create releases or tags

---

## Complete Developer Workflow

### For Feature Development

```bash
# 1. Start from develop
git checkout develop
git pull origin develop

# 2. Create feature branch
git checkout -b feature/amazing-feature

# 3. Make changes to app code
# Edit files in app/src/...

# 4. Commit with conventional commits
git add .
git commit -m "feat: add amazing feature"

# 5. Push and create PR
git push -u origin feature/amazing-feature
# Open PR: feature/amazing-feature → develop
```

### For Documentation Only

```bash
# Use docs/ prefix - won't trigger app CI
git checkout -b docs/update-readme

# Make documentation changes
# Edit *.md files, play-store/metadata, etc.

git add .
git commit -m "docs: update installation instructions"
git push -u origin docs/update-readme
# Open PR: docs/update-readme → develop
```

### For CI/CD Changes

```bash
# Use ci/ prefix - won't trigger app CI
git checkout -b ci/improve-caching

# Modify .github/workflows/*.yml
git add .
git commit -m "ci: improve gradle caching"
git push -u origin ci/improve-caching
# Open PR: ci/improve-caching → develop
```

---

## Release Process

### Step-by-Step Production Release

#### 1. Ensure develop is stable
All features merged and tested on `develop`.

#### 2. Run "Prepare Release" workflow
1. Go to **Actions** → **Prepare Release**
2. Click **Run workflow**
3. Select bump type: `patch`, `minor`, or `major`
4. Enter changelog (what users see in Play Store)
5. Click **Run workflow**

This automatically:
- Bumps version (e.g., 2.3.0 → 2.3.1)
- Creates `play-store/metadata/en-US/changelogs/231.txt`
- Updates `CHANGELOG.md`
- Creates PR: `develop` → `main`

#### 3. Review and merge the PR
- Verify changelog is accurate
- Ensure all checks pass
- Merge the PR

#### 4. Automatic release happens
When merged to `main`:
- Git tag created (e.g., `v2.3.1`)
- GitHub Release created with APK/AAB
- Play Store Production deployment

### Manual Testing Deployment

For testing before release:
1. Go to **Actions** → **Internal Testing Deploy**
2. Select track: `internal`, `alpha`, or `beta`
3. Click **Run workflow**

This deploys the current `develop` code to the selected track.

---

## GitHub Repository Setup

### Required Settings

Go to **Settings** in your GitHub repository and configure:

### 1. General Settings

**Settings → General**

| Setting | Value | Why |
|---------|-------|-----|
| Default branch | `develop` | New PRs target develop |
| Allow squash merging | ✅ Enabled | Cleaner history |
| Allow merge commits | ⚪ Optional | For release merges |
| Allow rebase merging | ⚪ Optional | Personal preference |
| Automatically delete head branches | ✅ Enabled | Cleanup after merge |

### 2. Branch Protection Rules

**Settings → Branches → Add rule**

#### Rule 1: Protect `main`

| Setting | Value |
|---------|-------|
| Branch name pattern | `main` |
| Require a pull request before merging | ✅ |
| Require approvals | 1 (or more for larger teams) |
| Dismiss stale PR approvals | ✅ |
| Require status checks to pass | ✅ |
| Status checks required | `Validate Release Readiness` |
| Require branches to be up to date | ✅ |
| Require conversation resolution | ✅ |
| Do not allow bypassing | ✅ |
| Restrict who can push | Maintainers only |

#### Rule 2: Protect `develop`

| Setting | Value |
|---------|-------|
| Branch name pattern | `develop` |
| Require a pull request before merging | ✅ |
| Require approvals | 0-1 (team preference) |
| Require status checks to pass | ✅ |
| Status checks required | `Build & Test` |
| Require branches to be up to date | ⚪ Optional |
| Allow force pushes | ❌ Never |
| Allow deletions | ❌ Never |

### 3. Actions Settings

**Settings → Actions → General**

| Setting | Value |
|---------|-------|
| Actions permissions | Allow all actions |
| Fork pull request workflows | Require approval for first-time contributors |
| Workflow permissions | Read and write permissions |
| Allow GitHub Actions to create PRs | ✅ |

### 4. Environments (Optional but Recommended)

**Settings → Environments**

Create environment: `production`
- Add required reviewers (maintainers)
- This adds an extra approval step before Play Store deployment

---

## Secrets Configuration

**Settings → Secrets and variables → Actions**

### Required Secrets

| Secret Name | Description | How to Get |
|-------------|-------------|------------|
| `KEYSTORE_FILE` | Base64-encoded release keystore | See below |
| `KEYSTORE_PASSWORD` | Keystore password | Your password |
| `KEY_ALIAS` | Key alias in keystore | Your alias |
| `KEY_PASSWORD` | Key password | Your password |
| `PLAY_STORE_SERVICE_ACCOUNT` | Base64-encoded service account JSON | See below |

### Encoding Your Keystore

**Windows PowerShell:**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("C:\path\to\release.keystore")) | Set-Clipboard
```

**macOS/Linux:**
```bash
base64 -i path/to/release.keystore | pbcopy  # macOS
base64 path/to/release.keystore | xclip -selection clipboard  # Linux
```

Paste the result into the `KEYSTORE_FILE` secret.

### Creating Play Store Service Account

1. Go to [Google Play Console](https://play.google.com/console)
2. Navigate to **Settings → API access**
3. Click **Create new service account**
4. In Google Cloud Console:
   - Create service account named `play-store-deploy`
   - No roles needed (permissions set in Play Console)
   - Create JSON key and download
5. Back in Play Console, click **Grant access** on the new account
6. Grant permissions:
   - ✅ Release to production, exclude devices, and use Play App Signing
   - ✅ Manage testing tracks and edit tester lists
7. Base64 encode the JSON file:

**Windows PowerShell:**
```powershell
[Convert]::ToBase64String([IO.File]::ReadAllBytes("service-account.json")) | Set-Clipboard
```

**macOS/Linux:**
```bash
base64 -i service-account.json | pbcopy
```

Paste into `PLAY_STORE_SERVICE_ACCOUNT` secret.

---

## Branch Protection Rules

### Visual Summary

```
┌─────────────────────────────────────────────────────────────────────┐
│                         BRANCH PROTECTION                            │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  main                                                                │
│  ├── 🔒 Protected                                                   │
│  ├── ✅ Require PR (no direct push)                                 │
│  ├── ✅ Require 1+ approval                                         │
│  ├── ✅ Require "Validate Release Readiness" to pass               │
│  ├── ✅ Require branch up-to-date                                   │
│  └── ⛔ Only accepts PRs from 'develop'                             │
│                                                                      │
│  develop                                                             │
│  ├── 🔒 Protected                                                   │
│  ├── ✅ Require PR (no direct push)                                 │
│  ├── ✅ Require "Build & Test" to pass (for app changes)           │
│  └── ✅ Accept PRs from any feature/fix/docs/ci branch             │
│                                                                      │
│  feature/*, fix/*, docs/*, ci/*                                     │
│  └── 🔓 Unprotected (developers can push freely)                   │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## FAQ

### Q: I changed a README file. Will it deploy to Play Store?
**No.** Only changes to `app/**`, `build.gradle.kts`, or `settings.gradle.kts` trigger the release workflow.

### Q: I updated a GitHub Action. Will it trigger CI?
**No.** `.github/**` changes only trigger the Docs CI workflow, not the app CI.

### Q: Can I merge a feature branch directly to main?
**No.** The Release Validation workflow explicitly blocks PRs from any branch except `develop`.

### Q: How do I test my changes on Play Store before production?
Use the **Internal Testing Deploy** workflow to deploy `develop` to the internal track.

### Q: What if I forget to create a changelog?
The Release Validation workflow will fail and block the merge until a changelog exists.

### Q: Can I skip Play Store deployment for a release?
Yes. Run the Release workflow manually and check "Skip Play Store deployment".

### Q: How do I hotfix production?
1. Create `fix/critical-bug` from `main`
2. Fix the issue
3. PR to `develop` first, test
4. Run Prepare Release with `patch` bump
5. Merge the release PR to `main`

### Q: What happens if I push to main directly?
You can't. Branch protection prevents direct pushes to `main`.

---

## Workflow Summary

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| `ci.yml` | App code changes | Build, test, lint |
| `docs.yml` | Doc/config changes | Validate metadata |
| `release-validation.yml` | PR to main | Block bad PRs, full validation |
| `prepare-release.yml` | Manual | Bump version, create PR |
| `release.yml` | Main merge (app code) | GitHub + Play Store release |
| `deploy-testing.yml` | Manual | Deploy to testing tracks |
| `codeql.yml` | Weekly + app changes | Security scanning |

---

## Quick Reference Card

```
┌──────────────────────────────────────────────────────────────────┐
│                     QUICK REFERENCE                               │
├──────────────────────────────────────────────────────────────────┤
│                                                                   │
│  🆕 New Feature:                                                  │
│     develop → feature/xyz → PR → develop                         │
│                                                                   │
│  🐛 Bug Fix:                                                      │
│     develop → fix/bug-123 → PR → develop                         │
│                                                                   │
│  📝 Docs Only:                                                    │
│     develop → docs/update → PR → develop (no app CI)             │
│                                                                   │
│  🧪 Test on Play Store:                                           │
│     Actions → Internal Testing Deploy → Run                       │
│                                                                   │
│  🚀 Production Release:                                           │
│     Actions → Prepare Release → Run                               │
│     Review PR → Merge to main                                     │
│     (Auto: Tag + GitHub Release + Play Store)                    │
│                                                                   │
└──────────────────────────────────────────────────────────────────┘
```
