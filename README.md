# Windows Mouse Controller

Android Studio project for a Windows-style on-screen pointer with a touchpad overlay.

## Build
1. Open this folder in Android Studio.
2. Allow Gradle sync (Android Gradle Plugin 8.2.2; Gradle 8.2.x; JDK 17).
3. Build > Build APK(s).
4. Install the debug APK from `app/build/outputs/apk/debug/`.

## First run
1. Open the app.
2. Enable **Windows Mouse Controller** under Android Settings > Accessibility.
3. Grant **Display over other apps** permission.
4. Return to the app and tap **Start Mouse Touchpad**.
5. Use the touchpad area to move the pointer. Use LEFT, RIGHT, DOUBLE, and scroll controls.

## Important limitations
- Android does not expose a normal third-party API for injecting a physical mouse device. This project draws a Windows-style pointer overlay and uses the user-enabled Accessibility Service to perform taps and swipe-based scrolling.
- Android versions and individual apps may restrict gesture injection or overlay behavior. Some secure screens, system dialogs, games, and protected apps may not respond.
- The RIGHT control currently performs a short accessibility tap at the pointer position; Android Accessibility does not provide a universal right-click event. It is a placeholder gesture, not guaranteed native context-menu behavior.
- This is source code, not a prebuilt or tested APK. Build and test on a real device before relying on it.

## Build APK on GitHub
1. Create a new GitHub repository.
2. Upload the contents of this project ZIP to the repository root (not the ZIP file itself).
3. Open the **Actions** tab and select **Build Android APK**.
4. Click **Run workflow** (or push to `main`/`master`).
5. When the run finishes, open the workflow run and download the `WindowsMouseController-debug` artifact.
