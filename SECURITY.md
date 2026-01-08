# Security Policy

## Supported Versions

Please check the individual application's `README.md` to see the currently supported versions. Generally, we support the latest major release.

| Version | Supported          |
| ------- | ------------------ |
| Latest  | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

We take security seriously. If you discover a security vulnerability in any of our applications, please follow these steps:

1. **Do NOT create a public issue.**
2. Email the details to the maintainer (or use the "Private Vulnerability Reporting" feature on GitHub if enabled).
3. Include:
    - Detailed steps to reproduce the vulnerability.
    - Affected versions/devices.
    - Potential impact.

## Response Timeline

- **Acknowledgement:** We will acknowledge receipt of your report within 48 hours.
- **Assessment:** We will assess the severity and impact within 1 week.
- **Fix:** We aim to release a patch for critical vulnerabilities within 2 weeks.

## Keystore & Secrets Management

- We never commit release keystores (`.jks`, `.keystore`) to the repository.
- API keys and secrets are managed via GitHub Secrets and injected during CI/CD.
- If you find a committed secret, please report it immediately so we can rotate it.

## Android Specific Security

- We default to `minSdk = 24` (Android 7.0) to ensure modern security features.
- We use R8/Proguard for code obfuscation on release builds.
- Network traffic is required to use HTTPS (Cleartext traffic disabled by default).
