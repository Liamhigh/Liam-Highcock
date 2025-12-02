# Verum Omnis Forensic Engine - TODO List

This document outlines the remaining tasks to complete the Verum Omnis Forensic Engine Android application.

## 📊 Current Implementation Status

| Component | Status | Notes |
|-----------|--------|-------|
| Core Engine | ✅ Complete | Case management, evidence handling |
| Cryptographic Sealing | ✅ Complete | SHA-512, HMAC-SHA512 |
| PDF Generation | ✅ Complete | iText7 with QR codes |
| Location Service | ✅ Complete | GPS capture |
| B1-B9 Leveler Engine | ✅ Complete | Analysis framework |
| UI Activities | ✅ Complete | 6 activities (Main, Case, Scanner, Report, Audio, Video) |
| Unit Tests | ✅ Complete | Core tests in place |
| CI/CD Pipeline | ✅ Complete | GitHub Actions workflow |
| **Evidence Persistence** | ✅ **Complete** | Room database with file storage |
| **Audio Recording** | ✅ **Complete** | AudioRecorderActivity with sealing |
| **Video Recording** | ✅ **Complete** | VideoRecorderActivity with CameraX |
| **Case Export** | ✅ **Complete** | JSON export functionality |

---

## 🟡 Medium Priority - Enhanced Functionality

### 5. OCR Integration for Documents
**Status:** ❌ Not Implemented (mentioned in ARCHITECTURE.md future enhancements)

**Tasks:**
- [ ] Integrate ML Kit or Tesseract for OCR
- [ ] Extract text from scanned documents
- [ ] Include extracted text in PDF reports
- [ ] Enable text search across evidence

### 6. Case Archive & Management
**Status:** ⚠️ Partial - Archive status exists but no archive UI

**Tasks:**
- [ ] Add archive case functionality in UI
- [ ] Create archived cases view/filter
- [ ] Implement unarchive functionality
- [ ] Add case search/filter capabilities

### 7. Evidence Verification UI
**Status:** ⚠️ Partial - Verification logic exists but limited UI

**Tasks:**
- [ ] Add individual evidence verification button
- [ ] Show verification status indicators
- [ ] Create detailed verification report view
- [ ] Add hash comparison tool for external verification

### 8. Enhanced B1-B9 Analysis
**Status:** ⚠️ Basic Implementation - Needs more features

**Tasks:**
- [ ] B6: Financial Transaction Correlation - Parse and correlate financial documents (invoices, receipts, bank statements) to detect discrepancies. Current implementation only has basic structure without actual document parsing.
- [ ] B7: Communication Pattern Analysis - Analyze email/message metadata for response patterns, unusual delays, or deleted message indicators. Current implementation checks for basic keyword indicators only.
- [ ] B8: Add detailed jurisdictional compliance rules (UAE, UK, EU, US) with specific legal requirements per jurisdiction
- [ ] Add analysis history and comparison
- [ ] Export analysis results separately

### 9. PDF Report Enhancements
**Status:** ⚠️ Working but could be improved

**Tasks:**
- [ ] Add evidence thumbnails/previews in PDF
- [ ] Include Leveler analysis results in PDF
- [ ] Add digital signature to PDF
- [ ] Support PDF/A format for long-term archiving
- [ ] Add page numbers and section links

---

## 🟢 Low Priority - Nice to Have

### 10. Multi-Language Support
**Status:** ❌ Not Implemented (mentioned in ARCHITECTURE.md)

**Tasks:**
- [ ] Externalize all strings to resources
- [ ] Add Arabic translation
- [ ] Add French translation
- [ ] Add German translation
- [ ] RTL layout support for Arabic

### 11. Accessibility Improvements
**Status:** ❌ Not Implemented

**Tasks:**
- [ ] Add content descriptions to all UI elements
- [ ] Ensure proper color contrast
- [ ] Support TalkBack navigation
- [ ] Add text scaling support

### 12. UI/UX Improvements
**Status:** ⚠️ Functional but basic

**Tasks:**
- [ ] Add animations and transitions
- [ ] Implement dark/light theme toggle
- [ ] Add onboarding/tutorial screens
- [ ] Improve empty state designs
- [ ] Add loading skeleton screens
- [ ] Implement swipe-to-delete for cases

