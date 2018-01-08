# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\Pruthviraj\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Basic proguard rules
-optimizations !code/simplification/arithmetic
-keepattributes Annotation
-keepattributes InnerClasses
-keepattributes EnclosingMethod
-keep class *.R$
-keep class com.squareup.okhttp.** { *; }

-dontskipnonpubliclibraryclasses
-forceprocessing
-optimizationpasses 5
-overloadaggressively

# Removing logging code
-assumenosideeffects class android.util.Log {
public static *** d(...);
public static *** v(...);
public static *** i(...);
public static *** w(...);
public static *** e(...);
}

# The -dontwarn option tells ProGuard not to complain about some artefacts in the Scala runtime

-dontwarn android.support.**
-dontwarn android.app.Notification
-dontwarn org.apache.**
-dontwarn com.google.common.**
-dontwarn org.w3c.dom.**
-dontwarn com.squareup.picasso.**
-dontwarn com.android.**

-keep public class com.google.android.gms.**
-dontwarn com.google.android.gms.**