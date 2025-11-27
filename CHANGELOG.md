# Changelog

All notable changes to the Expense Tracker project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.2.0] - 2025-11-28

### Changed
- 🔄 **CI/CD Improvements** - Fixed release workflow path triggers to include workflow file changes
- 📝 **Issue Templates** - Updated bug report and feature request templates for Android-specific information
- 🧹 **Codebase Cleanup** - Removed stale branches and streamlined workflows

### Fixed
- 🐛 Release workflow now triggers correctly when the workflow file itself is modified

## [2.1.0] - 2025-11-28

### Added
- 📤 **CSV Export** - Export all transactions to CSV file with full details
- 📊 **Export Summary** - View total income, expenses, and balance before exporting
- 📅 **Monthly Budget System** - Enhanced budget management with month-specific tracking
- 🔄 **CI/CD Pipeline** - Automated code quality checks on every PR
  - KtLint for Kotlin code style enforcement
  - Detekt for static code analysis
  - Android Lint for best practices
- 🚀 **Production Release Workflow** - Automated APK building with:
  - APK signing support (when keys configured)
  - Automatic changelog extraction
  - SHA-256 hash for APK verification
  - Version-named APK files (ExpenseTracker-v2.1.0.apk)
- 📋 **PR Templates** - Standardized pull request workflow
- 🔒 **CodeQL Security Scanning** - Automated security vulnerability detection

### Changed
- 🔄 Code formatting improvements across all Kotlin files
- 🔄 Import statements organized alphabetically
- 🔄 Consistent whitespace and indentation

### Technical
- 📦 Added ktlint (v11.6.0) Gradle plugin
- 📦 Added detekt (v1.23.6) Gradle plugin
- 📦 Created lint-baseline.xml for existing lint issues
- 📦 Added .editorconfig for consistent formatting
- 📦 Created detekt.yml configuration
- 📦 GitHub Actions workflows for CI/CD

## [2.0.0] - 2025-10-02

### Added
- ✨ Category system with 10 default categories (Food, Transport, Shopping, Bills, Entertainment, Health, Education, Travel, Groceries, Other)
- ✨ Category management (add, edit, delete custom categories)
- ✨ Category-based transaction filtering
- ✨ Budget management feature with monthly limits per category
- ✨ Budget progress tracking with visual indicators
- ✨ Home screen widget showing daily, weekly, and monthly expenses
- ✨ Individual filter chips with one-tap removal
- ✨ "Clear All Filters" button for quick filter reset
- ✨ Context-aware empty states (different messages for no data vs no filter results)
- ✨ Comprehensive responsive design system for all screen sizes (320dp to 720dp+)
- ✨ Dimension resources for 5 screen size breakpoints
- ✨ Auto-sizing text for balance displays
- 🎨 Material Design 3 complete implementation
- 🌓 Dark mode support with system theme integration
- ♿ Accessibility improvements (content descriptions, proper touch targets)

### Changed
- 🔄 Complete UI overhaul with Material Design 3
- 🔄 Modernized onboarding experience
- 🔄 Enhanced settings screen with better organization
- 🔄 Improved transaction list with category indicators
- 🔄 Better filter dialog showing all defined categories
- 🔄 Filter chips now remain visible even when no results match
- 🔄 Optimized layouts for Samsung S23 and other modern devices
- 🔄 Payment method management with drag-to-reorder
- 🔄 Enhanced transaction adapter with category colors

### Fixed
- 🐛 Samsung S23 layout issues (header too tall, text too large, items cut off)
- 🐛 Filter dialog now shows all defined categories, not just used ones
- 🐛 Filter UX issue where users couldn't clear filters with no results
- 🐛 Transaction header disappearing when filters had no matches
- 🐛 Category text being cut off in transaction items
- 🐛 Add expense dialog requiring scroll on small screens
- 🐛 Settings footer buttons not splitting 50/50

### Technical
- 📦 Migrated from CSV storage to SharedPreferences with JSON serialization
- 📦 Added CategoryUtils for color management
- 📦 Implemented FilterAdapter for improved filter UI
- 📦 Created ExpenseWidgetProvider for home screen widget
- 📦 Added BudgetManagementActivity for budget tracking
- 📦 Complete dimension resource system across 5 screen sizes

## [1.0.0] - 2025-08-10

### Added
- 🎉 Initial release
- ✅ Basic income and expense tracking
- ✅ Payment method management (Cash, UPI, Cards, Bank Transfer)
- ✅ Transaction list with edit and delete functionality
- ✅ Transaction filtering by payment method
- ✅ Search functionality for transactions
- ✅ Financial summary cards (Balance, Income, Expense)
- ✅ Detailed balance view (tap to see breakdown)
- ✅ Settings screen for payment method configuration
- ✅ Onboarding wizard for first-time setup
- ✅ Data persistence using SharedPreferences
- ✅ 100% offline functionality
- ✅ Material Design components
- ✅ Light and dark theme support

### Technical
- 📦 Built with Kotlin
- 📦 Material Design components library
- 📦 RecyclerView for transaction list
- 📦 SharedPreferences for data storage
- 📦 Minimum SDK 24 (Android 7.0)
- 📦 Target SDK 34 (Android 14)

---

## Release Notes

### v2.0.0 Highlights

**Major Features:**
This release represents a complete overhaul of the app with professional-grade features:

1. **Category System** - Organize transactions into meaningful categories with custom colors
2. **Budget Management** - Set and track monthly budgets with visual progress indicators
3. **Home Screen Widget** - Quick glance at expenses without opening the app
4. **Responsive Design** - Perfect display on all devices from small phones to large tablets
5. **Enhanced Filtering** - Filter by payment method AND category with visual chip indicators

**User Experience:**
- Smoother, more intuitive navigation
- Clear visual feedback for all actions
- Professional Material Design 3 appearance
- No more getting "stuck" with filters
- Faster access to common actions

**Performance:**
- Lightweight and fast
- Optimized for all screen sizes
- Smooth animations and transitions
- Efficient data management

### Upgrade Notes

**From v1.0 to v2.0:**
- All existing data is automatically migrated
- New categories are added with "Other" as default for existing transactions
- Payment methods are preserved
- No action required from users

**Clean Install:**
- Complete onboarding with payment method and category selection
- Quick setup in under 1 minute
- Sample data not included (start fresh)

---

## Download

**Latest Release:** [v2.0.0](https://github.com/saurabhkumar1432/expenseTracker/releases/latest)

**Requirements:**
- Android 7.0 (API 24) or higher
- ~2 MB storage space
- No internet connection required

**Installation:**
1. Download `app-debug.apk` from releases
2. Enable "Install from Unknown Sources" in Android settings
3. Open the APK file and follow installation prompts
4. Launch and complete the quick onboarding

---

## Contributing

See [README.md](README.md) for contribution guidelines.

## Support

For issues and feature requests, please use [GitHub Issues](https://github.com/saurabhkumar1432/expenseTracker/issues).
