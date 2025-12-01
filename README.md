# SAPS Forensic Evidence Engine

<p align="center">
  <img src="main-logo.png" alt="SAPS Forensic Engine Logo" width="200"/>
</p>

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

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Build Commands

```bash
# Build Debug APK
./gradlew assembleDebug

# Build Release APK
./gradlew assembleRelease

# Run Tests
./gradlew test
```

Output APKs will be in `app/build/outputs/apk/`

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

---

<p align="center">
  <b>VERUM OMNIS</b><br>
  <i>Truth Above All</i>
</p>
