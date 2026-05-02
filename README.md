# CreditPro — Android Kotlin App

A full-featured Credit Score & Financial Assistant app built with **Android Kotlin**, MVVM architecture, Room, Hilt, Navigation Component, and Firebase.

---

## 📁 Complete Project Structure

```
CreditProApp/
├── build.gradle                          # Root Gradle (plugins)
├── settings.gradle                       # Module config
├── gradle.properties
│
└── app/
    ├── build.gradle                      # App deps: Hilt, Room, Firebase, Maps, AdMob
    ├── proguard-rules.pro
    │
    └── src/main/
        ├── AndroidManifest.xml           # Activities, permissions, API keys
        │
        ├── java/com/creditpro/app/
        │   ├── CreditProApplication.kt   # Hilt app, Firebase init, notification channels
        │   │
        │   ├── activities/
        │   │   ├── SplashActivity.kt     # Animated splash → nav decision
        │   │   ├── OnboardingActivity.kt # 5-page ViewPager2: features, language, currency, profile
        │   │   └── MainActivity.kt       # NavHost + BottomNav + AdMob banner
        │   │
        │   ├── fragments/
        │   │   └── Fragments.kt          # All 14 fragments:
        │   │                             #  HomeFragment, CalculatorsFragment, CalcDetailFragment
        │   │                             #  CreditScoreFragment, SimulatorFragment, FullReportFragment
        │   │                             #  BankDirectoryFragment, MapViewFragment, TipsFragment
        │   │                             #  ProfileFragment, EditProfileFragment, SettingsFragment
        │   │                             #  NotificationsFragment
        │   │
        │   ├── adapters/
        │   │   └── Adapters.kt           # BankAdapter, CalculatorSectionAdapter,
        │   │                             # TipAdapter, FaqAdapter, NotificationAdapter,
        │   │                             # ResultDetailAdapter
        │   │
        │   ├── viewmodels/
        │   │   └── ViewModels.kt         # MainViewModel, CreditScoreViewModel,
        │   │                             # CalculatorViewModel, BankViewModel
        │   │
        │   ├── models/
        │   │   └── Models.kt             # User, CreditScore, Bank, Calculator,
        │   │                             # Tip, CalcResult, ReportQuestion, AppNotification
        │   │
        │   ├── repository/
        │   │   ├── Database.kt           # Room DB + UserDao + CreditScoreDao
        │   │   └── Repository.kt         # CreditProRepository + PreferencesRepository (DataStore)
        │   │
        │   ├── services/
        │   │   ├── DataService.kt        # 20 banks, 14 calculators, 8 tips, 6 FAQs,
        │   │   │                         # 10 report questions, all calculator engines
        │   │   └── Services.kt           # Firebase messaging, NotificationReceiver,
        │   │                             # TipNotificationWorker (WorkManager)
        │   │
        │   └── di/
        │       └── AppModule.kt          # Hilt DI: DB, DAOs, Repository, Prefs
        │
        └── res/
            ├── layout/                   # 25+ XML layouts
            │   ├── activity_splash.xml
            │   ├── activity_onboarding.xml
            │   ├── activity_main.xml
            │   ├── fragment_home.xml
            │   ├── fragment_calculators.xml
            │   ├── fragment_calc_detail.xml
            │   ├── fragment_credit_score.xml
            │   ├── fragment_simulator.xml
            │   ├── fragment_full_report.xml
            │   ├── fragment_bank_directory.xml
            │   ├── fragment_map_view.xml
            │   ├── fragment_tips.xml
            │   ├── fragment_profile.xml
            │   ├── fragment_edit_profile.xml
            │   ├── fragment_settings.xml
            │   ├── fragment_notifications.xml
            │   ├── fragment_onboarding_feature.xml
            │   ├── fragment_onboarding_selection.xml
            │   ├── fragment_onboarding_profile.xml
            │   ├── item_bank.xml
            │   ├── item_tip.xml
            │   ├── item_faq.xml
            │   ├── item_notification.xml
            │   ├── item_report_option.xml
            │   ├── item_result_detail.xml
            │   ├── item_input_field.xml
            │   ├── item_menu_row.xml
            │   ├── item_calc_section.xml
            │   ├── item_calculator_card.xml
            │   └── item_factor_row.xml
            │
            ├── navigation/nav_graph.xml  # Full nav graph with all 14 destinations
            ├── menu/bottom_nav_menu.xml
            ├── values/
            │   ├── colors.xml
            │   ├── strings.xml
            │   ├── themes.xml
            │   └── dimens.xml
            ├── drawable/                 # 25+ shape/vector drawables
            ├── color/nav_item_color.xml
            ├── anim/                     # slide_in/out_left/right, scale_in, fade_in
            └── xml/                      # backup_rules, data_extraction_rules
```

---

## 🚀 Setup Guide

### Step 1 — Open in Android Studio
1. Open **Android Studio Hedgehog (2023.1.1)** or newer
2. File → Open → select the `CreditProApp` folder
3. Wait for Gradle sync to finish

### Step 2 — Add JitPack (for MPAndroidChart)
In `settings.gradle`, add to `dependencyResolutionManagement.repositories`:
```groovy
maven { url 'https://jitpack.io' }
```

