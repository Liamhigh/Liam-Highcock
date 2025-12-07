# Contradiction Engine Architecture

## System Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                         USER INTERFACE                          │
│                                                                 │
│  ┌────────────────┐  ┌──────────────────┐  ┌─────────────────┐│
│  │   MainActivity  │  │ Contradiction    │  │  CaseEvidence   ││
│  │                │  │ EngineActivity   │  │    Activity     ││
│  │ [Main Menu]    │  │                  │  │                 ││
│  │ • Cases        │  │ • Create Case    │  │ • Add Evidence  ││
│  │ • Engine Button│  │ • View Cases     │  │ • View Evidence ││
│  │                │  │ • Run Engine     │  │ • Edit Evidence ││
│  └────────┬───────┘  └────────┬─────────┘  └────────┬────────┘│
└───────────┼────────────────────┼────────────────────┼─────────┘
            │                    │                    │
            └────────────────────┴────────────────────┘
                                 │
    ┌────────────────────────────┴────────────────────────────┐
    │                      DATA MODELS                         │
    │  ┌──────────┐  ┌──────────┐  ┌──────────────┐          │
    │  │   Case   │  │ Evidence │  │Contradiction │          │
    │  │          │  │          │  │              │          │
    │  │ • caseId │  │ • id     │  │ • statementA │          │
    │  │ • name   │  │ • type   │  │ • statementB │          │
    │  │ • evidence│  │ • content│  │ • reason     │          │
    │  └──────────┘  └──────────┘  └──────────────┘          │
    └──────────────────────────────────────────────────────────┘
                                 │
    ┌────────────────────────────┴────────────────────────────┐
    │                   ENGINE LAYER                           │
    │                                                          │
    │  ┌──────────────────────────────────────────────────┐   │
    │  │         EngineOrchestrator.run(case)             │   │
    │  │                                                  │   │
    │  │  Step 1: Parse Evidence                         │   │
    │  │  ┌────────────────────────────────────────┐     │   │
    │  │  │  EvidenceParser                        │     │   │
    │  │  │  • parseEvidence(evidence)             │     │   │
    │  │  │  • splitIntoStatements(text)           │     │   │
    │  │  │  Returns: List<String> (statements)    │     │   │
    │  │  └────────────────────────────────────────┘     │   │
    │  │                    ↓                            │   │
    │  │  Step 2: Detect Contradictions                  │   │
    │  │  ┌────────────────────────────────────────┐     │   │
    │  │  │  ContradictionEngine                   │     │   │
    │  │  │  • detectContradictions(statements)    │     │   │
    │  │  │  • isContradiction(a, b)               │     │   │
    │  │  │  Patterns:                             │     │   │
    │  │  │    - did / did not                     │     │   │
    │  │  │    - never / did                       │     │   │
    │  │  │    - always / never                    │     │   │
    │  │  │    - was / was not                     │     │   │
    │  │  │  Returns: List<Contradiction>          │     │   │
    │  │  └────────────────────────────────────────┘     │   │
    │  │                    ↓                            │   │
    │  │  Step 3: Apply Legal Rules                      │   │
    │  │  ┌────────────────────────────────────────┐     │   │
    │  │  │  LawEngine                             │     │   │
    │  │  │  • evaluate(statements)                │     │   │
    │  │  │  • loadJsonArray("verum_rules.json")   │     │   │
    │  │  │  • loadLegalSubjects()                 │     │   │
    │  │  │  Returns: List<String> (findings)      │     │   │
    │  │  └────────────────────────────────────────┘     │   │
    │  │                    ↓                            │   │
    │  │  Step 4: Build Report                           │   │
    │  │  ┌────────────────────────────────────────┐     │   │
    │  │  │  buildReport()                         │     │   │
    │  │  │  • Format evidence summary             │     │   │
    │  │  │  • Format contradictions               │     │   │
    │  │  │  • Format legal findings               │     │   │
    │  │  │  Returns: String (formatted report)    │     │   │
    │  │  └────────────────────────────────────────┘     │   │
    │  └──────────────────────────────────────────────────┘   │
    └──────────────────────────────────────────────────────────┘
                                 │
    ┌────────────────────────────┴────────────────────────────┐
    │                   STORAGE LAYER                          │
    │  ┌────────────────────────────────────────────────┐     │
    │  │           CaseRepository                       │     │
    │  │                                                │     │
    │  │  • saveCase(case)                              │     │
    │  │    └─> Gson.toJson(case)                       │     │
    │  │    └─> Write to files/cases/CASE-XXXX.json     │     │
    │  │                                                │     │
    │  │  • loadCase(id)                                │     │
    │  │    └─> Read from files/cases/CASE-XXXX.json    │     │
    │  │    └─> Gson.fromJson(json, Case::class.java)   │     │
    │  │                                                │     │
    │  │  • getAllCases()                               │     │
    │  │    └─> List all JSON files in directory        │     │
    │  │    └─> Parse each to Case object               │     │
    │  │                                                │     │
    │  │  • deleteCase(id)                              │     │
    │  │    └─> Delete files/cases/CASE-XXXX.json       │     │
    │  └────────────────────────────────────────────────┘     │
    └──────────────────────────────────────────────────────────┘
                                 │
    ┌────────────────────────────┴────────────────────────────┐
    │                 LOCAL FILE SYSTEM                        │
    │                                                          │
    │  /data/data/org.verumomnis.forensic/files/               │
    │  └── cases/                                              │
    │      ├── CASE-A1B2C3D4.json                              │
    │      ├── CASE-X9Y8Z7W6.json                              │
    │      └── ...                                             │
    └──────────────────────────────────────────────────────────┘
