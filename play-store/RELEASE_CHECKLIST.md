# Release Checklist

A comprehensive checklist for releasing new versions of Expense Tracker to the Google Play Store.

## 📋 Pre-Release Tasks

### Code Quality
- [ ] All features for this release are complete and merged
- [ ] Run full test suite: `./gradlew test`
- [ ] Run lint check: `./gradlew lint`
- [ ] Run ktlint: `./gradlew ktlintCheck`
- [ ] Run detekt: `./gradlew detekt`
- [ ] Fix any critical lint/detekt warnings
- [ ] Review and update ProGuard rules if needed

### Version Update
- [ ] Update `versionCode` in `app/build.gradle.kts` (increment by 1)
- [ ] Update `versionName` in `app/build.gradle.kts` (follow semantic versioning)
- [ ] Update `CHANGELOG.md` with new version and changes
- [ ] Create version-specific changelog: `play-store/metadata/en-US/changelogs/{versionCode}.txt`

### Testing
- [ ] Test on physical Android device
- [ ] Test on emulator with different API levels (24, 28, 33, 34)
- [ ] Test all major user flows:
  - [ ] Add income transaction
  - [ ] Add expense transaction
  - [ ] Edit/delete transactions
  - [ ] Filter by category and payment method
  - [ ] Set and track budgets
  - [ ] Export to CSV
  - [ ] Widget functionality
- [ ] Test dark mode
- [ ] Test on different screen sizes (phone, tablet)
- [ ] Test fresh install (onboarding flow)
- [ ] Test upgrade from previous version

### Build Verification
- [ ] Build release APK: `./gradlew assembleRelease`
- [ ] Build release AAB: `./gradlew bundleRelease`
- [ ] Verify APK installs and runs correctly
- [ ] Verify app size is reasonable
- [ ] Test signed release build on device

## 🔐 GitHub Secrets (One-Time Setup)

Ensure these secrets are configured in GitHub repository settings:

| Secret Name | Description | Status |
|-------------|-------------|--------|
| `KEYSTORE_FILE` | Base64 encoded keystore file | [ ] Configured |
| `KEYSTORE_PASSWORD` | Keystore password | [ ] Configured |
| `KEY_ALIAS` | Key alias name | [ ] Configured |
| `KEY_PASSWORD` | Key password | [ ] Configured |
| `PLAY_STORE_SERVICE_ACCOUNT` | Google Play API JSON key (base64) | [ ] Configured |
| `PACKAGE_NAME` | App package name (`com.example.expensetracker`) | [ ] Configured |

### Creating Keystore Secret
```powershell
# Windows PowerShell - Encode keystore to base64
[Convert]::ToBase64String([IO.File]::ReadAllBytes("release.keystore")) | Out-File keystore-base64.txt

# Use the content of keystore-base64.txt as KEYSTORE_FILE secret
```

### Creating Play Store Service Account
1. Go to Google Play Console → Setup → API access
2. Create a new service account or use existing
3. Download JSON key file
4. Base64 encode: `[Convert]::ToBase64String([IO.File]::ReadAllBytes("service-account.json"))`
5. Add as `PLAY_STORE_SERVICE_ACCOUNT` secret

## 🏪 Play Store Console Setup (First Release)

### App Creation
- [ ] Create new app in Google Play Console
- [ ] Set default language (English US)
- [ ] Select app type (Application)
- [ ] Select free or paid
- [ ] Accept developer agreement

### Store Listing
- [ ] Upload app icon (512x512)
- [ ] Upload feature graphic (1024x500)
- [ ] Upload phone screenshots (min 2, max 8)
- [ ] Upload tablet screenshots (recommended)
- [ ] Copy content from `play-store/metadata/en-US/`:
  - [ ] Title from `title.txt`
  - [ ] Short description from `short_description.txt`
  - [ ] Full description from `full_description.txt`

