# Contradiction Engine - Quick Start Guide

## What You Get

This implementation provides a **complete, working contradiction detection engine** that can be opened and run immediately in Android Studio.

## Features Implemented

✅ **Case Creation** - Create forensic cases with unique IDs  
✅ **Case Storage** - JSON-based local persistence  
✅ **Evidence Import** - Add text-based evidence  
✅ **Engine Orchestration** - Coordinated analysis pipeline  
✅ **Contradiction Detection** - Pattern-based contradiction engine  
✅ **Legal Rule Application** - Rule-based legal evaluation  
✅ **Report Generation** - Formatted forensic reports  
✅ **Report Viewer** - Display contradiction analysis  
✅ **UI Integration** - Fully wired user interface  

## File Structure

```
app/src/main/java/org/verumomnis/forensic/
├── model/
│   ├── Case.kt              # Case data model with JSON support
│   ├── Evidence.kt          # Evidence model (text/file)
│   └── Contradiction.kt     # Contradiction result model
├── engine/
│   ├── EvidenceParser.kt    # Statement extraction
│   ├── ContradictionEngine.kt  # Contradiction detection
│   ├── LawEngine.kt         # Legal rule application
│   ├── EngineOrchestrator.kt   # Full pipeline orchestration
│   └── CaseRepository.kt    # JSON storage
└── ui/
    ├── MainActivity.kt                 # Entry point (modified)
    ├── ContradictionEngineActivity.kt  # Main contradiction UI
    ├── CaseEvidenceActivity.kt        # Evidence management
    └── ReportViewerActivity.kt        # Report display (modified)
```

## How to Run

### 1. Open in Android Studio

```bash
# Clone or open the repository
cd Liam-Highcock
```

Open the project in Android Studio Hedgehog or later.

### 2. Sync Gradle

Android Studio will automatically:
- Download dependencies (Gson, Kotlin, AndroidX)
- Configure the build
- Index the project

### 3. Build and Run

- Click **Run** (▶️) or press `Shift+F10`
- Select your device/emulator
- App will install and launch

### 4. Use the Contradiction Engine

1. On the main screen, tap **"Contradiction Engine"** (red button)
2. Tap **"Create New Case"**
3. Enter a case name and tap **"Create"**
4. Tap your case to add evidence
5. Tap **"Add Text Evidence"**
6. Enter summary and content
7. Go back and tap **"Run Contradiction Engine"**
8. View the generated report

## Example Test Case

Try this to see contradictions detected:

**Evidence 1:**
- Summary: "Email from John"
- Content: "I did sign the contract on May 1st"

**Evidence 2:**
- Summary: "Email from John (later)"
- Content: "I never signed any contract"

**Result:** Engine will detect the contradiction between "did sign" and "never signed".

## Architecture Overview

### Data Flow

```
User Input → CaseEvidenceActivity
           ↓
      Case + Evidence
           ↓
    CaseRepository (saves JSON)
           ↓
  ContradictionEngineActivity (Run Engine button)
           ↓
   EngineOrchestrator.run(case)
           ↓
┌──────────┴──────────┬─────────────────┐
↓                     ↓                 ↓
EvidenceParser   ContradictionEngine   LawEngine
     ↓                ↓                 ↓
  Statements    Contradictions      Findings
           ↓         ↓                 ↓
           └─────────┴─────────────────┘
                     ↓
              Formatted Report
                     ↓
           ReportViewerActivity
```

### Storage

Cases are stored as JSON files in:
```
/data/data/org.verumomnis.forensic/files/cases/CASE-XXXX.json
```

Example case JSON:
```json
{
  "caseId": "CASE-A1B2C3D4",
  "caseName": "Contract Dispute",
  "createdAt": 1234567890000,
  "evidence": [
    {
      "evidenceId": "EV-X1Y2Z3A4",
      "type": "text",
      "content": "I did sign the contract",
      "summary": "Email from John",
      "timestamp": 1234567890000
    }
  ]
}
```

## Testing

### Manual Testing

1. Create a case
2. Add multiple evidence items with contradictory statements
3. Run the engine
4. Verify contradictions are detected

### Automated Testing

Run the instrumentation tests:

```bash
./gradlew connectedAndroidTest
```

Tests verify:
- Contradiction detection patterns (did/did not, never/did, always/never)
- Evidence parsing into statements
- No false positives on non-contradictory statements

## Customization

### Add More Contradiction Patterns

Edit `ContradictionEngine.kt`:

```kotlin
private fun isContradiction(a: String, b: String): Boolean {
    val aLower = a.lowercase()
    val bLower = b.lowercase()
    
    // Add your pattern here
    if (aLower.contains("confirmed") && bLower.contains("denied")) return true
    
    // ... existing patterns
}
```

### Modify Legal Rules

Edit `app/src/main/assets/rules/verum_rules.json`:

```json
{
  "legal_subjects": [
    {
      "name": "Your Legal Subject",
      "keywords": ["keyword1", "keyword2"],
      "severity": "HIGH"
    }
  ]
}
```

The `LawEngine` will automatically load and apply your rules.

## Integration with Existing Forensic Engine

The contradiction engine is **separate from** the existing forensic evidence system:

- **Existing System**: ForensicEngine, CryptographicSealingEngine, PDF generation
- **Contradiction Engine**: Simplified text-based analysis

Both can coexist. Users can choose which workflow to use.

## Troubleshooting

### Build Errors

**Error**: "Plugin not found"
- **Solution**: Ensure internet connection for Gradle to download dependencies

**Error**: "Cannot resolve symbol"
- **Solution**: File → Invalidate Caches → Invalidate and Restart

### Runtime Issues

**Error**: "No cases directory"
- **Solution**: App creates it automatically on first use

**Error**: "Cannot parse JSON"
- **Solution**: Check that verum_rules.json exists in assets/rules/

## Next Steps

Future enhancements you can add:

1. **PDF Reports**: Use iText7 to generate PDF contradiction reports
2. **OCR Support**: Add image-to-text for document evidence
3. **Timeline Analysis**: Visualize contradictions over time
4. **Export/Import**: Share cases between devices
5. **Advanced NLP**: Use ML models for better contradiction detection

## Summary

You now have:
- ✅ A working Android app
- ✅ Full contradiction detection engine
- ✅ Case and evidence management
- ✅ Report generation and viewing
- ✅ Local JSON storage
- ✅ Clean, maintainable code architecture

**Just open in Android Studio and click Run!**
