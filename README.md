# SAPS Forensic Evidence Engine

![Verum Omnis Logo](docs/images/main-logo.png)

[![Build SAPS Forensic APK](https://github.com/Liamhigh/Liam-Highcock/actions/workflows/build-apk.yml/badge.svg)](https://github.com/Liamhigh/Liam-Highcock/actions/workflows/build-apk.yml)

**A free forensic evidence collection application for the South African Police Service (SAPS)**

## 🔒 Overview

The SAPS Forensic Evidence Engine is a mobile application designed for law enforcement officers to collect, seal, and document forensic evidence. Built with constitutional governance principles, this app ensures evidence integrity and chain of custody documentation.

## ✨ Features

### For Law Enforcement Officers

- **📱 Easy Evidence Collection** - Simple interface designed for field use
- **📍 GPS Location Tagging** - Automatic GPS coordinates for all evidence
- **🔐 Cryptographic Sealing** - SHA-512 hashing for tamper-proof evidence
- **📄 PDF Report Generation** - Court-admissible forensic reports
- **📷 Photo Evidence Capture** - Built-in camera for document and scene capture
- **📝 Text Notes** - Add observations and notes to cases
- **🔗 Chain of Custody** - Full documentation of evidence handling

### Security Features

- **Offline First** - Works without internet connection
- **No Cloud Upload** - All data stays on device
- **No Telemetry** - Zero tracking or analytics
- **Tamper Detection** - Cryptographic verification of all evidence
- **Airgap Ready** - Can operate in secure environments

## 📲 Installation

### For SAPS Deployment

1. **Download the APK**
   - Go to [Releases](https://github.com/Liamhigh/Liam-Highcock/releases)
   - Download the latest `SAPS-Forensic-Release.apk`

2. **Install on Android Device**
   - Enable "Install from Unknown Sources" in Settings
   - Open the APK file
   - Tap "Install"
   - Grant required permissions (Camera, Location)

### Requirements

- Android 8.0 (Oreo) or higher
- Camera for evidence capture
- GPS for location tagging

## 🚀 Quick Start Guide

### Creating a Case

1. Open the app
2. Tap the **"+ New Case"** button
3. Enter the case number (e.g., SAPS-2024-001)
4. Enter a description of the case
5. Tap **"Create"**

### Adding Evidence

1. Open an existing case
2. Choose evidence type:
   - **Document** - For document descriptions
   - **Photo** - For capturing photos
   - **Note** - For text observations
3. GPS location is automatically recorded

### Generating a Report

1. Open a case with evidence
2. Tap **"Generate Forensic Report"**
3. Wait for PDF generation
4. View, share, or save the report

## 📋 Usage Workflow

```
┌─────────────────┐
│  Create Case    │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Collect Evidence│
│ - Photos        │
│ - Documents     │
│ - Notes         │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│ Generate Report │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Share/Submit   │
└─────────────────┘
```

## 🏗️ Technical Architecture

### Project Structure

```
app/src/main/java/org/verumomnis/forensic/
├── core/                    # Core forensic engine
│   ├── ForensicEngine.kt    # Main engine
│   ├── ForensicModels.kt    # Data models
│   └── VerumOmnisApplication.kt
├── crypto/                  # Cryptographic sealing
│   └── CryptographicSealingEngine.kt
├── location/               # GPS location services
│   └── ForensicLocationService.kt
├── pdf/                    # PDF report generation
│   └── ForensicPdfGenerator.kt
├── report/                 # Narrative generation
│   └── ForensicNarrativeGenerator.kt
└── ui/                     # User interface
    ├── MainActivity.kt
    ├── CaseDetailActivity.kt
    ├── ScannerActivity.kt
    └── ReportViewerActivity.kt
```

### Forensic Standards

| Standard | Implementation |
|----------|---------------|
| Hash Algorithm | SHA-512 |
| Seal Algorithm | HMAC-SHA512 |
| PDF Version | 1.7 |
| Tamper Detection | Mandatory |

## 🔐 Verum Omnis Constitutional Governance

This application operates under constitutional governance principles:

1. **Truth** - Factual accuracy and verifiable evidence
2. **Fairness** - Protection of vulnerable parties
3. **Human Rights** - Dignity, equality, and agency
4. **Non-Extraction** - No sensitive data transmission
5. **Human Authority** - AI assists, never overrides
6. **Integrity** - No manipulation or bias
7. **Independence** - No external influence on outputs

## 🛠️ Building from Source

### Prerequisites

- **Java**: JDK 17 or later
- **Android SDK**: API Level 34 (Android 14)
- **Android Studio**: Hedgehog (2023.1.1) or later (recommended for development)

### Quick Start

1. Clone the repository:
   ```bash
   git clone https://github.com/Liamhigh/Liam-Highcock.git
   cd Liam-Highcock
   ```

2. Create `local.properties` with your Android SDK path:
   ```bash
   echo "sdk.dir=/path/to/your/android/sdk" > local.properties
   # Example paths:
   # macOS: sdk.dir=/Users/yourusername/Library/Android/sdk
   # Linux: sdk.dir=/home/yourusername/Android/Sdk
   # Windows: sdk.dir=C\:\\Users\\yourusername\\AppData\\Local\\Android\\Sdk
   ```

3. Build the APK:
   ```bash
   # Linux/macOS
   ./gradlew assembleDebug
   
   # Windows
   gradlew.bat assembleDebug
   ```

### Build Commands

| Command | Description | Output |
|---------|-------------|--------|
| `./gradlew assembleDebug` | Build debug APK | `app/build/outputs/apk/debug/app-debug.apk` |
| `./gradlew assembleRelease` | Build release APK (unsigned) | `app/build/outputs/apk/release/app-release-unsigned.apk` |
| `./gradlew testDebugUnitTest` | Run unit tests | Test reports in `app/build/reports/tests/` |
| `./gradlew clean` | Clean build artifacts | - |

### CI/CD

GitHub Actions automatically builds the APK on:
- Push to `main` or `develop` branches
- Pull requests to `main` branch

Build artifacts are available for download from the Actions tab.

## 📦 Deployment Options

### Option 1: Direct APK Distribution

1. Download APK from GitHub Releases
2. Distribute via secure file transfer
3. Officers install on their devices

### Option 2: MDM (Mobile Device Management)

1. Use organization's MDM solution
2. Push APK to enrolled devices
3. Configure permissions centrally

### Option 3: Private App Store

1. Host APK on internal server
2. Officers download from approved source
3. Automatic updates via internal distribution

## 📞 Support

For technical support or questions:
- Open an issue on GitHub
- Contact the development team

## 📄 License

Copyright © 2024 Verum Global Foundation

**Creator**: Liam Highcock

This application is provided free of charge to the South African Police Service for use in lawful forensic evidence collection.

[![GitHub](https://img.shields.io/badge/GitHub-Liamhigh-181717?logo=github)](https://github.com/Liamhigh)

---

<p align="center">
  <b>VERUM OMNIS</b><br>
  <i>Truth Above All</i>
</p>
