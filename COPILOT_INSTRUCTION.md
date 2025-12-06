# VERUM OMNIS FORENSIC APP & ENGINE SPECIFICATION — FOR COPILOT IMPLEMENTATION

---

## APP PURPOSE

Build an Android application that:

1. Accepts user evidence (text, OCR from images, PDFs, notes, transcripts).
2. Sends all evidence into a fixed, unchanging forensic engine.
3. Processes evidence through the same 12 forensic steps every time.
4. Outputs a structured forensic report in plain text.

**The engine is deterministic. No AI. No variation. Same logic path for every case.**

---

## GLOBAL APP BEHAVIOR

- **ANY evidence loaded → passes through engine → engine produces report.**
- No branching logic. No special handling.
- All evidence follows the identical forensic pipeline.

---

## ENGINE ARCHITECTURE

**Package:** `org.verumomnis.engine`

**Components (each has one job):**

1. EvidenceIngestor
2. NarrativeBuilder
3. SubjectClassifier
4. ContradictionDetector
5. OmissionDetector
6. BehaviorAnalyzer
7. KeywordScanner
8. SeverityScorer
9. DishonestyCalculator
10. LiabilityExtractor
11. ActionRecommender
12. ReportBuilder

---

## FORENSIC PIPELINE (EXACT ORDER)

### STEP 1 — INGESTION

- Collect all evidence → convert to plain text.
- **Output:** `List<String> rawSentences`

### STEP 2 — NARRATIVE BUILD

- Merge text into single narrative, preserve timestamps.
- **Output:** `List<Sentence> narrativeList`

### STEP 3 — SUBJECT CLASSIFICATION

Classify each sentence into zero or more of:

- ShareholderOppression
- BreachOfFiduciaryDuty
- Cybercrime
- FraudulentEvidence
- EmotionalExploitation

**Output:** `List<Category>` per sentence

### STEP 4 — CONTRADICTION DETECTION

- Compare sentences vs. sentences, sentences vs. evidence files.
- Mark contradictions → severity HIGH.

### STEP 5 — OMISSION DETECTION

- Identify missing context, hidden details, deletions, cropped screenshots, incomplete timelines.
- Mark omissions → severity MEDIUM or HIGH.

### STEP 6 — BEHAVIORAL ANALYSIS

Scan each sentence for:

- Evasion
- Gaslighting
- Blame-shifting
- Selective disclosure
- Refusal to answer
- Justification loops
- Unauthorized access/control

**Output:** `List<BehaviorFlag>` per sentence

### STEP 7 — KEYWORD SCANNING

Scan for red-flag keywords:
- "I don't recall"
- "I was advised"
- "My understanding is"
- "I thought it was okay"
- "I didn't mean to"
- "I was protecting"
- Financial terms: "write-off", "transfer", "loan", "advance"

### STEP 8 — SEVERITY SCORING

Calculate per-sentence severity (0-100):
- Contradiction = +30
- Omission = +20
- Behavioral flag = +15
- Keyword match = +10

### STEP 9 — DISHONESTY CALCULATION

Aggregate total dishonesty score across all sentences.
**Range:** 0 (clean) to 100 (severely dishonest)

### STEP 10 — LIABILITY EXTRACTION

Extract sentences with legal liability implications:
- Breach of duty
- Unauthorized transactions
- Falsified evidence
- Intentional concealment

### STEP 11 — ACTION RECOMMENDATION

Based on total score:
- 0-30: Monitor
- 31-60: Investigate further
- 61-80: Legal consultation recommended
- 81-100: Urgent legal action required

### STEP 12 — REPORT BUILDING

Compile final forensic report with:
1. Executive summary
2. Dishonesty score
3. Flagged sentences (sorted by severity)
4. Contradictions list
5. Omissions list
6. Behavioral patterns
7. Liability statements
8. Recommended actions

---

**Copilot must NOT simplify any forensic logic.**

**Copilot must build a full working Android Studio project using the logic in this repository.**

---

## ANDROID APP STRUCTURE

### Required Activities:

1. **MainActivity**
   - Create new case
   - Enter case name
   - Generate UUID
   - Navigate to CaseDetailActivity

2. **CaseDetailActivity**
   - Display case metadata
   - List all evidence items
   - Buttons: Add Text, Add Image, Add PDF, Generate Report
   - When "Generate Report" pressed → run engine → display report

3. **ScannerActivity**
   - Capture photo via CameraX
   - Run OCR if enabled
   - Save to evidence folder
   - Add to case

