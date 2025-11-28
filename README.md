# Mooney Charlie

Mooney Charlie is a personal finance tracking Android application designed to help users manage their budget, track expenses and income, and visualize their spending habits. Built with modern Android development practices using Kotlin and Jetpack Compose.

## Features

*   **Dashboard Overview**: Get a quick snapshot of your current balance, total income, and total expenses for the month.
*   **Transaction Tracking**: Easily record daily income and expense transactions with categories and notes.
*   **Spending Visualization**: View a breakdown of your spending habits through a dynamic donut chart categorized by spending types.
*   **Budget Management**: Set and monitor monthly budgets to stay on track financially.
*   **Transaction History**: Browse through past transactions to review financial activity.
*   **Dark & Light Theme**: Fully supports system-wide dark and light modes for comfortable viewing in any environment.

## Functionality

*   **Dashboard**
    The main dashboard provides a comprehensive overview of the user's financial status, including current balance, monthly income vs. expenses, and a donut chart visualizing spending categories.

*   **Transaction History**
    Users can view a detailed history of all transactions, grouped by date. Each entry displays the category, amount, and notes. Transactions can be edited or deleted directly from this list.

*   **Monthly Budget**
    Users can set a monthly budget limit. The app tracks daily spending against this budget, providing visual indicators and trend lines to help users stay within their limits.

*   **New Entry**
    A simple and intuitive form allows users to quickly add new income or expense entries, specifying the amount, category, date, and optional notes.

## Tech Stack

*   **Language**: [Kotlin](https://kotlinlang.org/)
*   **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
*   **Architecture**: MVVM (Model-View-ViewModel)
*   **Local Database**: [Room Persistence Library](https://developer.android.com/training/data-storage/room)
*   **Navigation**: [Navigation Compose](https://developer.android.com/guide/navigation/navigation-compose)
*   **Concurrency**: Kotlin Coroutines & Flow
*   **Dependency Injection**: Manual Dependency Injection (via AppContainer/Factory pattern)
*   **Charting**: Custom Compose Canvas implementations & MPAndroidChart

## Prerequisites

*   Android Studio (latest stable version recommended)
*   JDK 11
*   Android SDK Platform 36 (as specified in build config)
*   Min SDK: 26 (Android 8.0 Oreo)

## Setup & Installation

### Option 1: Command Line
1.  **Clone the repository**
    ```bash
    git clone https://github.com/yourusername/MooneyCharlie.git
    ```
2.  **Open in Android Studio**
    *   Launch Android Studio.
    *   Select "Open" and navigate to the cloned `MooneyCharlie` directory.

### Option 2: Android Studio (Get from VCS)
1.  Launch Android Studio.
2.  Select **Get from VCS** (or **File > New > Project from Version Control**).
3.  Enter the repository URL: `https://github.com/yourusername/MooneyCharlie.git`.
4.  Click **Clone**.

### Build & Run
1.  **Sync Gradle**
    *   Allow Android Studio to download dependencies and sync the project.
2.  **Run the App**
    *   Connect an Android device (via USB or WiFi) or start an Emulator.
    *   Click the "Run" button (green play icon) in the toolbar.

## Project Structure

The project follows a clean architecture approach:

*   `com.mooney.charlie`
    *   `data/`: Contains Room database entities (`Entry`, `Budget`), DAOs, and the Repository.
    *   `ui/theme/`: Theme definitions (Colors, Type, Shapes).
    *   `viewmodel/` (or root package): Contains ViewModels (`HomeViewModel`, `BudgetViewModel`, etc.) managing UI state.
    *   `MainActivity.kt`: App entry point and navigation host.
    *   `HomePage.kt`, `BudgetPage.kt`, `HistoryPage.kt`: Main screen composables.

## License

[Add License Here, e.g., MIT]
