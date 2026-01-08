# Fastlane Configuration

This directory contains Fastlane configuration for automated Android app deployment.

## Setup

### 1. Install Fastlane

```bash
# Using RubyGems
gem install fastlane

# Or using Homebrew (macOS)
brew install fastlane
```

### 2. Configure Service Account

1. Go to [Google Play Console](https://play.google.com/console) → Setup → API access
2. Create or link a service account
3. Grant "Release Manager" permissions to the service account
4. Download the JSON key file
5. Place it as `play-store-credentials.json` in the project root (add to `.gitignore`)

Or set the environment variable:
```bash
export SUPPLY_JSON_KEY="path/to/play-store-credentials.json"
```

## Available Lanes

### Testing & Building

| Lane | Description | Command |
|------|-------------|---------|
| `test` | Run all tests | `fastlane test` |
| `lint` | Run lint, ktlint, and detekt | `fastlane lint` |
| `build_debug` | Build debug APK | `fastlane build_debug` |
| `build_release` | Build release APK | `fastlane build_release` |
| `build_bundle` | Build release AAB | `fastlane build_bundle` |

### Deployment

| Lane | Description | Command |
|------|-------------|---------|
| `internal` | Deploy to internal testing | `fastlane internal` |
| `alpha` | Deploy to alpha track | `fastlane alpha` |
| `beta` | Deploy to beta track | `fastlane beta` |
| `production` | Deploy to production | `fastlane production` |

### Promotion

| Lane | Description | Command |
|------|-------------|---------|
| `promote_internal_to_beta` | Promote internal to beta | `fastlane promote_internal_to_beta` |
| `promote_beta_to_production` | Promote beta to production | `fastlane promote_beta_to_production` |

### Metadata

| Lane | Description | Command |
|------|-------------|---------|
| `metadata` | Upload metadata only | `fastlane metadata` |
| `validate` | Validate without uploading | `fastlane validate` |

## Usage Examples

### First Release

```bash
# 1. Build and test locally
fastlane test
fastlane lint
fastlane build_bundle

# 2. Deploy to internal testing first
fastlane internal

# 3. After testing, promote to production
fastlane promote_internal_to_beta
fastlane promote_beta_to_production
```

### Regular Updates

```bash
# Quick deploy to internal testing
fastlane internal

# Or direct to production (after thorough testing)
fastlane production
```

### Update Store Listing Only

```bash
# Update metadata, screenshots, etc. without new build
fastlane metadata
```

## Environment Variables

| Variable | Description |
|----------|-------------|
| `SUPPLY_JSON_KEY` | Path to Play Store service account JSON |
| `SUPPLY_PACKAGE_NAME` | Package name (override Appfile) |
| `SUPPLY_TRACK` | Default track (internal/alpha/beta/production) |

## Troubleshooting

### Authentication Issues
- Ensure service account has correct permissions
- Verify JSON key file is valid and not expired
- Check that API access is enabled in Play Console

### Upload Failures
- Verify version code is higher than any existing upload
- Ensure AAB is signed correctly
- Check that app is not in draft state

### Metadata Errors
- Verify text lengths (title ≤30, short desc ≤80, full desc ≤4000)
- Check changelog length (≤500 characters)
- Ensure required screenshots are present

## Resources

- [Fastlane Documentation](https://docs.fastlane.tools)
- [Supply (Play Store) Action](https://docs.fastlane.tools/actions/supply/)
- [Google Play API Setup](https://docs.fastlane.tools/actions/supply/#setup)
