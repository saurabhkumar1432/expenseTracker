# Branching Strategy & Release Workflow

This repository follows a strict **Protected Branch Workflow** to ensure stability and security for automated deployments.

## 🌳 Branch Structure

| Branch | Role | Protection | Automated Action |
|--------|------|------------|------------------|
| `main` | **Production Code** | 🔒 **Protected**<br>No direct commits<br>PR required | - Creates Release Draft on Tag<br>- Deploys to **Production** (on publish) |
| `develop` | **Staging / Beta** | 🔒 **Protected**<br>No direct commits<br>PR required | - Deploys to **Internal / Alpha** track<br>- Runs Full CI Suite |
| `feature/*` | Development | Open | - Runs Lint & Unit Tests |
| `fix/*` | Bug Fixes | Open | - Runs Lint & Unit Tests |

## 🚀 Workflow Lifecycle

### 1. Development
1. **Create feature branch** from `develop`:
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/new-amazing-screen
   ```
2. **Commit changes** (conventional commits recommended).
3. **Open Pull Request** -> `develop`.
   - CI runs tests.
   - Code review required.

### 2. Staging / Internal Testing
1. **Merge PR** into `develop`.
2. **Automation**:
   - CI builds signed AAB.
   - **Uploads to Play Store (Internal Track)** automatically.
3. QA Team / Testers verify the build from internal track.

### 3. Production Release
1. Create **Release Pull Request** (`develop` -> `main`).
2. **Merge** to `main`.
3. **Tag the release** (e.g., `v1.2.0`).
4. **Automation**:
   - Builds Release Artifacts.
   - Creates GitHub Release.
   - **Uploads to Play Store (Production Track)**.

## 🛡️ Branch Protection Rules (Admin Setup)

Admins should configure GitHub Branch Protection rules for both `main` and `develop`:

- **Require Pull Request reviews**: Yes (at least 1 approval).
- **Require status checks to pass**: Yes (`Build & Test`, `Detekt`).
- **Do not allow bypassing settings**: Yes.
- **Require signed commits**: Recommended.
