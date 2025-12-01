# Verum Omnis Forensic Engine

An Android application for collecting, sealing, and reporting forensic evidence in accordance with the Verum Omnis Constitutional Governance Layer.

![Build Status](https://github.com/Liamhigh/Liam-Highcock/actions/workflows/build-apk.yml/badge.svg)

## Features

- **Cryptographic Evidence Sealing**: SHA-512 hashing with HMAC-SHA512 sealing for tamper detection
- **GPS Location Capture**: Automatic geolocation of evidence at collection time
- **AI-Readable PDF Reports**: Structured forensic narratives following legal admissibility standards
- **Offline-First Design**: No cloud logging, no telemetry, airgap ready
- **Stateless Operation**: No persistent user data beyond case files
- **B1-B9 Compliance**: Full Leveler Engine for contradiction and anomaly detection

## Constitutional Governance

This application operates under the Verum Omnis Constitution Mode, which enforces:

### Core Principles

| Principle | Description |
|-----------|-------------|
| **Truth** | Factual accuracy and verifiable evidence |
| **Fairness** | Protection of vulnerable parties |
| **Human Rights** | Dignity, equality, and agency |
| **Non-Extraction** | No sensitive data transmission |
| **Human Authority** | AI assists, never overrides |
| **Integrity** | No manipulation or bias |
| **Independence** | No external influence on outputs |

### Forensic Standards

| Standard | Value |
|----------|-------|
| Hash Standard | SHA-512 |
| PDF Standard | PDF 1.7 |
| Watermark | VERUM OMNIS 3D LOGO CENTERED |
| QR Code Inclusion | Yes |
| Tamper Detection | Mandatory |
| Admissibility Standard | Legal-grade |

### Security

| Feature | Status |
|---------|--------|
| Offline First | ✅ True |
| Stateless | ✅ True |
| No Cloud Logging | ✅ True |
| No Telemetry | ✅ True |
| Airgap Ready | ✅ True |

## Building

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Build Debug APK

```bash
./gradlew assembleDebug
```

### Build Release APK

```bash
./gradlew assembleRelease
```

The APK will be output to `app/build/outputs/apk/`

## Usage

1. **Create a Case** - Start by creating a new forensic case with a descriptive name
2. **Add Evidence** - Use the scanner to capture documents, photos, or text notes
3. **Generate Report** - Create a forensic PDF report with full evidence chain
4. **View/Share Reports** - Access and share sealed forensic reports

### Evidence Types

- 📄 Documents (scanned)
- 📷 Photos (captured)
- 📝 Text (notes and observations)
- 🎵 Audio (coming soon)
- 🎥 Video (coming soon)

## Project Structure

```
app/src/main/java/org/verumomnis/forensic/
├── core/                    # Core forensic engine
│   ├── ForensicEngine.kt
│   ├── ForensicEvidence.kt
│   └── VerumOmnisApplication.kt
├── crypto/                  # Cryptographic sealing
│   └── CryptographicSealingEngine.kt
├── location/               # GPS location services
│   └── ForensicLocationService.kt
├── pdf/                    # PDF report generation
│   └── ForensicPdfGenerator.kt
├── report/                 # Narrative generation
│   └── ForensicNarrativeGenerator.kt
├── leveler/                # B1-B9 Compliance engine
│   └── LevelerEngine.kt
└── ui/                     # User interface
    ├── MainActivity.kt
    ├── ScannerActivity.kt
    └── ReportViewerActivity.kt
```

## Leveler Engine (B1-B9 Compliance)

The Leveler Engine provides multi-dimensional forensic analysis:

- **Contradiction Detection**: Identifies conflicting statements across evidence
- **Timeline Reconstruction**: Builds chronological narrative from fragments
- **Behavioral Pattern Analysis**: Detects gaslighting, evasion, concealment
- **Jurisdictional Compliance**: UAE, SA, EU law compliance checking
- **Integrity Index**: 0-100 scoring with breakdown

### Behavioral Patterns Detected

| Pattern | Indicators |
|---------|------------|
| Evasion | "cannot comment", "not sure", "don't recall", "maybe" |
| Gaslighting | "you misunderstood", "that never happened", "you're confused" |
| Concealment | "deleted", "lost", "forgot", "not available", "accidentally" |
| Deflection | "what about you", "others did worse", "not my department" |

## License

Copyright © 2024 Verum Global Foundation

## Creator

**Liam Highcock**
