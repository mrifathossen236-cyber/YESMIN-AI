#!/data/data/com.termux/files/usr/bin/bash
set -e
echo "SR AI build helper"
echo "This requires a working Android SDK, Build Tools, JDK 17 and Gradle."
if ! command -v gradle >/dev/null 2>&1; then
  echo "Gradle not found. Install/configure Gradle and Android SDK first."
  exit 1
fi
if [ -z "${ANDROID_HOME:-}" ] && [ -z "${ANDROID_SDK_ROOT:-}" ]; then
  echo "Set ANDROID_HOME or ANDROID_SDK_ROOT to your Android SDK path."
  exit 1
fi
gradle --no-daemon assembleDebug
echo "APK: app/build/outputs/apk/debug/app-debug.apk"
