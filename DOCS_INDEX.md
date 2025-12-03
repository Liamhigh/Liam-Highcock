# Documentation Guide

Welcome! This branch contains comprehensive documentation to help you build the Verum Omnis Forensic Engine. Choose the guide that matches your needs:

## 📚 Documentation Overview

### For the Impatient
**[CLONE_AND_BUILD.md](CLONE_AND_BUILD.md)** - 3 steps, ~1 minute read
- Absolute minimum to get building
- No explanations, just commands
- Perfect if you're already familiar with Android

### For Quick Setup
**[QUICKSTART.md](QUICKSTART.md)** - 5 steps, ~3 minute read
- Includes prerequisite installation
- Step-by-step instructions
- Gets you building in 5 minutes
- Basic troubleshooting

### For Comprehensive Information
**[BUILD.md](BUILD.md)** - Complete guide, ~10 minute read
- Detailed build instructions
- All build variants explained
- Comprehensive troubleshooting
- Release signing configuration
- Command-line and IDE options
- Project structure explanation

### For Understanding the Branch
**[BRANCH_README.md](BRANCH_README.md)** - Branch overview, ~5 minute read
- What's included in this branch
- Why this branch exists
- Complete file structure
- What you get when you build
- System requirements

### For Understanding the Application
**[README.md](README.md)** - Project overview
- Feature list
- Constitutional governance principles
- B1-B9 Leveler Engine
- Usage instructions
- Security considerations
- Project structure

## 🎯 Choose Your Path

```
┌─────────────────────────────────────────┐
│   What do you want to do?              │
└─────────────────────────────────────────┘
           │
           ├─ Just build it now ────────────> CLONE_AND_BUILD.md
           │
           ├─ Set up properly (5 min) ─────> QUICKSTART.md
           │
           ├─ Understand build options ────> BUILD.md
           │
           ├─ Know what's in this branch ──> BRANCH_README.md
           │
           └─ Learn about the app ─────────> README.md
```

## 📋 Additional Files

### Configuration Templates
- **local.properties.template** - Template for Android SDK path configuration
  - Copy to `local.properties` and edit for your system
  - Or let Android Studio create it automatically

### Build Configuration
- **build.gradle.kts** - Root Gradle build file
- **app/build.gradle.kts** - App module build configuration
- **settings.gradle.kts** - Gradle settings
- **gradle.properties** - Gradle properties

### Git Configuration
- **.gitignore** - Excludes build artifacts from version control
- **.gitattributes** - Ensures consistent line endings across platforms

## 🚀 Recommended Flow

### First Time Users
1. Read **QUICKSTART.md** (5 minutes)
2. Clone and open in Android Studio
3. Build your first APK
4. If issues occur, consult **BUILD.md** troubleshooting
5. Read **README.md** to learn about features

### Experienced Android Developers
1. Read **CLONE_AND_BUILD.md** (1 minute)
2. Clone and build
3. Check **README.md** for app features
4. Refer to **BUILD.md** if needed

### DevOps / CI/CD Engineers
1. Read **BUILD.md** for complete build options
2. Check **BRANCH_README.md** for file structure
3. Review **gradle.properties** and build configs
4. Set up automated builds

## 📱 What You're Building

The **Verum Omnis Forensic Engine** is a complete Android application for forensic evidence collection with:

- Camera-based document scanning
- Cryptographic evidence sealing (SHA-512, HMAC-SHA512)
- GPS location capture
- PDF report generation
- B1-B9 forensic analysis engine
- Offline-first design (no network required)

See [README.md](README.md) for complete feature list.

## 🛠️ Build Requirements Summary

- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Java 17 (included with Android Studio)
- **Android SDK**: API 24-34
- **Gradle**: 8.4 (included in project)
- **Internet**: Required for first build (downloading dependencies)
- **Disk Space**: ~5 GB total

## ❓ Need Help?

1. **Quick answer**: Check the troubleshooting section in **QUICKSTART.md**
2. **Detailed help**: See troubleshooting in **BUILD.md**
3. **Still stuck**: Open an issue on GitHub with:
   - Your operating system
   - Android Studio version
   - Error message
   - Steps you've tried

## 📞 Support Resources

- **GitHub Issues**: For bug reports and feature requests
- **Documentation**: Start with this file, follow links as needed
- **Project README**: [README.md](README.md) for application details

---

**Ready to start?** Pick a guide above and begin building! We recommend starting with [QUICKSTART.md](QUICKSTART.md) for most users.