### App Content
- [ ] Complete privacy policy questionnaire
- [ ] Add privacy policy URL (host `PRIVACY_POLICY.md` or use GitHub Pages)
- [ ] Complete content rating questionnaire
- [ ] Complete target audience questionnaire
- [ ] Complete news apps questionnaire (if applicable)
- [ ] Complete COVID-19 apps questionnaire (if applicable)
- [ ] Complete data safety section:
  - [ ] Data collection: Financial info (optional)
  - [ ] Data sharing: None
  - [ ] Data handling: Local storage only
  - [ ] Security practices: Data encrypted in transit (N/A - no network)

### App Signing
- [ ] Let Google manage app signing (recommended)
- [ ] Or upload your signing key
- [ ] Download upload key certificate for verification

### Pricing & Distribution
- [ ] Set app as free
- [ ] Select countries for distribution
- [ ] Opt in/out of programs (Google Play Pass, etc.)

## 🚀 Release Process

### Option 1: Manual Release

1. **Build the release**
   ```powershell
   cd expenseTracker
   .\gradlew bundleRelease
   ```

2. **Locate the AAB**
   ```
   app/build/outputs/bundle/release/app-release.aab
   ```

3. **Upload to Play Console**
   - Go to Play Console → Release → Production (or Internal/Beta testing)
   - Create new release
   - Upload AAB
   - Add release notes from changelog
   - Review and roll out

### Option 2: GitHub Actions Release

1. **Create and push a tag**
   ```powershell
   git tag v2.3.0
   git push origin v2.3.0
   ```

2. **Monitor the workflow**
   - GitHub Actions builds and creates release
   - Download AAB from GitHub release

3. **Upload to Play Store (if not using automated upload)**
   - Download AAB from GitHub release
   - Upload to Play Console manually

### Option 3: Fully Automated (Recommended)

1. **Trigger the workflow**
   - Push tag or use workflow_dispatch
   - `release.yml` creates GitHub release with AAB
   - `play-store.yml` uploads to Play Store

2. **Monitor deployment**
   - Check GitHub Actions for build status
   - Verify upload in Play Console

## 📱 Internal Testing Track (Recommended First)

Before production release:

1. **Create internal testing track**
   - Play Console → Testing → Internal testing
   - Add tester email addresses

2. **Upload to internal track**
   - Upload AAB to internal testing
   - Testers receive email notification

3. **Test thoroughly**
   - Install via Play Store internal track
   - Verify all functionality
   - Check for crashes in Play Console

4. **Promote to production**
   - Once testing is complete
   - Play Console → Promote release → Production

## ✅ Post-Release Tasks

- [ ] Verify app is live on Play Store
- [ ] Test download and installation from Play Store
- [ ] Monitor crash reports in Play Console
- [ ] Monitor user reviews and ratings
- [ ] Respond to any critical issues promptly
- [ ] Update GitHub release notes if needed
- [ ] Announce release on social media/website (optional)
- [ ] Tag the release in your project management tool
- [ ] Archive release artifacts

## 🐛 Rollback Procedure

If critical issues are discovered:

1. **Halt rollout**
   - Play Console → Releases → Halt rollout

2. **Rollback option**
   - Can only rollback to a previous active release
   - Upload the previous version as a new release if needed

3. **Hotfix**
   - Create hotfix branch
   - Fix the issue
   - Increment versionCode
   - Follow release process again

## 📊 Monitoring

After release, monitor:

- [ ] Android Vitals in Play Console
- [ ] Crash-free users percentage
- [ ] ANR rate
- [ ] User ratings and reviews
- [ ] Uninstall rate
- [ ] Installation statistics

## 📝 Version History Template

When updating CHANGELOG.md:

```markdown
## [X.Y.Z] - YYYY-MM-DD

### Added
- New feature description

### Changed
- Changed behavior description

### Fixed
- Bug fix description

### Removed
- Removed feature description

### Security
- Security fix description
```

## 📞 Emergency Contacts

- **Google Play Support:** [Play Console Help](https://support.google.com/googleplay/android-developer/)
- **GitHub Actions Issues:** Check workflow logs
- **App Owner:** saurabhkumar1432001@gmail.com

---

**Last Updated:** January 2026
