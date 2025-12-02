---
applyTo:
  - "**/test/**"
  - "**/*Test.kt"
  - "**/*Tests.kt"
---

# Test Code Instructions

Guidelines for writing tests in the Verum Omnis Forensic Engine.

## Test Framework

- Use JUnit 4 for unit tests
- Use AndroidX Test for instrumentation tests
- Use Truth or standard assertions for verification

## Test Structure

```kotlin
class CryptographicSealingEngineTest {
    
    @Test
    fun `hash should produce consistent SHA-512 output`() {
        // Arrange
        val input = "test data"
        val expectedHash = "..." // Known expected value
        
        // Act
        val result = sealingEngine.hash(input)
        
        // Assert
        assertEquals(expectedHash, result)
    }
}
```

## Testing Priorities

### Critical (Must Have)
- Cryptographic operations (hash, HMAC)
- Evidence sealing and verification
- PDF generation validity

### Important (Should Have)
- Leveler Engine calculations
- Location service handling
- Permission flows

### Nice to Have
- UI interaction tests
- Edge case scenarios

## Test Data

- Use deterministic test data with known expected outputs
- Include test vectors for cryptographic functions
- Avoid random data that makes tests non-reproducible

## What NOT to Test

- Do not add network-related tests (app is offline-first)
- Do not add tests requiring real GPS (use mock locations)
- Do not add tests that depend on external services
