# Contributing to expenseTracker

Thank you for contributing! This project uses pull requests and automated checks.

## Development workflow
- Fork the repository and create a branch (feature/your-feature or fix/short-description).
- Keep changes focused and scoped to a single purpose.
- Rebase or keep your branch up to date with `main` before opening a PR.

## Local checks to run before opening a PR
1. Install JDK 17 and Android SDK.
2. Build the project:
   - Windows (PowerShell): `./gradlew assembleDebug`
3. Run unit tests:
   - `./gradlew testDebugUnitTest`
4. Run Android Lint:
   - `./gradlew lintDebug`
5. Generate Jacoco coverage locally:
   - `./gradlew jacocoTestReport`

## Linting and formatting
We recommend adding ktlint and detekt to your local environment:
- ktlint: https://ktlint.dev/
- detekt: https://github.com/detekt/detekt

CI will run lint and unit tests on every PR. Ensure your PR passes the CI checks.

## Security
If you discover a security issue, see SECURITY.md for private reporting instructions.

## Releases & changelog
- Update CHANGELOG.md when adding features or making breaking changes.
- Releases are created automatically when a tag like `vX.Y.Z` is pushed.

## PR checklist
- Tests for new behavior
- Follow code style
- Documentation updated
- Assign reviewers and link related issues
