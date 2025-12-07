# Contradiction Engine - User Guide

## Overview

The Contradiction Engine is a forensic tool for analyzing statements and detecting contradictions. It provides a simple, offline-first workflow for building cases, adding evidence, and generating analysis reports.

## Features

- **Case Management**: Create and manage forensic cases
- **Evidence Import**: Add text-based evidence (statements, messages, documents)
- **Contradiction Detection**: Automatically detect contradictory statements
- **Legal Rule Application**: Apply legal rules from JSON configuration
- **Report Generation**: Generate comprehensive forensic reports
- **Offline Storage**: All data stored locally as JSON files

## How to Use

### 1. Launch Contradiction Engine

From the main app screen, tap the **"Contradiction Engine"** button (red button in the toolbar).

### 2. Create a New Case

1. Tap **"Create New Case"**
2. Enter a case name
3. Tap **"Create"**

### 3. Add Evidence

1. Select your case from the list
2. Tap **"Add Text Evidence"**
3. Enter:
   - **Summary**: Brief description (e.g., "Email from John")
   - **Content**: The actual text content containing statements
4. Tap **"Add"**

Repeat to add multiple pieces of evidence.

### 4. Run the Engine

Once you've added evidence:

1. Tap **"Run Contradiction Engine"** on your case
2. The engine will:
   - Parse all evidence into individual statements
   - Detect contradictions between statements
   - Apply legal rules
   - Generate a comprehensive report

### 5. View the Report

The report displays:
- **Evidence Summary**: List of all evidence items
- **Contradictions Found**: Detected contradictions with explanations
- **Legal Evaluation**: Legal subjects and findings based on rules

## Contradiction Detection Patterns

The engine detects these contradiction patterns:

| Pattern | Example |
|---------|---------|
| did / did not | "I did sign" vs "I did not sign" |
| never / did | "I never received it" vs "I did receive it" |
| always / never | "I always attend" vs "I never attend" |
| was / was not | "I was there" vs "I was not there" |

## Legal Rule Application

The engine reads rules from `assets/rules/verum_rules.json` and applies them to statements:

- **Legal Subjects**: Detects legal issues (fraud, cybercrime, breach of duty, etc.)
- **Dishonesty Patterns**: Identifies omissions, manipulations, contradictions
- **Behavioral Patterns**: Detects evasion, gaslighting, concealment

## Data Storage

All cases are stored locally in:
```
/data/data/org.verumomnis.forensic/files/cases/
```

Each case is a JSON file: `CASE-XXXX.json`

## Architecture

### Model Layer
- `Case`: Container for evidence items
- `Evidence`: Text or file-based evidence
- `Contradiction`: Detected contradiction result

### Engine Layer
- `EvidenceParser`: Extracts statements from evidence
- `ContradictionEngine`: Detects contradictions
- `LawEngine`: Applies legal rules
- `EngineOrchestrator`: Coordinates all engines

### Storage Layer
- `CaseRepository`: JSON-based local persistence

## Example Workflow

1. **Create Case**: "Johnson vs Smith Contract Dispute"
2. **Add Evidence**:
   - Email from Johnson: "I never signed the contract"
   - Email from Smith: "Johnson did sign the contract on May 1st"
3. **Run Engine**
4. **Review Report**: Contradiction detected between the two statements

## Tips

- Add evidence in chronological order
- Use clear, descriptive summaries
- Include all relevant statements, even if they seem consistent
- The more evidence, the better the analysis

## Limitations

- Currently supports text-based evidence only
- Contradiction detection is pattern-based (not AI-powered)
- Legal rules are predefined in JSON configuration

## Next Steps

Future enhancements:
- OCR for image-based evidence
- PDF document parsing
- More sophisticated NLP-based contradiction detection
- Export reports as PDF
- Timeline visualization
