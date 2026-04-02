# FlowSenseAI

AI-powered personal finance manager for Android that automates transaction tracking and turns raw payment data into actionable insights.

## What the app does

FlowSenseAI captures transactions from multiple payment sources, categorizes them, and surfaces insights through a simple mobile experience.

- Android app built with **Kotlin + Jetpack Compose + MVVM**
- Local persistence using **Room + Flow** for reactive updates
- AI/ML-assisted categorization using **TensorFlow Lite**
- Insight generation using **Gemini API**

## App Workflow (based on current UI screens)

### 1) Dashboard: financial snapshot + category insights
The dashboard gives a monthly summary (net balance, total spent, total income) and a category-level spending breakdown.

![Dashboard](docs/images/01-dashboard.png)

### 2) Transactions: filtered timeline of money movement
Transactions are grouped by date with quick filters (All/Debit/Credit/Auto), helping users audit income and expense flow quickly.

![Transactions](docs/images/02-transactions.png)

### 3) Add Transaction: manual correction and control
Users can add or adjust entries with type selection, category, date, proof attachment, and notes. This supports correction when auto-detection is incomplete.

![Add Transaction](docs/images/03-add-transaction.png)

### 4) Budgets: proactive alerts + forecasting
Budget screen highlights overspending alerts and displays AI-guided forecast cards (for example, projected savings and financial health status).

![Budgets](docs/images/04-budgets.png)

### 5) Settings: profile, security, export, and preferences
Settings screen centralizes account linking, security toggle, dark mode, notifications, and export options.

![Settings](docs/images/05-settings.png)

## End-to-end data flow

1. App receives transaction signals (notifications / user input)
2. Text parsing extracts amount, merchant, payment channel, and timestamp
3. ML pipeline classifies category and normalizes merchant names
4. Room persists transactions and Flow updates UI in real time
5. Gemini module generates insight summaries and budget guidance

## Resume-ready project highlights (balanced Android + AI/ML)

- Engineered a real-time transaction ingestion flow with `NotificationListenerService`, persisting data using Room and rendering reactive screens through Flow.
- Built on-device transaction categorization with TensorFlow Lite using text preprocessing, feature extraction, and confidence-based prediction.
- Implemented merchant normalization using string similarity + rule refinement to improve consistency in analytics.
- Integrated Gemini-based insight generation with anomaly checks to provide personalized budget and spending recommendations.

## Tech Stack

- **Mobile:** Kotlin, Jetpack Compose, Material 3, Navigation Compose
- **Architecture:** MVVM, Repository Pattern, Hilt
- **Storage:** Room, Kotlin Flow
- **AI/ML:** TensorFlow Lite, Gemini API
- **Background:** NotificationListenerService, WorkManager

## Local setup

1. Clone the repo
2. Follow manual resource setup in [`MANUAL_SETUP.md`](MANUAL_SETUP.md)
3. Build and run the app in Android Studio

## Screenshot setup for GitHub

To make the above images render on GitHub, place your screenshots in:

- `docs/images/01-dashboard.png`
- `docs/images/02-transactions.png`
- `docs/images/03-add-transaction.png`
- `docs/images/04-budgets.png`
- `docs/images/05-settings.png`

(You can use the screenshots you shared and rename them to these filenames.)
