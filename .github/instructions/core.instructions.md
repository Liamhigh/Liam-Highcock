---
applyTo:
  - "**/core/**"
  - "**/ForensicEngine.kt"
  - "**/ForensicEvidence.kt"
---

# Core Forensic Engine Instructions

The core module contains the foundational forensic engine components.

## Core Principles

1. **Truth** - All evidence must be factual and unmodified
2. **Integrity** - No manipulation or bias in processing
3. **Non-Extraction** - Never transmit sensitive data externally
4. **Human Authority** - AI assists, never overrides human decisions

## ForensicEvidence Data Class

When modifying the evidence data structure:

- Maintain immutability where possible
- Include all required metadata fields
- Preserve backward compatibility with stored evidence
- Use appropriate data types for timestamps (Long/Instant)

## ForensicEngine Guidelines

- Keep the engine stateless between operations
- All operations should be deterministic
- Handle all error cases explicitly
- Provide meaningful error messages

## Evidence Types

Supported evidence types and their requirements:

| Type | Capture | Metadata Required |
|------|---------|-------------------|
| DOCUMENT | Camera scan | Size, page count |
| PHOTO | Camera capture | Resolution, GPS |
| TEXT | Manual input | Character count |
| AUDIO | (Future) | Duration, format |
| VIDEO | (Future) | Duration, resolution |

## Adding New Evidence Types

1. Add enum value to `EvidenceType`
2. Implement capture logic
3. Add sealing support
4. Update PDF generator
5. Write comprehensive tests
