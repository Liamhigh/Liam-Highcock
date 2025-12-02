---
applyTo:
  - "**/pdf/**"
  - "**/report/**"
  - "**/*Generator.kt"
---

# PDF and Report Generation Instructions

This directory contains code for generating forensic PDF reports and narrative content.

## PDF Standards

1. **PDF 1.7 compliance** - All generated PDFs must be PDF 1.7 compliant
2. **iText7 library** - Use iText7 for PDF generation
3. **A4 page size** - Use A4 (210mm x 297mm) with proper margins
4. **Forensic watermark** - Include "VERUM OMNIS FORENSIC SEAL" watermark

## Required Report Elements

Every forensic report must include:

- Case identifier and timestamp
- Evidence list with individual SHA-512 hashes
- Combined case integrity hash
- HMAC-SHA512 seal hashes
- QR code with verification data
- APK signature hash for chain of trust
- B1-B9 Leveler analysis results

## QR Code Requirements

- Use ZXing library for QR generation
- Include: case ID, primary hash, timestamp
- Ensure QR is scannable at 150 DPI minimum

## Narrative Generation

- Generate human-readable forensic narratives
- Maintain factual, neutral language
- Follow legal admissibility standards
- Include jurisdictional compliance notes (UAE, UK, EU, US)

## Testing

- Verify generated PDFs are valid and openable
- Test QR code scannability
- Verify hash values in reports match source evidence
- Test with various evidence types and quantities
