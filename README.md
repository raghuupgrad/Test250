# Calculator

A simple native Android calculator (Kotlin + ViewBinding). No Gradle wrapper is
shipped on purpose: GitHub Actions installs Gradle via `gradle/actions/setup-gradle`.

## Build the APK with GitHub Actions

1. Create a new GitHub repository and push the **contents of this folder** to the
   `main` branch (so `settings.gradle` sits at the repository root).
2. Add the workflow file at `.github/workflows/android.yml`
   (copy it from `android.yml` in the release, or paste the content from the README below).
3. Push, then check the **Actions** tab — the debug APK appears as an artifact
   named `calculator-debug-apk`.

## Build locally

Open the folder in Android Studio (Ladybug or newer), or run:

```bash
gradle assembleDebug
```

## Workflow file (.github/workflows/android.yml)

```yaml
name: Build Calculator APK

on:
  workflow_dispatch:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

permissions:
  contents: read

jobs:
  build:
    name: Build Android APK
    runs-on: ubuntu-latest
    steps:
      - name: Check out source code
        uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '17'

      - name: Install Gradle 8.9
        uses: gradle/actions/setup-gradle@v4
        with:
          gradle-version: '8.9'

      - name: Set up Android SDK
        uses: android-actions/setup-android@v3
        with:
          cmdline-tools-version: 11076708

      - name: Install Android SDK packages
        run: sdkmanager "platform-tools" "platforms;android-35" "build-tools;35.0.0"

      - name: Generate Gradle wrapper
        run: gradle wrapper --gradle-version 8.9

      - name: Grant execute permission for gradlew
        run: chmod +x ./gradlew

      - name: Build debug APK
        run: ./gradlew --no-daemon clean assembleDebug

      - name: Upload APK artifact
        uses: actions/upload-artifact@v4
        with:
          name: calculator-debug-apk
          path: app/build/outputs/apk/debug/app-debug.apk
          if-no-files-found: error
          retention-days: 14
```