### Step 3 — Add fonts
Create `app/src/main/res/font/` and add:
- `sora_bold.ttf` — download from [Google Fonts: Sora](https://fonts.google.com/specimen/Sora)
- `dm_sans.ttf` — download from [Google Fonts: DM Sans](https://fonts.google.com/specimen/DM+Sans)

Or replace in `themes.xml` and layouts:
```xml
android:fontFamily="sans-serif"   <!-- replaces @font/sora_bold -->
```

### Step 4 — Firebase Setup
1. Go to [console.firebase.google.com](https://console.firebase.google.com)
2. Create project `creditpro-app`
3. Add Android app with package name `com.creditpro.app`
4. Download `google-services.json` → place in `app/`
5. Enable: **Analytics**, **Authentication**, **Crashlytics**, **Cloud Messaging**, **Remote Config**

### Step 5 — Google Maps API Key
1. Go to [console.cloud.google.com](https://console.cloud.google.com)
2. Enable: **Maps SDK for Android**, **Places API**
3. Create API key → restrict to your app's SHA-1
4. Replace in `AndroidManifest.xml`:
```xml
android:value="YOUR_GOOGLE_MAPS_API_KEY"
```

### Step 6 — AdMob Setup
1. Create account at [admob.google.com](https://admob.google.com)
2. Create app + Ad Units (Banner, Interstitial, Native)
3. Replace in `AndroidManifest.xml`:
```xml
android:value="ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"
```
4. Replace test ad unit IDs in layouts with your real IDs

### Step 7 — Permissions (already in Manifest)
- `INTERNET` — Firebase, Maps, Ads
- `ACCESS_FINE_LOCATION` — ATM locator
- `CALL_PHONE` — Bank directory call button
- `POST_NOTIFICATIONS` — Push alerts (Android 13+)

### Step 8 — Build & Run
```
Build → Make Project
Run → Run 'app'
```

---

## 🏗️ Architecture

```
View (Activities/Fragments)
    ↕  observe LiveData / call functions
ViewModel  (MainViewModel, CreditScoreViewModel, CalculatorViewModel, BankViewModel)
    ↕  suspend functions
Repository  (CreditProRepository)
    ↕                    ↕
Room DB (User,       DataStore Prefs
 CreditScore)        (language, currency,
                      onboarding, notifications)
```

**Key patterns used:**
- **MVVM** — ViewModels hold all state, Fragments only observe
- **Hilt** — Dependency injection throughout (`@HiltAndroidApp`, `@AndroidEntryPoint`, `@HiltViewModel`)
- **Room** — Local DB for User and CreditScore with LiveData queries
- **DataStore** — Preferences for language, currency, onboarding flag
- **Navigation Component** — Single-activity, all screens are fragments
- **WorkManager** — Daily tip notification scheduling
- **Coroutines** — All DB and async operations

---

## ✅ Features Checklist

### Onboarding
- [x] Animated splash with scale + fade
- [x] 5-page ViewPager2 onboarding
- [x] Language selection (8 languages) with ChipGroup
- [x] Currency selection (7 currencies)
- [x] Profile setup screen

### Home Dashboard
- [x] Gradient hero card with live credit score
- [x] Score progress bar (300–850)
- [x] 4 quick action buttons
- [x] Score factor rows with progress bars
- [x] Tips RecyclerView (3 latest)
- [x] Greeting with time of day

### Credit Score
- [x] Full score display with range chips (Poor/Fair/Good/V.Good/Exceptional)
- [x] Quick Check (opens Credit Karma)
- [x] Offline Simulator option
- [x] Full Report option

### Score Simulator
- [x] 5 reactive sliders (Payment, Utilization, Age, Inquiries, Mix)
- [x] Live score recomputation on every slider change
- [x] Dynamic improvement tips
- [x] Save scenario to Room DB

### Full Report Wizard
- [x] 10-step questionnaire with back navigation
- [x] Progress bar updates per step
- [x] Computed final score + result screen

### 14 Calculators
- [x] EMI, Loan Eligibility, Vehicle Loan, Prepayment
- [x] SIP, Mutual Funds, FD, RD, ROI, EPF
- [x] GST, Sales Tax, Salary, Score Boost
- [x] All compute real results with detailed breakdown

### Bank Directory
- [x] 20 global banks with search
- [x] Country filter chips
- [x] Tap-to-call customer care
- [x] ATM Map shortcut

### ATM & Branch Locator
- [x] Google Maps integration
- [x] Filter chips (All/ATM/Bank)
- [x] Sample markers on map
- [x] Horizontal nearby locations strip

### Tips & Insights
- [x] 8 tips with category filter
- [x] Expand/collapse on tap
- [x] FAQ accordion (6 questions)

### Profile
- [x] Stats grid (score, rating, age, accounts)
- [x] Edit Profile with name/email
- [x] Language & currency settings
- [x] Notifications toggle
- [x] Sign out with confirmation dialog

### Notifications
- [x] Firebase Cloud Messaging service
- [x] Daily tip WorkManager job
- [x] 3 notification channels (score, tips, reminders)
- [x] Notification screen with read/unread state

### Infrastructure
- [x] Hilt DI throughout
- [x] Room database with migrations
- [x] DataStore for preferences
- [x] ProGuard rules configured
- [x] Navigation graph with all 14 destinations + animations

---

## 🔧 Next Steps to Production

1. **Replace test AdMob IDs** with real ones before Play Store submission
2. **Add real Google Maps API key** (restrict to release SHA-1)
3. **Integrate real credit bureau API** (Experian, Equifax, or Credit Karma API)
4. **Add biometric auth** using `androidx.biometric:biometric`
5. **Implement AI chatbot** using Anthropic API for financial Q&A
6. **Add Plaid integration** for bank account linking and auto expense tracking
7. **Add gamification** — badges for credit milestones
8. **Localize strings** — move hardcoded strings to `strings.xml` per language
9. **Add unit tests** for DataService calculator logic
10. **Set up CI/CD** with GitHub Actions for automated builds
