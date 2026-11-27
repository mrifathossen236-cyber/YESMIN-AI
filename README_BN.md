# SR 1 AI — Android Assistant

প্রথম সংস্করণ: Android APK (নিজের ফোনে ব্যবহারের জন্য)।

## এই MVP-তে যা আছে
- Google AI Studio / Gemini API key অ্যাপের ভেতরে সংরক্ষণ
- Gemini text chat
- ফোনের speech recognizer দিয়ে বাংলা/ইংরেজি voice input (push-to-talk)
- Android Text-to-Speech দিয়ে উত্তর শোনা
- ইনস্টল করা অ্যাপের নাম লিখে/বলে অ্যাপ খোলা
- Accessibility Service চালু করলে focused editable field-এ AI-এর সর্বশেষ উত্তর বসানো
- Google Search খোলা

## পরের ধাপে যোগ করতে হবে
- প্রকৃত real-time, দুই-মুখী voice call (Gemini Live API)
- MediaProjection দিয়ে সম্মতিসহ screen capture এবং screen image Gemini-তে পাঠানো
- আরও নিরাপদ API-key storage / optional backend proxy
- আরও নির্ভরযোগ্য app-action intent parsing ও confirmation UI

**গুরুত্বপূর্ণ:** এই MVP-তে “Speak” হলো push-to-talk, সবসময় চালু থাকা call নয়। Screen capture এখনো অন্তর্ভুক্ত নয়। Accessibility কেবল তখনই text বসাতে পারবে যখন ব্যবহারকারী Android Settings থেকে SR 1 AI service নিজে চালু করবেন এবং কোনো editable text field focus করা থাকবে। কোনো permission নীরবে চালু হয় না।

## Gemini API
1. https://aistudio.google.com/apikey থেকে নিজের API key তৈরি করুন।
2. APK খুলে key বসিয়ে Save করুন।
3. অ্যাপ বর্তমানে `gemini-3.8-flash` মডেল ব্যবহার করে। Google-এর model availability ও rate limit account/tier অনুযায়ী বদলাতে পারে।
4. API key কাউকে দেবেন না, public repository-তে রাখবেন না। এই app key-টি এই ডিভাইসের private app preferences-এ রাখে; ফোনে অ্যাপের ডেটা সুরক্ষিত রাখুন।

## GitHub Actions দিয়ে APK build
1. ZIP extract করে project-এর ভেতরের সব ফাইল repository root-এ upload করুন।
2. GitHub → Actions → **SR 1 AI Android APK** → Run workflow।
3. সফল হলে workflow run-এর Artifacts থেকে `SR-1-AI-debug-APK` download করুন।
4. ZIP extract করে `app-debug.apk` install করুন।

Workflow Android SDK licenses accept করে তারপর SDK 35 install করে।

## Local build
JDK 17, Android SDK Platform 35, Build Tools 35.0.0 এবং Gradle 8.9 প্রয়োজন:
```bash
gradle --no-daemon assembleDebug
```
APK: `app/build/outputs/apk/debug/app-debug.apk`
