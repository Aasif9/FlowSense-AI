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

![Dashboard](docs/images/homepage.jpeg)

### 2) Transactions: filtered timeline of money movement
Transactions are grouped by date with quick filters (All/Debit/Credit/Auto), helping users audit income and expense flow quickly.

![Transactions](docs/images/transaction_list.jpeg)

### 3) Add Transaction: manual correction and control
Users can add or adjust entries with type selection, category, date, proof attachment, and notes. This supports correction when auto-detection is incomplete.

![Add Transaction](docs/images/add-transaction.jpeg)

### 4) Budgets: proactive alerts + forecasting
Budget screen highlights overspending alerts and displays AI-guided forecast cards (for example, projected savings and financial health status).

![Budgets](docs/images/ai_generated_budget_forecast_predictor.jpeg)

### 5) Settings: profile, security, export, and preferences
Settings screen centralizes account linking, security toggle, dark mode, notifications, and export options.

![Settings](docs/images/settings_page.jpeg)

## End-to-end data flow

1. App receives transaction signals (notifications / user input)
2. Text parsing extracts amount, merchant, payment channel, and timestamp
3. ML pipeline classifies category and normalizes merchant names
4. Room persists transactions and Flow updates UI in real time
5. Gemini module generates insight summaries and budget guidance


## Tech Stack

- **Mobile:** Kotlin, Jetpack Compose, Material 3, Navigation Compose
- **Architecture:** MVVM, Repository Pattern, Hilt
- **Storage:** Room, Kotlin Flow
- **AI/ML:** TensorFlow Lite, Gemini API
- **Background:** NotificationListenerService, WorkManager





