[![CI](https://github.com/USERNAME/expenseTracker/actions/workflows/ci.yml/badge.svg)](https://github.com/USERNAME/expenseTracker/actions/workflows/ci.yml)
[![Release](https://img.shields.io/github/v/release/USERNAME/expenseTracker?include_prereleases)](https://github.com/USERNAME/expenseTracker/releases)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

# 💰 Expense Tracker

A modern, fully offline Android expense tracker with Material Design 3, featuring intelligent filtering, budget management, and home screen widget support.

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Material Design 3](https://img.shields.io/badge/UI-Material%20Design%203-orange.svg)
![Offline](https://img.shields.io/badge/Mode-Offline-red.svg)
[![Release](https://img.shields.io/github/v/release/saurabhkumar1432/expenseTracker)](https://github.com/saurabhkumar1432/expenseTracker/releases/latest)
[![Download](https://img.shields.io/github/downloads/saurabhkumar1432/expenseTracker/total)](https://github.com/saurabhkumar1432/expenseTracker/releases)
[![CI](https://github.com/saurabhkumar1432/expenseTracker/actions/workflows/pr-check.yml/badge.svg)](https://github.com/saurabhkumar1432/expenseTracker/actions/workflows/pr-check.yml)

## 📥 Download

### Google Play Store (Recommended)

<a href="https://play.google.com/store/apps/details?id=com.saurabhkumar.expensetracker">
  <img alt="Get it on Google Play" src="https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png" width="200"/>
</a>

*Coming soon to Google Play Store*

### Direct Download (APK)

**[⬇️ Download Latest APK](https://github.com/saurabhkumar1432/expenseTracker/releases/latest)**

**Quick Install:**
1. Download `expenseTracker-vX.X.X.apk` from the latest release
2. Enable "Install from Unknown Sources" in your Android settings
3. Open the APK and install
4. Launch the app and complete the quick onboarding!

### Build from Source

See [Getting Started](#-getting-started) section for build instructions.

**Requirements:**
- Android 7.0 (API 24) or higher
- ~5 MB storage space
- No internet connection required

**Privacy:** Your data stays on your device. See our [Privacy Policy](play-store/PRIVACY_POLICY.md) for details.

## ✨ Features

### Core Functionality
- 📊 **Income & Expense Tracking** - Track all your financial transactions
- 💳 **Payment Methods** - Manage multiple payment methods (Cash, UPI, Cards, etc.)
- 🏷️ **Categories** - 10 default categories with custom colors (Food, Transport, Shopping, etc.)
- 🔍 **Smart Filtering** - Filter by payment method, category, search, and date range
- 💵 **Budget Management** - Set monthly budgets per category with progress tracking
- 📤 **CSV Export** - Export all transactions to CSV with summary statistics
- 📱 **Home Screen Widget** - Quick view of daily, weekly, and monthly expenses
- 🌓 **Dark Mode** - Full dark theme support with system integration

### User Experience
- ✅ **Fully Responsive** - Optimized for all screen sizes (phones to tablets)
- ✅ **Material Design 3** - Modern, clean interface following latest design guidelines
- ✅ **Individual Filter Chips** - See and remove active filters with one tap
- ✅ **Context-Aware Empty States** - Clear messages when filters have no results
- ✅ **Transaction Management** - Edit, delete, and search transactions easily
- ✅ **Detailed Balance View** - Tap balance to see breakdown (Income, Expense, Balance)

### Technical Highlights
- 📴 **100% Offline** - No internet required, all data stored locally
- 🚀 **Lightweight** - No heavy databases, uses SharedPreferences + JSON
- 🎨 **Adaptive UI** - Automatic dimension scaling across devices
- ♿ **Accessible** - Content descriptions and proper touch targets (48dp+)
- 🔄 **Data Persistence** - Reliable data storage with backward compatibility

## 📱 Screenshots

### Main Dashboard
- Financial summary cards (Balance, Income, Expense)
- Transaction list with category colors
- Individual filter chips with quick removal
- Search functionality

### Features
- Category-based filtering with visual indicators
- Budget progress tracking
- Payment method management with drag-to-reorder
- Modern onboarding experience
- Settings with theme selection

## 🚀 Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17 or higher
- Android SDK 24+ (Android 7.0+)
- Target SDK 34 (Android 14)

### Installation

#### Windows (PowerShell)
```powershell
# Clone the repository
git clone https://github.com/saurabhkumar1432/expenseTracker.git
cd expenseTracker

# Build the app
.\gradlew assembleDebug

# Install on connected device/emulator
.\gradlew installDebug
```

#### Linux/macOS (Terminal)
```bash
# Clone the repository
git clone https://github.com/saurabhkumar1432/expenseTracker.git
cd expenseTracker

# Build the app
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug
```

### First Run
1. Complete the onboarding wizard
2. Select your preferred payment methods
3. Choose categories you want to track
4. Start adding transactions!

## 🏗️ Architecture

### Tech Stack
- **Language:** Kotlin 100%
- **UI Framework:** Android XML with Material Design 3
- **Architecture Pattern:** MVVM-inspired with data layer separation
- **Data Storage:** SharedPreferences with JSON serialization
- **Build System:** Gradle with Kotlin DSL

### Project Structure
```
app/src/main/
├── java/com/example/expensetracker/
│   ├── MainActivity.kt                    # Main dashboard
│   ├── SettingsModernActivity.kt          # Settings & management
│   ├── BudgetManagementActivity.kt        # Budget tracking
│   ├── OnboardingModernActivity.kt        # First-time setup
│   ├── data/
│   │   ├── Models.kt                      # Data models
│   │   ├── TransactionStore.kt            # Transaction management
│   │   └── Prefs.kt                       # Data persistence
│   ├── ui/
│   │   ├── TransactionAdapter.kt          # Transaction list
│   │   ├── CategoryUtils.kt               # Category helpers
│   │   └── FilterAdapter.kt               # Filter UI
│   └── widget/
│       └── ExpenseWidgetProvider.kt       # Home screen widget
└── res/
    ├── layout/                            # UI layouts
    ├── values/                            # Base dimensions & styles
    ├── values-sw320dp/                    # Small phones
    ├── values-sw360dp/                    # Standard phones
    ├── values-sw600dp/                    # Tablets
    ├── values-sw720dp/                    # Large tablets
    └── values-night/                      # Dark mode
```

### Data Models
```kotlin
// Transaction
data class Transaction(
    val amount: Double,
    val note: String,
    val isCredit: Boolean,      // true = income, false = expense
    val timestamp: Long,
    val paymentMethod: String,
    val category: String = "Other"
)

// Budget
data class Budget(
    val category: String,
    val monthlyLimit: Double
)

// Financial Summary
data class FinancialSummary(
    val totalCredit: Double,
    val totalDebit: Double,
    val balance: Double
)
```

## 🎨 Design System

### Responsive Breakpoints
| Screen Size | Width | Examples | Use Case |
|------------|-------|----------|----------|
| Small | 320dp | Budget phones | Compact layout |
| Standard | 360dp-414dp | Samsung S23, Pixel 6 | Standard phones |
| Tablet | 600dp-720dp | 7-10" tablets | Spacious layout |
| Large Tablet | 720dp+ | iPad Pro, Galaxy Tab | Maximum space |

### Color Scheme
- **Primary:** Material Design 3 dynamic colors
- **Credit/Income:** Green (#4CAF50)
- **Debit/Expense:** Red (#F44336)
- **Category Colors:** 10 distinct colors for visual differentiation

### Typography Scale
- Display: 32-48sp (Large balance display)
- Headline: 24-28sp (Screen titles)
- Title: 16-24sp (Section headers)
- Body: 14-18sp (Content)
- Caption: 11-16sp (Metadata)

## 🔧 Configuration

### Data Storage
All data is stored locally using SharedPreferences:
- **Transactions** - JSON serialized list
- **Payment Methods** - JSON array
- **Categories** - JSON array  
- **Budgets** - JSON serialized list
- **Filter State** - Persisted selections
- **Theme Preference** - Light/Dark/System

### Default Categories
1. 🍔 Food
2. 🚗 Transport
3. 🛍️ Shopping
4. 💡 Bills
5. 🎬 Entertainment
6. 🏥 Health
7. 📚 Education
8. ✈️ Travel
9. 🛒 Groceries
10. 📦 Other

### Default Payment Methods
- 💵 Cash
- 📱 UPI
- 💳 Credit Card
- 💳 Debit Card
- 🏦 Bank Transfer

## 📊 Key Features Details

### Smart Filtering
- **Multi-select:** Choose multiple payment methods and categories
- **Search:** Find transactions by note/description
- **Date Range:** Filter by custom date ranges
- **Individual Chips:** See active filters as chips with one-tap removal
- **Clear All:** Quick button to remove all filters
- **Persistent:** Filters are remembered between sessions

### Budget Management
- Set monthly limits per category
- Visual progress bars showing budget usage
- Color-coded warnings (green → yellow → red)
- Current month spending display
- Easy budget editing and removal
- Budget exceeded alerts

### Home Screen Widget
- Daily expense summary
- Weekly expense summary
- Monthly expense summary
- Auto-refresh on data changes
- Tap to open app
- Material Design styling

## 🧪 Testing

### Manual Testing Checklist
- [ ] Add income transaction
- [ ] Add expense transaction
- [ ] Edit existing transaction
- [ ] Delete transaction
- [ ] Filter by payment method
- [ ] Filter by category
- [ ] Search transactions
- [ ] Clear filters when no results
- [ ] Set budget for category
- [ ] Exceed budget and verify warning
- [ ] Change theme (Light/Dark/System)
- [ ] Add/edit/delete payment methods
- [ ] Add/edit/delete categories
- [ ] Verify widget updates
- [ ] Test on different screen sizes

### Device Compatibility
✅ Tested on:
- Samsung S23 (360dp)
- Pixel 6 (412dp)
- Small phones (320dp emulator)
- Tablets (600dp+ emulator)

## 🐛 Known Issues & Fixes

### Fixed Issues
✅ **Samsung S23 Layout** - Header too tall, text cut off → Fixed with responsive dimensions  
✅ **Filter Category Bug** - Only showed used categories → Fixed to show all defined categories  
✅ **Filter UX Issue** - Users stuck when filters had no results → Fixed with persistent filter chips and "Clear All" button  
✅ **Responsive Design** - Inconsistent sizing across devices → Fixed with comprehensive dimension system  

## 🤝 Contributing

This is a personal project, but suggestions and feedback are welcome!

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is for personal use and learning purposes.

## 👤 Author

**Saurabh Kumar**
- GitHub: [@saurabhkumar1432](https://github.com/saurabhkumar1432)

## 🙏 Acknowledgments

- Material Design 3 guidelines by Google
- Android development community
- GitHub Copilot for development assistance

## 📈 Version History

### v2.2.0 (November 2025)
- 🔄 **CI/CD Improvements** - Fixed release workflow path triggers
- 📝 **Issue Templates** - Updated for Android-specific bug reports
- 🧹 **Codebase Cleanup** - Streamlined workflows and templates

### v2.1.0 (November 2025)
- 📤 **CSV Export** - Export transactions to CSV with summary
- 📅 **Monthly Budgets** - Enhanced budget system with month tracking
- 🔄 **CI/CD Pipeline** - Automated testing with ktlint, detekt, lint
- 🚀 **Production Releases** - Automated APK signing and changelog
- 🔒 **Security Scanning** - CodeQL integration for vulnerability detection

### v2.0.0 (October 2025)
- ✨ Added category system with 10 default categories
- ✨ Implemented budget management feature
- ✨ Added home screen widget
- ✨ Complete responsive design overhaul
- ✨ Individual filter chips with one-tap removal
- 🐛 Fixed filter UX issues
- 🎨 Material Design 3 upgrade
- 🌓 Dark mode support

### v1.0.0 (August 2025)
- 🎉 Initial release
- ✅ Basic income/expense tracking
- ✅ Payment method management
- ✅ Transaction filtering
- ✅ Settings and onboarding

## 🔮 Future Enhancements

Potential features for future versions:
- 📊 Charts and analytics
- 📅 Recurring transactions
- 📄 Export to PDF
- ☁️ Optional cloud backup
- 🔔 Budget notifications
- 💱 Multi-currency support
- 👥 Multi-user profiles

## 📞 Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Email: saurabhkumar1432001@gmail.com

---

**Built with ❤️ using Kotlin and Material Design 3**
