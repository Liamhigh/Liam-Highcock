---
applyTo:
  - "**/crypto/**"
---

# Cryptographic Code Instructions

This directory contains security-critical cryptographic sealing code. Exercise extreme caution when making changes.

## Critical Requirements

1. **Never weaken hash algorithms** - SHA-512 must be used for content hashing
2. **Preserve HMAC-SHA512** - All sealing operations must use HMAC-SHA512
3. **No external dependencies** - Do not add third-party crypto libraries without explicit approval
4. **Backward compatibility** - Changes must not break verification of existing sealed evidence

## Coding Guidelines

- Always use `java.security.MessageDigest` for hashing
- Use `javax.crypto.Mac` for HMAC operations
- Never log or expose cryptographic keys
- Handle all exceptions appropriately - failures must be explicit, never silent

## Testing Requirements

- Add unit tests for all cryptographic functions
- Include test vectors with known expected outputs
- Test edge cases: empty input, large files, special characters

## Security Checklist

Before submitting changes:
- [ ] Hash algorithm remains SHA-512
- [ ] HMAC algorithm remains HMAC-SHA512
- [ ] No secrets are logged or exposed
- [ ] Existing sealed evidence can still be verified
- [ ] Unit tests pass with expected outputs
