# Play Store Graphics Checklist

This document outlines all required graphic assets for the Expense Tracker Play Store listing.

## Required Assets

### App Icon
- [ ] `icon-512.png` - 512x512 PNG, 32-bit, no alpha/transparency
- **Location:** `play-store/graphics/icon-512.png`
- **Notes:** Must match the app's launcher icon design

### Feature Graphic
- [ ] `feature-graphic.png` - 1024x500 PNG
- **Location:** `play-store/graphics/feature-graphic.png`
- **Notes:** This appears at the top of your store listing. Use brand colors and highlight the app's main value proposition.

### Screenshots (Phone) - Required
Minimum 2, Maximum 8 screenshots per device type

| Screenshot | Screen | Description |
|------------|--------|-------------|
| [ ] `phone-1.png` | Home/Dashboard | Show financial summary cards and recent transactions |
| [ ] `phone-2.png` | Add Transaction | Demonstrate easy expense/income entry |
| [ ] `phone-3.png` | Budget Management | Display budget progress bars and categories |
| [ ] `phone-4.png` | Categories View | Show the category filtering system |
| [ ] `phone-5.png` | Filter System | Highlight smart filtering with chips |
| [ ] `phone-6.png` | Settings/Theme | Show dark mode and customization options |

- **Location:** `play-store/graphics/phone/`
- **Dimensions:** 16:9 or 9:16 aspect ratio, min 320px, max 3840px each side
- **Recommended:** 1080x1920 or 1440x2560 (portrait)

### Screenshots (7-inch Tablet) - Recommended
- [ ] `tablet-7-1.png` - Dashboard view
- [ ] `tablet-7-2.png` - Transaction entry
- [ ] `tablet-7-3.png` - Budget overview

- **Location:** `play-store/graphics/tablet-7/`
- **Dimensions:** 16:9 or 9:16 aspect ratio

### Screenshots (10-inch Tablet) - Recommended
- [ ] `tablet-10-1.png` - Dashboard view
- [ ] `tablet-10-2.png` - Transaction entry
- [ ] `tablet-10-3.png` - Budget overview

- **Location:** `play-store/graphics/tablet-10/`
- **Dimensions:** 16:9 or 9:16 aspect ratio

### Promo Video - Optional
- [ ] YouTube video URL
- **Location:** `play-store/metadata/en-US/video.txt`
- **Notes:** 30 seconds to 2 minutes recommended

## Asset Specifications Summary

| Asset Type | Dimensions | Format | Required |
|------------|------------|--------|----------|
| App Icon | 512x512 | PNG, 32-bit | ✅ Yes |
| Feature Graphic | 1024x500 | PNG | ✅ Yes |
| Phone Screenshots | 16:9 or 9:16 | PNG/JPEG | ✅ Min 2 |
| 7" Tablet Screenshots | 16:9 or 9:16 | PNG/JPEG | Recommended |
| 10" Tablet Screenshots | 16:9 or 9:16 | PNG/JPEG | Recommended |
| Promo Video | YouTube URL | - | Optional |

## Asset Creation Tips

### General Guidelines
1. **Use device frames** for a professional look (Android Studio, Figma, or online tools)
2. **Add marketing text overlays** to highlight key features
3. **Show the app's best features** - dashboard, budgeting, analytics
4. **Use consistent style** across all screenshots (same fonts, colors, positioning)
5. **Keep text readable** at small sizes on the Play Store
6. **Use high-quality images** - avoid blur or compression artifacts

### Recommended Tools
- **Android Studio:** Built-in screenshot capture with device frames
- **Figma:** Professional design tool with device mockup templates
- **Canva:** Easy-to-use with Play Store screenshot templates
- **Photoshop/GIMP:** Full control over graphics creation
- **Screenshots.pro:** Online tool for device frames

### Feature Graphic Tips
- Include the app icon prominently
- Use brand colors (#4CAF50 for income, #F44336 for expense)
- Add tagline: "Track expenses, manage budgets"
- Keep important elements in the center (may be cropped on edges)
- Avoid small text - won't be readable

### Screenshot Best Practices
1. **Screenshot 1:** Hook users with the main dashboard showing real data
2. **Screenshot 2:** Show the core action (adding an expense)
3. **Screenshot 3:** Highlight a differentiating feature (budgets)
4. **Screenshot 4-6:** Additional features (filtering, analytics, dark mode)

### Sample Screenshot Overlays
- "Track every expense 💰"
- "Set budgets, stay on track 📊"
- "Smart filtering made easy 🔍"
- "Beautiful dark mode 🌙"
- "100% offline & private 🔒"

## Directory Structure

Create this directory structure in `play-store/`:

```
play-store/
├── graphics/
│   ├── icon-512.png
│   ├── feature-graphic.png
│   ├── phone/
│   │   ├── phone-1.png
│   │   ├── phone-2.png
│   │   ├── phone-3.png
│   │   ├── phone-4.png
│   │   ├── phone-5.png
│   │   └── phone-6.png
│   ├── tablet-7/
│   │   ├── tablet-7-1.png
│   │   ├── tablet-7-2.png
│   │   └── tablet-7-3.png
│   └── tablet-10/
│       ├── tablet-10-1.png
│       ├── tablet-10-2.png
│       └── tablet-10-3.png
└── metadata/
    └── en-US/
        └── video.txt (YouTube URL, optional)
```

## Checklist Status

Use this section to track your progress:

### Icon & Feature Graphic
- [ ] App icon designed and exported at 512x512
- [ ] Feature graphic designed and exported at 1024x500

### Phone Screenshots
- [ ] Screenshot 1: Dashboard captured
- [ ] Screenshot 2: Add transaction captured
- [ ] Screenshot 3: Budget management captured
- [ ] Screenshot 4: Categories captured
- [ ] Screenshot 5: Filtering captured
- [ ] Screenshot 6: Dark mode/settings captured
- [ ] Device frames added to all screenshots
- [ ] Marketing text overlays added
- [ ] Final review completed

### Tablet Screenshots (if doing)
- [ ] 7-inch tablet screenshots captured and framed
- [ ] 10-inch tablet screenshots captured and framed

### Final Steps
- [ ] All images optimized for web (compressed)
- [ ] All images uploaded to Play Console
- [ ] Preview checked on various devices

## Resources

- [Google Play Store Asset Guidelines](https://support.google.com/googleplay/android-developer/answer/9866151)
- [Screenshot Requirements](https://support.google.com/googleplay/android-developer/answer/9866151#screenshots)
- [Feature Graphic Guidelines](https://support.google.com/googleplay/android-developer/answer/9866151#feature-graphic)
