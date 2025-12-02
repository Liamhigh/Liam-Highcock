plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.verumomnis.forensic"
    compileSdk = 34

    defaultConfig {
        applicationId = "org.verumomnis.forensic"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("KEYSTORE_PATH")
            val keystorePass = System.getenv("KEYSTORE_PASSWORD")
            val keyAliasValue = System.getenv("KEY_ALIAS")
            val keyPass = System.getenv("KEY_PASSWORD")
            
            // Only configure signing if all required environment variables are present
            if (keystorePath != null && keystorePass != null && keyAliasValue != null && keyPass != null) {
                val keystoreFile = file(keystorePath)
                if (keystoreFile.exists()) {
                    storeFile = keystoreFile
                    storePassword = keystorePass
                    keyAlias = keyAliasValue
                    keyPassword = keyPass
                } else {
                    logger.warn("Keystore file not found at: $keystorePath - release APK will be unsigned")
                }
            } else {
                logger.warn("Signing environment variables not set - release APK will be unsigned")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Apply signing config - use release if fully configured, otherwise fall back to debug
            val releaseSigningConfig = signingConfigs.getByName("release")
            if (releaseSigningConfig.storeFile != null && 
                releaseSigningConfig.storePassword != null &&
                releaseSigningConfig.keyAlias != null &&
                releaseSigningConfig.keyPassword != null) {
                signingConfig = releaseSigningConfig
            } else {
                // Fall back to debug signing to ensure APK is always signed
                signingConfig = signingConfigs.getByName("debug")
                logger.warn("Release signing not configured - using debug signing config")
            }
        }
        debug {
            isMinifyEnabled = false
            // Explicitly use debug signing config to ensure APK is always signed
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/LICENSE"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
        }
    }
}

dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity-ktx:1.8.2")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // CameraX for document capture
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // PDF Processing (offline-capable) - using iText for Android
    implementation("com.itextpdf:itext7-core:7.2.5")

    // Cryptography
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // JSON parsing
    implementation("com.google.code.gson:gson:2.10.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // QR Code generation
    implementation("com.google.zxing:core:3.5.2")

    // SLF4J binding for Android (required by iText7, prevents R8 missing class errors)
    implementation("org.slf4j:slf4j-android:1.7.36")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.3.1")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
