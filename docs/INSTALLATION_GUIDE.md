# SAPS Forensic Engine - Installation Guide

## Quick Installation (For Field Officers)

### Step 1: Download the App

1. On your Android device, open a web browser
2. Go to: `https://github.com/Liamhigh/Liam-Highcock/releases`
3. Find the latest release
4. Tap on **SAPS-Forensic-Release.apk** to download

### Step 2: Enable Installation from Unknown Sources

**Android 8.0 and above:**
1. Open **Settings**
2. Go to **Apps** or **Apps & notifications**
3. Tap **Special app access**
4. Tap **Install unknown apps**
5. Find your browser (Chrome, etc.)
6. Enable **Allow from this source**

### Step 3: Install the App

1. Open your **Downloads** folder
2. Tap on **SAPS-Forensic-Release.apk**
3. Tap **Install**
4. Wait for installation to complete
5. Tap **Open** or find the app in your app drawer

### Step 4: Grant Permissions

When first opening the app, grant these permissions:

1. **Camera** - Required for capturing photo evidence
2. **Location** - Required for GPS tagging of evidence

## Device Requirements

| Requirement | Minimum |
|------------|---------|
| Android Version | 8.0 (Oreo) |
| Storage | 50 MB free |
| Camera | Required |
| GPS | Recommended |
| Internet | Not required |

## Troubleshooting

### "App not installed" Error

1. Make sure you have enough storage space
2. Uninstall any older versions first
3. Try downloading the APK again

### Camera Not Working

1. Go to Settings > Apps > SAPS Forensic Engine
2. Tap Permissions
3. Enable Camera permission

### GPS Location Not Available

1. Enable Location in device settings
2. Go outside for better GPS signal
3. Grant Location permission to the app

### App Crashes on Start

1. Restart your device
2. Reinstall the app
3. Check for available updates

## Security Notes

- **Do not** share the APK via unsecured channels
- **Do not** install APKs from unknown sources
- **Always** verify the APK hash before installation
- Keep your device updated with security patches

## IT Administrator Notes

### Recommended Deployment

For SAPS-wide deployment, we recommend:

1. **MDM Deployment** - Push via Mobile Device Management
2. **Enterprise App Store** - Host on internal distribution
3. **Hash Verification** - Verify APK integrity before deployment

### APK Signing

Release APKs are signed with the official signing key.
Verify signature before distributing to officers.

### Network Requirements

The app is **offline-first** and does not require:
- Internet connection
- Cloud services
- External servers

All data is stored locally on the device.

## Contact Support

For installation issues:
1. Open a GitHub issue
2. Include device model and Android version
3. Describe the error message

---

**SAPS Forensic Evidence Engine**  
*Verum Omnis Constitutional Governance*
