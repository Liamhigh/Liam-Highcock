# SAPS Forensic Engine - Deployment Guide

## Overview

This guide is for IT administrators deploying the SAPS Forensic Evidence Engine across the organization.

---

## Deployment Options

### Option 1: Direct APK Distribution (Recommended for Small Deployments)

**Best for:** Individual stations, small units

1. Download release APK from GitHub
2. Verify APK integrity (SHA-256 hash)
3. Transfer to devices via:
   - USB cable
   - Secure file share
   - AirDrop alternative
4. Install on each device manually

**Pros:**
- Simple, no infrastructure needed
- Works offline
- Quick deployment

**Cons:**
- Manual updates required
- No central management

### Option 2: MDM Deployment (Recommended for Large Deployments)

**Best for:** Province-wide, national deployment

Supported MDM platforms:
- Microsoft Intune
- VMware Workspace ONE
- Samsung Knox
- Google Android Enterprise

**Deployment Steps:**

1. **Upload APK to MDM**
   ```
   Dashboard → Apps → Add → Android LOB App
   Upload: SAPS-Forensic-Release.apk
   ```

2. **Configure App Settings**
   ```json
   {
     "permissions": {
       "camera": "granted",
       "location": "granted"
     },
     "restrictions": {
       "uninstall": "disabled",
       "data_backup": "disabled"
     }
   }
   ```

3. **Assign to Device Groups**
   - Create device group for SAPS devices
   - Assign app as required install
   - Set installation priority

4. **Push Deployment**
   - Devices receive app automatically
   - Installation confirmed via MDM dashboard

### Option 3: Private App Store

**Best for:** Organizations with internal app stores

1. Set up internal distribution server
2. Upload APK to repository
3. Configure auto-update policy
4. Officers access via internal app store

---

## Pre-Deployment Checklist

### Device Requirements

| Requirement | Minimum | Recommended |
|-------------|---------|-------------|
| Android Version | 8.0 | 12.0+ |
| RAM | 2 GB | 4 GB |
| Storage | 50 MB | 100 MB |
| Camera | VGA | 12 MP+ |
| GPS | Required | High Accuracy |

### Security Checklist

- [ ] Devices encrypted
- [ ] Screen lock enabled
- [ ] Unknown sources restricted
- [ ] Device administrator policies applied
- [ ] Remote wipe capability configured

### Network Requirements

The app is **fully offline** and requires:
- ❌ No internet connection
- ❌ No cloud services
- ❌ No VPN
- ❌ No external servers

All data stored locally on device.

---

## Security Configuration

### App Permissions

Required permissions:
```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### Data Protection

The app implements:
- **No cloud backup** - Data stays on device
- **No data extraction** - Blocked by app configuration
- **Cryptographic sealing** - SHA-512 + HMAC-SHA512
- **No telemetry** - Zero analytics or tracking

### Recommended Device Policies

```
Policy: SAPS_Forensic_Device

Settings:
  - Encryption: Required
  - Screen Lock: PIN (6+ digits)
  - Auto-lock: 2 minutes
  - USB Debugging: Disabled
  - Developer Options: Disabled
  - Unknown Sources: Disabled (except for APK install)
  - Factory Reset Protection: Enabled
```

---

## APK Verification

### Before Deployment

Always verify APK integrity:

1. **Download from official source**
   ```
   https://github.com/Liamhigh/Liam-Highcock/releases
   ```

2. **Calculate hash**
   ```bash
   sha256sum SAPS-Forensic-Release.apk
   ```

3. **Compare with published hash**
   - Check release notes for official hash
   - Must match exactly

4. **Verify signature**
   ```bash
   apksigner verify --print-certs SAPS-Forensic-Release.apk
   ```

### APK Signing

Release APKs are signed with project signing key.
Never deploy unsigned or re-signed APKs.

---

## Update Management

### Version Tracking

| Version | Release Date | Notes |
|---------|--------------|-------|
| 1.0.0   | 2024-XX-XX   | Initial release |

### Update Process

1. **Test new version**
   - Deploy to test devices
   - Verify functionality
   - Check evidence integrity

2. **Stage rollout**
   - Start with 10% of devices
   - Monitor for issues
   - Expand gradually

3. **Full deployment**
   - Push to all devices
   - Verify installation
   - Collect feedback

### Rollback Procedure

If issues arise:
1. Stop deployment
2. Identify affected devices
3. Push previous stable version
4. Investigate issue
5. Re-deploy after fix

---

## Troubleshooting

### Installation Failures

| Error | Cause | Solution |
|-------|-------|----------|
| Parse error | Corrupt APK | Re-download |
| Not compatible | Old Android | Upgrade device |
| Insufficient storage | Low space | Clear storage |
| Blocked by policy | MDM restriction | Update policy |

### Runtime Issues

| Issue | Cause | Solution |
|-------|-------|----------|
| Camera black | Permission | Grant in settings |
| No GPS | Location off | Enable location |
| Crashes | Memory | Restart device |
| Reports fail | Storage | Clear old files |

### Support Escalation

1. **Level 1:** Station IT support
2. **Level 2:** Provincial IT team
3. **Level 3:** National IT / Development team

---

## Compliance

### Legal Requirements

The app is designed for:
- ✅ South African ECT Act compliance
- ✅ Digital evidence standards
- ✅ Chain of custody requirements
- ✅ Court admissibility standards

### Audit Trail

Each evidence item includes:
- Unique identifier
- Timestamp (UTC)
- GPS coordinates
- Device identifier
- Cryptographic hash
- Digital seal

### Data Retention

Recommend configuring:
- Case data: Retained as per SAPS policy
- Device storage: Regular backup to secure storage
- Report archival: Per case management procedures

---

## Support Resources

### Documentation

- [README](../README.md) - Overview
- [Installation Guide](INSTALLATION_GUIDE.md) - End user installation
- [User Manual](USER_MANUAL.md) - Usage instructions

### Technical Support

- GitHub Issues: For bug reports
- Email: [Configure organization email]
- Phone: [Configure support hotline]

---

## Appendix: Quick Deployment Script

For Linux/Mac administrators:

```bash
#!/bin/bash
# SAPS Forensic Engine - Deployment Script

APK_URL="https://github.com/Liamhigh/Liam-Highcock/releases/latest/download/SAPS-Forensic-Release.apk"
APK_FILE="SAPS-Forensic-Release.apk"

# Download
echo "Downloading SAPS Forensic Engine..."
curl -L -o $APK_FILE $APK_URL

# Verify (update hash)
echo "Verifying integrity..."
HASH=$(sha256sum $APK_FILE | cut -d' ' -f1)
echo "SHA-256: $HASH"

# Install to connected device
echo "Installing to device..."
adb install -r $APK_FILE

echo "Deployment complete!"
```

---

**SAPS Forensic Evidence Engine**  
*Verum Omnis Constitutional Governance*

Document Version: 1.0
