# Repeats (Android)

Repeats is a free, open-source application designed to help you learn efficiently using the spaced repetition method. It is available for Android (this repository) and Windows 10.

## How it works?
The app actively helps you learn by sending you periodic notifications containing questions from your study sets. You can answer directly from the notification; if you get it wrong, the app will show you the correct answer. It's a great way to memorize vocabulary, dates, or any other information without actively opening the app!

## Features

* **Background Learning:** Answer questions directly from periodic push notifications.
* **Text Recognition (OCR):** Easily create new study sets by taking photos of your notes or textbooks (powered by Firebase ML Vision).
* **Repeats Community:** Share your study sets with others or download sets created by the community.
* **Fast Learning Mode:** A dedicated mode for intense, focused study sessions.
* **Read Aloud:** Listen to your study sets using the background audio service.
* **Statistics:** Track your progress and see how well you know specific sets.
* **Highly Customizable:** Manage your reminders, set up "Silence Hours" to prevent notifications at night, and adjust the frequency of questions.

## Download App

You don't need to compile it yourself. You can download the official, ready-to-use version here:
* [Repeats on Google Play](https://play.google.com/store/apps/details?id=com.rootekstudio.repeatsandroid)
* [Releases](https://github.com/jakub-sieradzki/RepeatsJava/releases) tab on this repo

## Tech Stack & Libraries

This project is built natively for Android using **Java** and uses the following major technologies:
* **Minimum SDK:** Android 5.0 (API 21)
* **Target SDK:** Android 14 (API 34)
* **Firebase:** Firestore (for the community features), ML Vision (for text recognition/OCR), and Dynamic Links.
* **Microsoft App Center:** For crash reporting and analytics.
* **UI Components:** AndroidX, Material Design Components, Applandeo Material Calendar View.

## How to Compile / Build from Source

If you want to contribute, review the code, or build the app yourself, follow these steps:

### Prerequisites
* Android Studio (Latest stable version recommended)
* JDK 17 (or newer, required by newer Gradle versions)

### Steps
1. Clone the repository:
```
    git clone https://github.com/jakub-sieradzki/repeatsjava.git
```
2. Open the cloned directory in Android Studio.

3. Let Gradle sync the project and download all necessary dependencies.

4. (Note: Because this project uses Firebase, you might need your own google-services.json file placed in the app/ directory if you plan to fork it and use your own backend databases for the Community/ML features).

5. Click the Run button (green play icon) in Android Studio to build and install the app on a connected device or emulator.

Alternatively, you can build the APK via the command line using the Gradle wrapper:

On Windows:
    gradlew.bat assembleDebug

On macOS/Linux:
    ./gradlew assembleDebug

The compiled APK will be located in app/build/outputs/apk/debug/.
