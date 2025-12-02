# Copilot Instructions for Verum Omnis Forensic Engine

## Project Overview

This is the **Verum Omnis Forensic Engine** - an offline-first Android application for collecting, cryptographically sealing, and reporting forensic evidence. The application operates under strict constitutional governance principles emphasizing truth, fairness, and human rights.

## Technology Stack

- **Language**: Kotlin
- **Platform**: Android (min SDK 24, target SDK 34)
- **Build System**: Gradle with Kotlin DSL
- **JDK**: Java 17

### Key Dependencies

- **CameraX**: Document and photo capture
- **iText7**: PDF report generation
- **ZXing**: QR code generation
- **AndroidX Security Crypto**: Cryptographic operations
- **Google Play Services Location**: GPS location capture
- **Coroutines**: Asynchronous operations

## Building and Testing

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34

### Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew testDebugUnitTest
```

### Output Location

APK files are generated at `app/build/outputs/apk/`

## Project Structure

```
app/src/main/java/org/verumomnis/forensic/
├── core/                    # Core forensic engine
├── crypto/                  # Cryptographic sealing (SHA-512, HMAC-SHA512)
├── leveler/                 # B1-B9 Leveler Engine for evidence analysis
├── location/                # GPS location services
├── pdf/                     # PDF report generation
├── report/                  # Narrative generation
└── ui/                      # User interface (Activities)
```

## Coding Conventions

### Kotlin Style

- Use Kotlin idioms and language features
- Prefer `val` over `var` where possible
- Use data classes for DTOs and domain models
- Use sealed classes for representing states and results
- Follow Android naming conventions for resources

### Architecture

- Use ViewBinding for UI components
- Use Kotlin Coroutines for async operations
- Follow offline-first design principles
- Maintain stateless processing where possible

## Security Considerations

This is a forensic evidence application with strict security requirements:

### Critical Security Requirements

1. **No Network Operations**: The app must remain offline-capable. Never add code that transmits data externally.
2. **Cryptographic Integrity**: All evidence must be sealed with SHA-512 hashes and HMAC-SHA512.
3. **No Telemetry**: Never add analytics, crash reporting, or any data collection.
4. **Tamper Detection**: Any changes to cryptographic functions must maintain tamper detection capabilities.
5. **No Cloud Dependencies**: The app must function completely offline.

### When Modifying Crypto Code

- Preserve SHA-512 for content hashing
- Preserve HMAC-SHA512 for sealing
- Maintain backward compatibility with existing sealed evidence
- Add comprehensive unit tests for any crypto changes

## Constitutional Governance

The application enforces these principles - code changes must not violate them:

| Principle | Requirement |
|-----------|-------------|
| Truth | All evidence must be factual and unmodified |
| Fairness | Protect vulnerable parties |
| Human Rights | Respect dignity, equality, and agency |
| Non-Extraction | No sensitive data transmission |
| Human Authority | AI assists, never overrides |
| Integrity | No manipulation or bias |
| Independence | No external influence on outputs |

## B1-B9 Leveler Engine

When working with the Leveler Engine, maintain these analysis capabilities:

- **B1**: Event Chronology Reconstruction
- **B2**: Contradiction Detection Matrix
- **B3**: Missing Evidence Gap Analysis
- **B4**: Timeline Manipulation Detection
- **B5**: Behavioral Pattern Recognition
- **B6**: Financial Transaction Correlation
- **B7**: Communication Pattern Analysis
- **B8**: Jurisdictional Compliance Check (UAE, UK, EU, US)
- **B9**: Integrity Index Scoring (0-100)

## PDF Report Standards

When modifying PDF generation:

- Maintain PDF 1.7 compliance
- Include forensic watermark: "VERUM OMNIS FORENSIC SEAL"
- Generate QR codes with verification data
- Include all hash values for verification
- Use A4 page size with proper margins

## Testing Guidelines

- Write unit tests for new functionality
- Test cryptographic operations independently
- Verify hash calculations match expected outputs
- Test PDF generation produces valid documents
- Ensure location services handle permission denial gracefully

## CI/CD

The repository uses GitHub Actions for:

- Building debug APK on push/PR to main and develop branches
- Running unit tests
- Building signed release APK on main branch (requires secrets)

Ensure any changes pass the existing CI workflow.

## Common Tasks

### Adding New Evidence Type

1. Update `EvidenceType` enum in core
2. Implement capture logic in relevant Activity
3. Add cryptographic sealing support
4. Update PDF report generator for new type
5. Add unit tests

### Modifying UI

1. Use ViewBinding, not findViewById
2. Follow Material Design guidelines
3. Ensure accessibility compliance
4. Test on multiple screen sizes

### Adding New Analysis Feature

1. Implement in LevelerEngine
2. Update ForensicNarrativeGenerator
3. Update PDF report output
4. Add comprehensive unit tests
