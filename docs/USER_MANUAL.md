# SAPS Forensic Engine - User Manual

## Introduction

The SAPS Forensic Evidence Engine is designed to help law enforcement officers collect, document, and secure forensic evidence in the field. This guide will walk you through all features of the application.

---

## Getting Started

### Home Screen

When you open the app, you'll see the **Home Screen** with:

- **Header** - App title and Verum Omnis governance badge
- **Cases List** - All your active cases
- **+ New Case** - Button to create a new case

---

## Creating a Case

### Step-by-Step

1. **Tap "New Case"** button at bottom right
2. **Enter Case Number**
   - Use SAPS format: `SAPS-2024-001`
   - Or your station's format
3. **Enter Description**
   - Brief description of the case
   - Include relevant details
4. **Tap "Create"**

### Case Information

Each case tracks:
- Case number (unique identifier)
- Description
- Status (Open, In Progress, Closed)
- Evidence items collected
- Creation timestamp

---

## Collecting Evidence

### Types of Evidence

| Type | Use For | Example |
|------|---------|---------|
| **Document** | Written records | Contracts, statements |
| **Photo** | Visual evidence | Crime scenes, injuries |
| **Note** | Observations | Officer notes, witness info |

### Adding Document Evidence

1. Open a case
2. Tap **"Document"** button
3. Enter document description
4. Tap **"Add"**

The system will:
- Record current GPS location
- Generate unique ID
- Create cryptographic hash
- Timestamp the entry

### Capturing Photo Evidence

1. Open a case
2. Tap **"Photo"** button
3. Camera will open
4. Frame the subject
5. Tap **"CAPTURE EVIDENCE"**

The system will:
- Take high-quality photo
- Record GPS coordinates
- Create SHA-512 hash
- Seal the evidence cryptographically

### Adding Text Notes

1. Open a case
2. Tap **"Note"** button
3. Type your observations
4. Tap **"Add"**

---

## Evidence Details

Each evidence item shows:

```
┌─────────────────────────────────────┐
│ PHOTO                    2024-03-15 │
│                                     │
│ Photo evidence captured at crime    │
│ scene entrance                      │
│                                     │
│ 📍 -25.7461, 28.1879               │
│ Hash: a7b4c2d9e1f...               │
└─────────────────────────────────────┘
```

- **Type** - Document, Photo, or Note
- **Timestamp** - When evidence was collected
- **Description** - What the evidence is
- **GPS** - Location coordinates
- **Hash** - Cryptographic verification code

---

## Generating Reports

### Creating a Forensic Report

1. Open a case with evidence
2. Scroll down to **"Generate Forensic Report"**
3. Tap the button
4. Wait for processing
5. View or share the report

### Report Contents

The PDF report includes:

1. **Title Page**
   - Case number
   - Description
   - Status
   - Evidence count
   - Generation timestamp

2. **Case Overview**
   - Complete case details
   - Governance declaration

3. **Evidence Summary**
   - Count by type
   - Total items

4. **Evidence Details**
   - Each item fully documented
   - GPS coordinates
   - Hash values

5. **Chain of Custody**
   - Chronological evidence list
   - Collection timestamps

6. **Verification Page**
   - Document hash
   - Verification instructions

### Sharing Reports

After generating:

1. **View Report** - Open in PDF viewer
2. **Share** - Send via email, messaging, etc.
3. **Open External** - Use device PDF app

---

## Understanding GPS Data

### Location Format

GPS coordinates appear as:
```
📍 -25.7461, 28.1879 (±5m)
```

- First number: Latitude (negative = South)
- Second number: Longitude (positive = East)
- (±Xm): Accuracy in meters

### Location Tips

- Works best outdoors
- Wait for GPS lock before capturing
- Indoor locations less accurate
- Check accuracy indicator

---

## Security Features

### Cryptographic Sealing

Every piece of evidence is:

1. **Hashed** - SHA-512 fingerprint created
2. **Sealed** - HMAC-SHA512 signature applied
3. **Timestamped** - Exact time recorded
4. **Geotagged** - Location embedded

### Tamper Detection

If anyone modifies evidence:
- Hash will change
- Seal verification fails
- Report shows tampering

### Offline Operation

The app:
- Requires NO internet
- Stores all data locally
- Never uploads to cloud
- Works in remote areas

---

## Best Practices

### Evidence Collection

✅ **DO:**
- Capture immediately at scene
- Include scale references in photos
- Write detailed descriptions
- Verify GPS lock before capture

❌ **DON'T:**
- Wait to document evidence
- Take photos of screens only
- Use vague descriptions
- Skip GPS verification

### Case Management

✅ **DO:**
- Use consistent case numbering
- Generate reports before handover
- Verify report integrity
- Back up to secure storage

❌ **DON'T:**
- Share unlocked devices
- Delete cases prematurely
- Share reports insecurely
- Ignore error messages

---

## Troubleshooting

### Common Issues

| Problem | Solution |
|---------|----------|
| No GPS | Go outside, wait 30 seconds |
| Camera black | Grant camera permission |
| Report fails | Check storage space |
| App crashes | Restart, reinstall if needed |

### Getting Help

1. Note the error message
2. Check this guide
3. Contact IT support
4. Open GitHub issue

---

## Quick Reference Card

```
╔═══════════════════════════════════════╗
║     SAPS FORENSIC ENGINE              ║
║         Quick Reference               ║
╠═══════════════════════════════════════╣
║                                       ║
║  CREATE CASE:  + Button → Fill form   ║
║                                       ║
║  ADD EVIDENCE: Open case → Choose:    ║
║    • Document (descriptions)          ║
║    • Photo (camera capture)           ║
║    • Note (text observations)         ║
║                                       ║
║  GENERATE REPORT: Case → Report btn   ║
║                                       ║
║  SHARE: Report → Share button         ║
║                                       ║
╠═══════════════════════════════════════╣
║  GPS: All evidence auto-geotagged     ║
║  HASH: SHA-512 tamper protection      ║
║  OFFLINE: No internet required        ║
╚═══════════════════════════════════════╝
```

---

**SAPS Forensic Evidence Engine**  
*Verum Omnis - Truth Above All*

Version 1.0.0
