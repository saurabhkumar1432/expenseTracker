# Play Store Metadata

This directory contains the Play Store listing metadata for the Expense Tracker app, structured for use with Fastlane Supply.

## Directory Structure

```
play-store/
├── README.md
└── metadata/
    └── en-US/
        ├── title.txt              # App name (max 30 characters)
        ├── short_description.txt  # Brief description (max 80 characters)
        ├── full_description.txt   # Full description (max 4000 characters)
        └── changelogs/
            └── default.txt        # Default changelog for all versions
```

## Updating Metadata

### Title (title.txt)
- Maximum 30 characters
- This is the app name shown on the Play Store

### Short Description (short_description.txt)
- Maximum 80 characters
- Displayed on the Play Store listing page
- Should be compelling and describe the app's main purpose

### Full Description (full_description.txt)
- Maximum 4000 characters
- Supports limited HTML formatting:
  - `<b>bold</b>`
  - `<i>italic</i>`
  - `<u>underline</u>`
- Use bullet points with • (Alt+7 on Windows)
- Include key features, benefits, and a call to action

### Changelogs (changelogs/)
- `default.txt` - Used when no version-specific changelog exists
- Version-specific: Create `{versionCode}.txt` (e.g., `15.txt` for version code 15)
- Maximum 500 characters per changelog

## Adding New Languages

1. Create a new directory under `metadata/` with the locale code (e.g., `es-ES`, `fr-FR`)
2. Copy all files from `en-US/` to the new directory
3. Translate the content

## Using with Fastlane

### Setup
```bash
# Install Fastlane
gem install fastlane

# Initialize Fastlane in project root
fastlane init
```

### Upload Metadata
```bash
# Upload metadata only
fastlane supply --metadata_path ./play-store/metadata

# Upload with APK/AAB
fastlane supply --aab app/build/outputs/bundle/release/app-release.aab --metadata_path ./play-store/metadata
```

### Fastfile Example
```ruby
lane :deploy do
  gradle(task: "bundleRelease")
  supply(
    track: "production",
    aab: "app/build/outputs/bundle/release/app-release.aab",
    metadata_path: "./play-store/metadata"
  )
end
```

## Graphics Assets

For screenshots and feature graphics, add these directories:
- `metadata/en-US/images/phoneScreenshots/` - Phone screenshots (min 2, max 8)
- `metadata/en-US/images/featureGraphic.png` - Feature graphic (1024x500)
- `metadata/en-US/images/icon.png` - App icon (512x512)

## Resources

- [Fastlane Supply Documentation](https://docs.fastlane.tools/actions/supply/)
- [Play Store Listing Guidelines](https://support.google.com/googleplay/android-developer/answer/9859455)
- [Google Play Console](https://play.google.com/console)