```

## Data Flow Example

### Creating and Analyzing a Case

```
USER ACTION: Tap "Create New Case"
    ↓
ContradictionEngineActivity.showCreateCaseDialog()
    ↓
Case.create("Contract Dispute")
    ↓
CaseRepository.saveCase(case)
    ↓
JSON written to: files/cases/CASE-A1B2C3D4.json
    ↓
UI updated with new case

─────────────────────────────────────────────────

USER ACTION: Tap case → Tap "Add Text Evidence"
    ↓
CaseEvidenceActivity.showAddTextEvidenceDialog()
    ↓
Evidence.createText("I did sign", "Email from John")
    ↓
case.evidence.add(evidence)
    ↓
CaseRepository.saveCase(case)
    ↓
JSON updated with new evidence
    ↓
UI shows evidence in list

─────────────────────────────────────────────────

USER ACTION: Add second evidence
    ↓
Evidence.createText("I did not sign", "Email from John 2")
    ↓
case.evidence.add(evidence)
    ↓
CaseRepository.saveCase(case)

─────────────────────────────────────────────────

USER ACTION: Tap "Run Contradiction Engine"
    ↓
EngineOrchestrator.run(case)
    ↓
┌─────────────────────────────────────┐
│ Step 1: Parse Evidence              │
│ EvidenceParser extracts:            │
│ • "I did sign"                      │
│ • "I did not sign"                  │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Step 2: Detect Contradictions       │
│ ContradictionEngine compares:       │
│ • "did sign" vs "did not sign"      │
│ • Pattern match: did / did not      │
│ • Creates Contradiction object      │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Step 3: Apply Legal Rules           │
│ LawEngine checks verum_rules.json   │
│ • Matches keywords                  │
│ • Identifies legal subjects         │
│ • Returns findings                  │
└─────────────────────────────────────┘
    ↓
┌─────────────────────────────────────┐
│ Step 4: Build Report                │
│ Formats into text report:           │
│ ═══════════════════════════════════ │
│ VERUM OMNIS FORENSIC REPORT         │
│ ═══════════════════════════════════ │
│ Case: Contract Dispute              │
│ ...                                 │
│ CONTRADICTIONS FOUND:               │
│ 1. "I did sign" contradicts         │
│    "I did not sign"                 │
│ ...                                 │
└─────────────────────────────────────┘
    ↓
Report passed to ReportViewerActivity
    ↓
Report displayed to user
```

## Component Dependencies

```
ContradictionEngineActivity
    ├── depends on → CaseRepository (storage)
    ├── depends on → EngineOrchestrator (analysis)
    └── depends on → Case, Evidence (models)

CaseEvidenceActivity
    ├── depends on → CaseRepository (storage)
    └── depends on → Case, Evidence (models)

EngineOrchestrator
    ├── depends on → EvidenceParser
    ├── depends on → ContradictionEngine
    ├── depends on → LawEngine
    └── depends on → Case, Evidence, Contradiction (models)

EvidenceParser
    └── depends on → Evidence (model)

ContradictionEngine
    └── depends on → Contradiction (model)

LawEngine
    ├── depends on → Context (for assets)
    └── reads → assets/rules/verum_rules.json

CaseRepository
    ├── depends on → Context (for file system)
    ├── depends on → Gson (serialization)
    └── depends on → Case (model)
```

## File Formats

### Case JSON Structure
```json
{
  "caseId": "CASE-A1B2C3D4",
  "caseName": "Contract Dispute",
  "createdAt": 1701950400000,
  "evidence": [
    {
      "evidenceId": "EV-X1Y2Z3A4",
      "type": "text",
      "content": "I did sign the contract",
      "filePath": "",
      "summary": "Email from John",
      "timestamp": 1701950400000
    }
  ]
}
```

### Rule JSON Structure (verum_rules.json)
```json
{
  "legal_subjects": [
    {
      "name": "Fraud",
      "keywords": ["forged", "false representation"],
      "severity": "CRITICAL"
    }
  ]
}
```

## Extension Points

To extend functionality:

1. **Add contradiction patterns**: Edit `ContradictionEngine.isContradiction()`
2. **Add legal rules**: Modify `assets/rules/verum_rules.json`
3. **Add evidence types**: Extend `Evidence` model and update parser
4. **Customize report format**: Modify `EngineOrchestrator.buildReport()`
5. **Add export formats**: Create new methods in `EngineOrchestrator`