### 13. Case Templates
**Status:** ❌ Not Implemented

**Tasks:**
- [ ] Create predefined case templates
- [ ] Allow custom template creation
- [ ] Pre-populate expected evidence types

### 14. Evidence Image Enhancement
**Status:** ❌ Not Implemented

**Tasks:**
- [ ] Add image cropping before capture
- [ ] Implement document edge detection
- [ ] Add brightness/contrast adjustment
- [ ] Support multiple image capture modes

### 15. Biometric Security
**Status:** ❌ Not Implemented

**Tasks:**
- [ ] Add fingerprint/face unlock option
- [ ] Implement PIN/password protection
- [ ] Encrypt sensitive data at rest

---

## 🔧 Technical Debt & Improvements

### 16. Code Quality
**Tasks:**
- [ ] Add more unit tests for edge cases
- [ ] Add instrumentation tests for UI
- [ ] Implement Kotlin Flow for reactive data
- [ ] Add dependency injection (Hilt/Koin)
- [ ] Refactor ForensicEngine to use Repository pattern

### 17. Error Handling
**Tasks:**
- [ ] Implement comprehensive error handling
- [ ] Add user-friendly error messages
- [ ] Implement local crash logging (logs stored locally only, never transmitted - maintains offline-first principle)
- [ ] Add retry mechanisms for failed operations

### 18. Performance Optimization
**Tasks:**
- [ ] Implement lazy loading for large case lists
- [ ] Optimize image compression
- [ ] Add background processing for report generation
- [ ] Implement efficient evidence content caching

### 19. Documentation
**Tasks:**
- [ ] Add KDoc comments to all public APIs
- [ ] Create user manual/guide
- [ ] Add code architecture diagrams
- [ ] Document cryptographic specifications

---

## 🧪 Testing Gaps

| Test Type | Status | Notes |
|-----------|--------|-------|
| Unit Tests | ✅ Present | CryptoEngine, LevelerEngine, Models |
| Integration Tests | ❌ Missing | Need ForensicEngine integration tests |
| UI Tests | ❌ Missing | Need Espresso tests for all activities |
| PDF Tests | ❌ Missing | Need tests for PDF output validation |

### Testing Tasks:
- [ ] Add ForensicEngine integration tests (requires Android context)
- [ ] Add UI tests for MainActivity
- [ ] Add UI tests for ScannerActivity
- [ ] Add UI tests for CaseDetailActivity
- [ ] Add UI tests for ReportViewerActivity
- [ ] Add PDF output validation tests
- [ ] Add location service mock tests

---

## 📋 Summary

### MVP Status: ✅ COMPLETE

All high-priority MVP features have been implemented:

| Task | Status | Commit |
|------|--------|--------|
| Evidence Persistence | ✅ Complete | Room database + file storage |
| Audio Evidence | ✅ Complete | AudioRecorderActivity |
| Video Evidence | ✅ Complete | VideoRecorderActivity |
| Data Export/Backup | ✅ Complete | JSON case export |

### Remaining Enhancements (Post-MVP)
- **OCR Integration:** 1-2 days
- **Enhanced B1-B9:** 2-3 days
- **Multi-Language:** 2-3 days
- **All Other Enhancements:** 2-3 weeks

---

## 📝 Notes

1. **Offline-First Priority:** All features must work completely offline
2. **No Telemetry:** Do not add any analytics or crash reporting that transmits data externally
3. **Security First:** All data must be encrypted and secured locally
4. **Constitutional Governance:** All features must align with the Verum Omnis Constitutional Governance principles defined in `VerumOmnisApplication.kt`:
   - **Truth:** Factual accuracy and verifiable evidence
   - **Fairness:** Protection of vulnerable parties
   - **Human Rights:** Dignity, equality, and agency
   - **Non-Extraction:** No sensitive data transmission
   - **Human Authority:** AI assists, never overrides
   - **Integrity:** No manipulation or bias
   - **Independence:** No external influence on outputs

---

*Last Updated: December 2024*
*Created by: GitHub Copilot*
