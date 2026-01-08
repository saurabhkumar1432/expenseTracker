# Contributing Guidelines

Thank you for your interest in contributing to our Android applications! We are dedicated to keeping our projects open source and high quality.

## 📁 Repository Standards

Before contributing, please read:

👉 **[BRANCH_STRATEGY.md](./BRANCH_STRATEGY.md)** - Branching and release workflow

## 🛠️ Getting Started

1. **Fork the repository** you want to work on.
2. **Clone** your fork locally.
3. **Set up your environment**:
    - JDK 17
    - Android Studio Hedgehog or newer
    - Check `local.properties.example` in the specific project for required keys.

## 🔄 Development Workflow

1. **Create a Branch**:
   **Always branch from `develop`, never `main`!**
   ```bash
   git checkout develop
   git pull origin develop
   git checkout -b feature/amazing-feature
   ```

2. **Make Changes**:
   - Follow the architecture defined in the project's `README.md`.
   - Ensure you add unit tests for logic changes.
   - Run `gradlew lint` to check for style issues.

3. **Commit Messages**:
   We follow [Conventional Commits](https://www.conventionalcommits.org/):
   - `feat: add new login screen`
   - `fix: resolve crash on startup`
   - `docs: update readme`

4. **Pull Request**:
   - Push your branch to GitHub.
   - **Open a Pull Request against the `develop` branch.**
   - Fill out the PR template completely.

## 🐛 Reporting Bugs

Please use the **Bug Report** issue template when reporting bugs. Include:
- Device model and Android version
- Steps to reproduce
- Screenshots or screen recordings
- Logs (if available)

## ⚖️ License

By contributing, you agree that your contributions will be licensed under the MIT License defined in the root of the repository.
