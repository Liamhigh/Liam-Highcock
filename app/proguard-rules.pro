# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep forensic engine classes
-keep class org.verumomnis.forensic.** { *; }

# Keep crypto classes for evidence sealing
-keep class org.verumomnis.forensic.crypto.** { *; }
