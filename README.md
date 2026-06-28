# html2apk BY Hekcer

> Convert any HTML/CSS/JS project or URL into a signed Android APK — no Android Studio, no Java, no setup.

html2apk is a self-contained Windows executable. Download it, run it, build your app. Everything needed — build tools, Java runtime, signing tools — is bundled inside.

---

## Download

**[⬇ Download html2apk.exe](../../releases/latest)**

No installation required. Just download and run.

---

## What it does

html2apk wraps the real Android SDK build pipeline (`aapt2`, `zipalign`, `apksigner`, `bundletool`) into a simple GUI. You provide your HTML/JS/CSS — either as a local folder or a URL — and it produces a properly built, signed Android app.

---

## Features

- **APK & AAB output** — sideload-ready APK, Play Store–ready AAB, or both at once
- **URL or local HTML** — point at a live website or bundle a local HTML project directly into the app
- **Real Android build tools** — identical output to a standard Android build, no shortcuts
- **Signed or debug-signed** — ready to install or submit immediately
- **Custom icon** — any PNG; all mipmap densities generated automatically
- **Screen orientation** — portrait, landscape, or unspecified
- **Permission toggles** — camera, media storage
- **Zoom & loading screen** — configure WebView behavior
- **Extra file bundling** — include additional assets alongside your HTML
- **Min / Target SDK** — Android 5.0 (API 21) through Android 14 (API 34)
- **No Java required** — bundled minimal JRE, fully self-contained

---

## Usage

1. **Download** `html2apk.exe` from [Releases](../../releases/latest)
2. **Run** it — no installation, no setup
3. Fill in your **App Settings** (name, package ID, URL or local HTML path)
4. Adjust SDK versions, orientation, permissions, and icon as needed
5. Check **Signed** for a release-signed APK
6. Check **Also AAB** if you also want an Android App Bundle
7. Click **⚒ Build APK**

Output files appear in an `output/` folder next to the exe:

```
output/
├── MyApp-v1.0-signed.apk
└── MyApp-v1.0-signed.aab   ← only if "Also AAB" was checked
```

---

## FAQ

**Does it need Java installed?**
No. The exe bundles its own minimal Java runtime.

**Does it need Android Studio or the Android SDK?**
No. All required build tools are embedded in the exe.

**Can I use my own signing keystore?**
The bundled debug keystore works for sideloading and testing. For Play Store publishing with your own key, see the source code — the signing step is straightforward to adapt.

**What's the difference between APK and AAB?**
APKs can be sideloaded directly onto a device. AABs are required for Google Play Store submission.

**What Android versions does the output support?**
By default, Min SDK 21 (Android 5.0) through Target SDK 36 (Android 16). Both are configurable in the GUI.

**Does it work on macOS or Linux?**
The exe targets Windows only.

MIT — see [LICENSE](LICENSE).
