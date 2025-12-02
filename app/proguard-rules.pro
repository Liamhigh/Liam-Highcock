# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep Verum Omnis core classes
-keep class org.verumomnis.forensic.** { *; }

# Keep Gson serialized classes
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }

# Keep iText PDF classes
-keep class com.itextpdf.** { *; }
-dontwarn com.itextpdf.**

# Keep ZXing classes for QR
-keep class com.google.zxing.** { *; }

# Keep CameraX classes
-keep class androidx.camera.** { *; }

# Keep SLF4J API classes required by iText7 and avoid warnings
-keep class org.slf4j.Logger { *; }
-keep class org.slf4j.LoggerFactory { *; }
-keep class org.slf4j.impl.StaticLoggerBinder { *; }
-dontwarn org.slf4j.**
