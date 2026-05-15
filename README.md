# Organize Them 📅

**Organize Them** is a modern Android appointment scheduling application built with Jetpack Compose. It allows users to create booking links, manage their availability, and sync appointments directly with Google Calendar, similar to services like Calendly.

## 🚀 Features

- **Google Calendar Sync**: Real-time integration using Google Identity Services and Firebase Functions to check availability and prevent double-booking.
- **Smart Authentication**: Supports traditional Email/Password login and modern Google Sign-In via the Android Credential Manager API.
- **Availability Management**: Customise your working hours and duration for different types of booking links.
- **Seamless Booking**: A clean, guided flow for clients to book time slots without needing an account.
- **Modern UI**: Built entirely with Jetpack Compose using Material 3 design principles.
- **Robust Architecture**: Follows Clean Architecture patterns with Hilt for Dependency Injection and a stateless UI approach for high testability.

## 🛠 Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Dependency Injection**: [Hilt](https://dagger.dev/hilt/)
- **Backend**: [Firebase](https://firebase.google.com/)
    - Authentication (Email & Google)
    - Cloud Firestore (NoSQL Database)
    - Cloud Functions (Google Calendar logic)
- **Identity**: [Credential Manager API](https://developer.android.com/identity/credential-manager) & Google Identity Services
- **Architecture**: MVVM (Model-View-ViewModel) with State Hoisting

## 📦 Setup & Installation

### Prerequisites
- Android Studio Ladybug or newer.
- A Firebase Project.
- A Google Cloud Console Project (for Google Calendar API).

### Steps
1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/OrganizeThem.git
   ```
2. **Add Firebase**:
   - Place your `google-services.json` in the `app/` directory.
   - Enable Authentication (Email & Google) and Firestore in the Firebase Console.
3. **Configure Google API**:
   - Enable the **Google Calendar API** in your Google Cloud Console.
   - Add your Web Client ID to the project (found in `SignInScreen.kt` or `local.properties`).
4. **Build & Run**:
   - Sync Gradle and run the app on an emulator or physical device.

## 🧪 Testing

The project includes a comprehensive testing suite focusing on UI and state:
- **UI Tests**: Located in `app/src/androidTest/`. Run them using:
  ```bash
  ./gradlew connectedDebugAndroidTest
  ```
- **Verification**: Tests confirm UI state changes, loading indicators, and user interaction flows.

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
