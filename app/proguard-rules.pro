
-keep class com.peter_kameel.fos_au.** { *; }
######################## Retrofit ######################################
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keep class com.peter_kameel.fos_au.pojo.UserRequest { *; }
-keep class com.peter_kameel.fos_au.pojo.DataRequest { *; }
-keep class com.peter_kameel.fos_au.pojo.CoarseEntity { *; }
-keep class com.peter_kameel.fos_au.pojo.SemesterEntity { *; }