4. **ReportViewerActivity**
   - Display final forensic report
   - Show all 12 pipeline steps
   - Export option

---

## DATA MODELS

### ForensicCase

```kotlin
data class ForensicCase(
    val id: String,              // UUID
    val name: String,
    val createdAt: Long,
    val evidence: List<Evidence>,
    val reportPath: String?
)
```

### Evidence

```kotlin
data class Evidence(
    val id: String,
    val type: EvidenceType,      // TEXT, IMAGE, PDF, AUDIO, VIDEO
    val timestamp: Long,
    val filePath: String,
    val contentHash: String,     // SHA-512
    val rawText: String          // extracted/OCR text
)
```

### Sentence

```kotlin
data class Sentence(
    val id: Int,
    val text: String,
    val timestamp: Long?,
    val categories: List<Category>,
    val behaviorFlags: List<BehaviorFlag>,
    val severityScore: Int,
    val isContradiction: Boolean,
    val isOmission: Boolean,
    val keywords: List<String>
)
```

### ForensicReport

```kotlin
data class ForensicReport(
    val caseId: String,
    val generatedAt: Long,
    val dishonestyScore: Int,    // 0-100
    val sentences: List<Sentence>,
    val contradictions: List<String>,
    val omissions: List<String>,
    val behaviorPatterns: Map<String, Int>,
    val liabilityStatements: List<String>,
    val recommendation: String,
    val summary: String
)
```

---

## FILE STORAGE

All data stored in app-private directory:

```
/Android/data/org.verumomnis.forensic/files/
    cases/
        {CASE_ID}/
            case.json
            evidence/
                text_001.txt
                image_002.jpg
                pdf_003.pdf
            reports/
                report_{timestamp}.txt
```

---

## ENGINE IMPLEMENTATION REQUIREMENTS

### Each component MUST:

1. **EvidenceIngestor**
   - Accept all evidence types
   - Convert to plain text
   - Return `List<String>`

2. **NarrativeBuilder**
   - Merge all text preserving timestamps
   - Split into sentences
   - Return `List<Sentence>`

3. **SubjectClassifier**
   - Classify each sentence
   - Use keyword matching for categories
   - Return updated sentences with categories

4. **ContradictionDetector**
   - Compare all sentence pairs
   - Flag contradictions
   - Mark severity HIGH

5. **OmissionDetector**
   - Detect missing context
   - Identify deletions, cropping
   - Mark severity MEDIUM/HIGH

6. **BehaviorAnalyzer**
   - Pattern match for evasion, gaslighting, etc.
   - Flag behavioral issues
   - Add to sentence metadata

7. **KeywordScanner**
   - Scan predefined keyword list
   - Mark matches
   - Add to sentence metadata

8. **SeverityScorer**
   - Calculate per-sentence score
   - Use fixed formula
   - Return 0-100

9. **DishonestyCalculator**
   - Aggregate all sentence scores
   - Calculate case-level dishonesty
   - Return 0-100

10. **LiabilityExtractor**
    - Filter sentences with legal implications
    - Return liability list

11. **ActionRecommender**
    - Map score to recommendation
    - Use fixed thresholds
    - Return action string

12. **ReportBuilder**
    - Compile all outputs
    - Format as structured text
    - Return final report

---

## TECHNICAL REQUIREMENTS

- **Language:** Kotlin
- **Android SDK:** 34
- **Architecture:** Single-module with organized packages
- **Storage:** Local file I/O only
- **Processing:** On-device only
- **No networking**
- **No cloud integration**
- **No AI/ML models** (deterministic rules only)
- **No external APIs**

---

## BUILD REQUIREMENTS

The app must:

✅ Compile without errors in Android Studio
✅ Sync with Gradle successfully
✅ Build debug APK: `./gradlew assembleDebug`
✅ Build release APK: `./gradlew assembleRelease`
✅ Run on Android 7.0+ (API 24+)
✅ Function completely offline

---

## CONSTRAINTS FOR COPILOT

### MUST DO:

- Wire all 12 engine components in exact order
- Use deterministic logic only (no AI)
- Follow the exact pipeline steps
- Preserve all existing forensic logic
- Build a working APK

### MUST NOT DO:

- Simplify or remove any engine components
- Add AI/ML processing
- Add cloud features
- Add networking
- Skip any pipeline steps
- Change the order of operations
- Remove existing classes

---

**THE GOAL:** A fully functional, deterministic, offline forensic analysis app that processes all evidence through the same 12-step pipeline every time.
