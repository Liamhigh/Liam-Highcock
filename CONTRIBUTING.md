# Contributing to Verum Omnis Forensic Engine

Thank you for your interest in contributing to the Verum Omnis Forensic Engine!

## 🛠️ Development Setup

### Prerequisites

1. **Java Development Kit (JDK) 17 or later**
   - [Adoptium/Temurin](https://adoptium.net/) (recommended)
   - [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)

2. **Android SDK**
   - API Level 34 (Android 14)
   - Build Tools 34.0.0+
   - [Download Android Studio](https://developer.android.com/studio) (includes SDK)

3. **Android Studio** (recommended for development)
   - Version: Hedgehog (2023.1.1) or later

### Getting Started

1. **Clone the repository**
   ```bash
   git clone https://github.com/Liamhigh/Liam-Highcock.git
   cd Liam-Highcock
   ```

2. **Configure the Android SDK**
   
   Create a `local.properties` file in the project root:
   ```bash
   # macOS
   echo "sdk.dir=/Users/$USER/Library/Android/sdk" > local.properties
   
   # Linux
   echo "sdk.dir=/home/$USER/Android/Sdk" > local.properties
   
   # Windows (Git Bash)
   echo "sdk.dir=C\:\\Users\\$USERNAME\\AppData\\Local\\Android\\Sdk" > local.properties
   ```

3. **Build the project**
   ```bash
   # Linux/macOS
   ./gradlew assembleDebug
   
   # Windows
   gradlew.bat assembleDebug
   ```

4. **Run tests**
   ```bash
   ./gradlew testDebugUnitTest
   ```

### Opening in Android Studio

1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the cloned repository
4. Android Studio will automatically configure the project

## 📁 Project Structure

```
├── app/                          # Android application module
│   ├── src/main/
│   │   ├── java/org/verumomnis/forensic/
│   │   │   ├── core/            # Core forensic engine
│   │   │   ├── crypto/          # Cryptographic sealing
│   │   │   ├── leveler/         # B1-B9 analysis engine
│   │   │   ├── location/        # GPS location services
│   │   │   ├── pdf/             # PDF report generation
│   │   │   ├── report/          # Narrative generation
│   │   │   └── ui/              # User interface
│   │   ├── res/                 # Android resources
│   │   └── assets/              # Configuration files
│   └── src/test/                # Unit tests
├── docs/                        # Documentation
│   ├── images/                  # Logo and images
│   └── pdfs/                    # Reference documents
├── gradle/                      # Gradle wrapper
├── scripts/                     # Build scripts
└── .github/workflows/           # CI/CD configuration
```

## 🧪 Testing

### Running Unit Tests

```bash
./gradlew testDebugUnitTest
```

### Running Instrumented Tests

Requires a connected Android device or emulator:
```bash
./gradlew connectedAndroidTest
```

## 📝 Code Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Add comments for complex logic
- Document public APIs with KDoc

## 🔒 Security Guidelines

This is a **forensic** application. Security is paramount:

1. **Never commit secrets** - API keys, certificates, signing keys
2. **Offline-first design** - No unnecessary network calls
3. **No telemetry** - Respect user privacy
4. **Verify integrity** - Always calculate and verify hashes
5. **No external logging** - Logs stay on device

## 🐛 Reporting Issues

When reporting issues, please include:

1. Device model and Android version
2. Steps to reproduce
3. Expected behavior
4. Actual behavior
5. Screenshots if applicable
6. Logs if available

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project.

---

**AI FORENSICS FOR TRUTH**
