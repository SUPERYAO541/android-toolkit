# 在啟用 R8 的情況下構建應用時，R8 會按照您指定的路徑和文件名輸出報告
#-printusage classes_removed_by_proguard.txt

# billing https://developer.android.com/google/play/billing/billing_library_overview#Verify-purchase-device
#-keep class com.android.vending.billing.**

# Model
#-keep class com.superyao.dev.toolkit.** { *; }

# ==============================
# Reflection
# ==============================

# ==============================
# JavaScript
# ==============================

# ==============================
# Third party
# ==============================

# === 20151008 Timber https://github.com/JakeWharton/timber/blob/master/timber/consumer-proguard-rules.pro
-dontwarn org.jetbrains.annotations.**

# Remove log
-assumenosideeffects class android.util.Log {
public static boolean isLoggable(java.lang.String, int);
public static int d(...);
public static int w(...);
public static int v(...);
public static int i(...);
public static int e(...);
}
# Remove timber log
-assumenosideeffects class timber.log.Timber* {
public static *** d(...);
public static *** w(...);
public static *** v(...);
public static *** i(...);
# !!! keep for report to crashlytics !!!
#public static *** e(...);
}

# === 20200514 Gson https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg
##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------