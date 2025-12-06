# Verum Forensic App — Developer Specification for Copilot

**Authoritative Build + Wiring Instructions**

## Purpose of This Document

This document tells Copilot exactly how the app is supposed to function, how the forensic engine works, and how to assemble all modules into a fully working Android APK.

This repo contains ALL forensic logic already — contradiction engine, timeline reconstruction, reporting, hashing, evidence handling — but the UI and module wiring must be completed.

**Copilot must NOT simplify any forensic logic.**

**Copilot must build a full working Android Studio project using the logic in this repository.**

---

## 1. App Overview

The Verum Forensic App is a local-only, offline-first forensic tool that allows a user to:

1. **Create a Forensic Case**
2. **Add Evidence**
   - Text notes
   - Images (scan or gallery)
   - Documents
   - Audio
   - Video
3. **Run the Forensic Engine**
   - Contradiction analysis
   - Timeline reconstruction
   - Behavioural/linguistic metrics
   - SHA-512 hashing
   - Evidence indexing
4. **Generate a Sealed Report**
5. **View and Export the Report**

**All forensic processing is done on-device only.**

---

## 2. Architecture Requirements (MANDATORY)

The repo uses a **single-module structure** with organized packages:

- `/app` — Android entry point
- `/core` — data models (Case, Evidence, ForensicEngine)
- `/crypto` — cryptographic sealing (SHA-512, HMAC-SHA512)
- `/leveler` — B1-B9 Leveler Engine (contradiction analysis, integrity scoring)
- `/report` — forensic narrative generation
- `/pdf` — PDF report generation
- `/location` — GPS location services
- `/tax` — tax return analysis engine
- `/database` — local data persistence
- `/ui` — Activities and UI components

### Copilot must:

✔ Ensure all packages are properly wired together
✔ Use existing classes from each package
✔ Ensure Android Studio can build a complete APK
✔ NOT create duplicate logic that already exists

---

## 3. Required Activities (Copilot must generate these if missing)

### MainActivity

- User types case name
- Press "Create Case"
- App generates UUID
- Creates folder structure:
  ```
  /cases/{CASE_ID}/
      case.json
      evidence/
      reports/
  ```
- Navigates to `CaseDetailActivity`

---

### CaseDetailActivity

Shows:
- Case metadata
- Evidence list

Buttons:
- Add Text Note
- Add Image
- Add Audio
- Add Video
- **Generate Report**

When the user taps "Generate Report":
1. Collect all evidence
2. Call `ForensicEngine.process(case)`
3. Run B1-B9 Leveler analysis
4. Generate cryptographic seals (SHA-512, HMAC-SHA512)
5. Write PDF report to `/cases/{CASE_ID}/reports/`
6. Navigate to `ReportViewerActivity`

---

### ScannerActivity

- Capture image using CameraX
- Save to `/cases/{CASE_ID}/evidence/IMG_xxx.jpg`
- Compute SHA-512 hash
- Update case model

---

### AudioRecorderActivity

- Record audio using MediaRecorder
- Save to `/cases/{CASE_ID}/evidence/AUD_xxx.wav`
- Compute SHA-512 hash

---

### VideoRecorderActivity

- Record video
- Save to `/cases/{CASE_ID}/evidence/VID_xxx.mp4`
- Compute SHA-512 hash

---

### ReportViewerActivity

Load report PDF and display forensic sections:
- **Contradictions** (from B2 Leveler)
- **Timeline** (from B1 Leveler)
- **Narrative** (from ForensicNarrativeGenerator)
- **Evidence Index**
- **SHA-512 Seals**
- **B9 Integrity Score**

Provide "Export" button

---

## 4. Required Forensic Engine Behaviour

**Copilot must NOT replace this logic.**
Logic must be read from existing classes in `/core`, `/crypto`, `/leveler`, `/report`, `/pdf`.

### The Forensic Engine must:

1. Load all evidence from case
2. Compute SHA-512 hashes per item
3. Run **B1-B9 Leveler Engine**:
   - B1: Event Chronology Reconstruction
   - B2: Contradiction Detection Matrix
   - B3: Missing Evidence Gap Analysis
   - B4: Timeline Manipulation Detection
   - B5: Behavioral Pattern Recognition
   - B6: Financial Transaction Correlation
   - B7: Communication Pattern Analysis
   - B8: Jurisdictional Compliance Check
   - B9: Integrity Index Scoring (0-100)
4. Generate forensic narrative using `ForensicNarrativeGenerator`
5. Compute HMAC-SHA512 seals using `CryptographicSealingEngine`
6. Produce PDF report using `ForensicPdfGenerator`
7. Include QR code with verification data

**Use existing classes:**
- `ForensicEngine` (core)
- `CryptographicSealingEngine` (crypto)
- `LevelerEngine` (leveler)
- `ForensicNarrativeGenerator` (report)
- `ForensicPdfGenerator` (pdf)

---

## 5. Data Model Rules

### Case object must contain:

```kotlin
data class ForensicCase(
    val id: String,              // UUID
    val name: String,
    val createdAt: Long,         // timestamp
    val evidence: List<ForensicEvidence>,
    val reportPath: String?
)
```

### Evidence object must contain:

```kotlin
data class ForensicEvidence(
    val id: String,
    val type: EvidenceType,      // TEXT, IMAGE, AUDIO, VIDEO, DOCUMENT
    val timestamp: Long,
    val filePath: String,
    val contentHash: String,     // SHA-512
    val metadata: Map<String, Any>
)
```

### Report must include:

- Forensic narrative summary
- B1-B9 Leveler analysis results
- Timeline of events
- Contradiction matrix
- Evidence index with SHA-512 hashes
- Final case integrity hash (HMAC-SHA512)
- QR code for verification
- APK signature hash

**Copilot must use existing data models from `/core` package.**

---

## 6. File Storage Rules

Everything must be stored locally in app-private storage:

```
/Android/data/org.verumomnis.forensic/files/cases/{CASE_ID}/
    case.json
    evidence/
        IMG_xxx.jpg
        AUD_xxx.wav
        VID_xxx.mp4
        DOC_xxx.pdf
    reports/
        forensic_report_{timestamp}.pdf
```

**No cloud storage. No network calls. Everything offline.**

---

## 7. Cryptographic Requirements

### All evidence must be cryptographically sealed:

1. **SHA-512** for content hashing
2. **HMAC-SHA512** for tamper-proof sealing
3. **QR Code** with verification metadata
4. **APK Signature Hash** for chain of trust

Use `CryptographicSealingEngine` class from `/crypto` package.

**DO NOT implement custom crypto. Use existing implementation.**

---

## 8. Build Requirements (Critical)

The final output must be:

✔ A complete Android Studio project
✔ That syncs with Gradle without errors
✔ That compiles without errors
✔ That produces a working debug APK
✔ That produces a working release APK (with signing)
✔ Using ALL logic already present in the repository
✔ Wiring together existing forensic engine components

### Copilot MUST NOT:

❌ Delete working forensic logic
❌ Replace existing engine code
❌ Simplify B1-B9 Leveler analysis
❌ Remove cryptographic sealing
❌ Convert app to cloud architecture
❌ Add network calls
❌ Add Firebase or external dependencies

---

## 9. Testing Requirements

Ensure these work:

1. Create a case → saves to database/filesystem
2. Add evidence (text, image, audio, video) → files saved, hashed
3. Generate report → B1-B9 analysis runs, PDF created
4. View report → PDF displays correctly
5. Export report → file can be shared

**The goal is a fully functional offline forensic app that builds into a deployable APK.**
