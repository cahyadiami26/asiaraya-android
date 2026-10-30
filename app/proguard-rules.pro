# Proguard rules for HRIS Asia Raya
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.SerialName <fields>;
}
-keep class id.co.asiaraya.hris.core.model.** { *; }
