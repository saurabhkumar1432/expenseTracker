# Ultra-Light Expense Tracker (Offline)

Features:
- One-time onboarding to enter payment modes (stored in SharedPreferences)
- Add expense: amount, optional reason, mode (spinner)
- CSV (tab separated) storage in internal storage (no DB) for minimal footprint
- Simple summary (count + total)
- Edit modes later in Settings
- Completely offline

Build / Run (Windows PowerShell):
1. Generate Gradle wrapper if missing:
   gradle wrapper
2. Build:
   .\gradlew assembleDebug
3. Install (device/emulator connected):
   .\gradlew installDebug

Data Location:
- Modes: SharedPreferences (expense_prefs)
- Expenses: filesDir/expenses.csv (tab-separated: id,amount,reason,mode,time)

Notes:
- To reset app: clear app storage or uninstall.
- CSV kept append-only; for very large usage consider pruning or migrating to Room.
- Min SDK 24, target 34.
