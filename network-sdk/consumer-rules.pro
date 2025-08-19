## --- WorkManager core rules ---
## Keep all ListenableWorker subclasses (base for Worker, CoroutineWorker, etc.)
#-keep class androidx.work.ListenableWorker { *; }
#-keep class androidx.work.Worker { *; }
#-keep class androidx.work.CoroutineWorker { *; }
#
## Keep your custom worker class (adjust package as needed)
#-keep class com.ptsl.network_sdk.** extends androidx.work.ListenableWorker { *; }
#
## Keep generated Hilt worker factories
#-keepclassmembers class ** {
#    @dagger.hilt.android.HiltAndroidApp *;
#}
#
## --- Hilt / Dagger rules ---
#-keep class dagger.hilt.** { *; }
#-keep class javax.inject.** { *; }
#-dontwarn dagger.hilt.**
#-dontwarn javax.inject.**
#
## Keep startup provider used by WorkManager and Hilt
#-keep class androidx.startup.InitializationProvider { *; }
#-keep class androidx.startup.InitializationProvider
#
## Avoid warnings
#-dontwarn androidx.work.**
#
#
#
##----------------------------- uu---------------------------
#
######################################
## ✅ GENERAL KEEP CONFIGURATIONS
######################################
#
## Keep annotations and signatures
#-keepattributes Annotation, Signature,
#    RuntimeVisibleAnnotations, RuntimeInvisibleAnnotations,
#    RuntimeVisibleParameterAnnotations, RuntimeInvisibleParameterAnnotations,
#    AnnotationDefault, EnclosingMethod, InnerClasses
#
## Keep Kotlin Metadata for reflection
#-keep class kotlin.Metadata { *; }
#
## Kotlin coroutines
#-keepclassmembers class kotlin.coroutines.Continuation {
#    <fields>;
#    <methods>;
#}
#-keep class kotlin.coroutines.Continuation { *; }
#-keep class kotlinx.coroutines.** { *; }
#-keep class kotlinx.coroutines.internal.** { *; }
#-dontwarn kotlin.coroutines.**
#-dontwarn kotlinx.coroutines.flow.**
#
## Top-level functions that can only be used by Kotlin.
#-dontwarn retrofit2.KotlinExtensions
#-dontwarn retrofit2.KotlinExtensions$*
#-dontwarn kotlin.Unit
#
######################################
## ✅ GSON (DATA MODELS)
######################################
#
## Keep Gson model classes and fields
#-keep class com.ptsl.network_sdk.data_model.data_model.** { *; }
#-keep class com.ptsl.network_sdk.data_model.BaseResponse { *; }
#
## Allow Gson to access @SerializedName fields via reflection
#-keepclassmembers class * {
#    @com.google.gson.annotations.SerializedName <fields>;
#}
#
######################################
## ✅ NETWORK SERVICE CORE
######################################
#
#-keep class com.ptsl.network_sdk.** { *; }
#-keep class com.ptsl.network_sdk.NetworkDataUploader {
#    public *;
#}
#
##-keep class com.ptsl.rso.network_service.data_model.** { *; }
##-keep class com.ptsl.rso.network_service.NetworkDataUploader {
##    public *;
##}
#
######################################
## ✅ RETROFIT & OKHTTP
######################################
#
## Retrofit core
#-keep interface retrofit2.* { *; }
#-keep class retrofit2.** { *; }
#
## Retrofit response and converter
#-keep class retrofit2.Call
#-keep class retrofit2.Callback
#-keep class retrofit2.Converter$Factory
#-keep class retrofit2.converter.gson.** { *; }
#
## Retrofit reflection on generic parameters
#-if interface * { @retrofit2.http.* <methods>; }
#-keep,allowobfuscation interface <1>
#
## Keep inherited services
#-if interface * { @retrofit2.http.* <methods>; }
#-keep,allowobfuscation interface * extends <1>
#
## Retrofit response (e.g., Response<T>)
#-keep,allowobfuscation,allowshrinking class retrofit2.Response
#
## Retrofit method & parameter annotations
#-keepclassmembers,allowshrinking,allowobfuscation interface * {
#    @retrofit2.http.* <methods>;
#}
#
## OkHttp and okio
#-keep class okhttp3.** { *; }
#-keep class okio.** { *; }
#
## Disable OkHttp debug logging interceptor
#-dontwarn okhttp3.logging.**
#-keep class okhttp3.logging.HttpLoggingInterceptor { *; }
#-assumenosideeffects class okhttp3.logging.HttpLoggingInterceptor {
#    public <init>(...);
#    public *** intercept(...);
#}
#
######################################
## ✅ ROOM DATABASE
######################################
#
#-keep class androidx.room.** { *; }
#
#-keepclassmembers class * {
#    @androidx.room.* <methods>;
#    @androidx.room.* <fields>;
#}
#
#-keep class * extends androidx.room.RoomDatabase { *; }
#-keep class * implements androidx.room.RoomDatabase { *; }
#
## Keep Room DAO interfaces
#-keep interface * {
#    @androidx.room.Dao <methods>;
#}
#
## For coroutines/Flow in Room
#-dontwarn kotlin.coroutines.**
#
######################################
## ✅ DAGGER / HILT
######################################
#
#-keepattributes Annotation
#
## Hilt core
#-keep class dagger.hilt.** { *; }
#-keep class dagger.hilt.internal.** { *; }
#-keep class dagger.hilt.android.internal.** { *; }
#
## Dagger core
#-keep class dagger.** { *; }
#-keep interface dagger.** { *; }
#-keep class dagger.internal.** { *; }
#
## javax.inject
#-keep class javax.inject.** { *; }
#
## Keep classes with dagger/hilt annotations
#-keep class * {
#    @dagger.hilt.InstallIn <methods>;
#    @dagger.Module <methods>;
#    @dagger.hilt.EntryPoint <methods>;
#    @dagger.Binds <methods>;
#    @dagger.Provides <methods>;
#    @javax.inject.Inject <fields>;
#    @dagger.hilt.android.HiltAndroidApp <methods>;
#    @dagger.hilt.android.AndroidEntryPoint <methods>;
#}
#
## Required to prevent stripping of injected constructors, fields, and methods
#-keepclasseswithmembers class * {
#    @javax.inject.Inject <init>(...);
#}
#-keepclasseswithmembers class * {
#    @javax.inject.Inject *;
#}
#
######################################
## ✅ PERMISSIONX
######################################
#
#-keep class com.guolindev.permissionx.** { *; }
#
######################################
## ✅ OPTIONAL IGNORE/DON’T WARN RULES
######################################
#
## BouncyCastle
#-dontwarn org.bouncycastle.jsse.BCSSLParameters
#-dontwarn org.bouncycastle.jsse.BCSSLSocket
#-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
#
## Conscrypt
#-dontwarn org.conscrypt.Conscrypt$Version
#-dontwarn org.conscrypt.Conscrypt
#-dontwarn org.conscrypt.ConscryptHostnameVerifier
#
## OpenJSSE
#-dontwarn org.openjsse.javax.net.ssl.SSLParameters
#-dontwarn org.openjsse.javax.net.ssl.SSLSocket
#-dontwarn org.openjsse.net.ssl.OpenJSSE
#
## Animal Sniffer plugin (used in Retrofit builds)
#-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#
## JSR 305 nullability annotations
#-dontwarn javax.annotation.**
#
#
## Kotlin Parcelize
#-keep class kotlinx.parcelize.Parcelize
#-keep @kotlinx.parcelize.Parcelize class * implements android.os.Parcelable {
#    <fields>;
#    <methods>;
#}
#
##----------------------------- uu---------------------------
#
#
## Please add these rules to your existing keep rules in order to suppress warnings.
## This is generated automatically by the Android Gradle plugin.
#-dontwarn org.bouncycastle.jsse.BCSSLParameters
#-dontwarn org.bouncycastle.jsse.BCSSLSocket
#-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
#-dontwarn org.conscrypt.Conscrypt$Version
#-dontwarn org.conscrypt.Conscrypt
#-dontwarn org.conscrypt.ConscryptHostnameVerifier
#-dontwarn org.openjsse.javax.net.ssl.SSLParameters
#-dontwarn org.openjsse.javax.net.ssl.SSLSocket
#-dontwarn org.openjsse.net.ssl.OpenJSSE
#-dontwarn org.bouncycastle.**
#-dontwarn org.conscrypt.**
#-dontwarn org.openjsse.**


# --------- Preserve SDK Namespace ---------
# Ensure classes remain in com.ptsl.network_sdk.* namespace
-keeppackagenames com.ptsl.network_sdk.**
-repackageclasses 'com.ptsl.network_sdk'
# --------- General SDK Protection ---------
-keep class com.ptsl.network_sdk.** { *; }
-dontwarn com.ptsl.network_sdk.**

# --------- Retrofit & OkHttp ---------
-keepattributes Signature
-keepattributes *Annotation*
-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**

# --------- Gson ---------
-keep class com.google.gson.** { *; }
-keep class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-dontwarn com.google.gson.**

# --------- Room ---------
-keepclassmembers class * {
    @androidx.room.* <methods>;
}
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# --------- Hilt / Dagger ---------
-keep class dagger.** { *; }
-dontwarn dagger.**
-keep class javax.inject.** { *; }
-dontwarn javax.inject.**
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**
-keep class androidx.hilt.** { *; }

# --------- WorkManager ---------
-keep class androidx.work.** { *; }
-dontwarn androidx.work.**

# --------- Optional TLS Providers ---------
-dontwarn org.conscrypt.**
-keep class org.conscrypt.** { *; }

-dontwarn org.bouncycastle.**
-keep class org.bouncycastle.** { *; }

-dontwarn org.openjsse.**
-keep class org.openjsse.** { *; }

# --------- Android Reflection Safety ---------
-keepclassmembers class * {
    public <init>(...);
}
-keepclassmembers class * {
    *** *(...);
}


# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.android.Version
-dontwarn com.android.build.api.instrumentation.AsmClassVisitorFactory
-dontwarn com.android.build.api.instrumentation.ClassContext
-dontwarn com.android.build.api.instrumentation.ClassData
-dontwarn com.android.build.api.instrumentation.FramesComputationMode
-dontwarn com.android.build.api.instrumentation.InstrumentationContext
-dontwarn com.android.build.api.instrumentation.InstrumentationParameters$None
-dontwarn com.android.build.api.instrumentation.InstrumentationScope
-dontwarn com.android.build.api.transform.Context
-dontwarn com.android.build.api.transform.DirectoryInput
-dontwarn com.android.build.api.transform.Format
-dontwarn com.android.build.api.transform.JarInput
-dontwarn com.android.build.api.transform.QualifiedContent$DefaultContentType
-dontwarn com.android.build.api.transform.QualifiedContent$Scope
-dontwarn com.android.build.api.transform.QualifiedContent
-dontwarn com.android.build.api.transform.Status
-dontwarn com.android.build.api.transform.Transform
-dontwarn com.android.build.api.transform.TransformInput
-dontwarn com.android.build.api.transform.TransformInvocation
-dontwarn com.android.build.api.transform.TransformOutputProvider
-dontwarn com.android.build.api.variant.AndroidComponentsExtension$DefaultImpls
-dontwarn com.android.build.api.variant.AndroidComponentsExtension
-dontwarn com.android.build.api.variant.ApplicationAndroidComponentsExtension
-dontwarn com.android.build.api.variant.ApplicationVariant
-dontwarn com.android.build.api.variant.DynamicFeatureAndroidComponentsExtension
-dontwarn com.android.build.api.variant.DynamicFeatureVariant
-dontwarn com.android.build.api.variant.GeneratesApk
-dontwarn com.android.build.api.variant.Instrumentation
-dontwarn com.android.build.api.variant.SourceDirectories$Flat
-dontwarn com.android.build.api.variant.SourceDirectories$Layered
-dontwarn com.android.build.api.variant.Sources
-dontwarn com.android.build.api.variant.Variant
-dontwarn com.android.build.api.variant.VariantInfo
-dontwarn com.android.build.api.variant.VariantSelector
-dontwarn com.android.build.gradle.AppExtension
-dontwarn com.android.build.gradle.api.ApplicationVariant
-dontwarn com.android.build.gradle.internal.dsl.BuildType
-dontwarn com.android.build.gradle.internal.dsl.ProductFlavor
-dontwarn com.android.builder.model.BuildType
-dontwarn com.android.builder.model.ProductFlavor
-dontwarn com.android.ide.common.repository.GradleVersion
-dontwarn com.android.utils.FileUtils
-dontwarn com.deenislam.sdk.MainDirections$ActionGlobalKhatamEQuranHomeFragment
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean
-dontwarn org.apache.commons.io.FileUtils
-dontwarn org.apache.commons.io.IOUtils
-dontwarn org.gradle.api.Action
-dontwarn org.gradle.api.DefaultTask
-dontwarn org.gradle.api.DomainObjectSet
-dontwarn org.gradle.api.GradleException
-dontwarn org.gradle.api.NamedDomainObjectContainer
-dontwarn org.gradle.api.Plugin
-dontwarn org.gradle.api.Project
-dontwarn org.gradle.api.artifacts.Configuration
-dontwarn org.gradle.api.artifacts.ConfigurationContainer
-dontwarn org.gradle.api.artifacts.DependencyResolutionListener
-dontwarn org.gradle.api.artifacts.ResolvableDependencies
-dontwarn org.gradle.api.artifacts.VersionConstraint
-dontwarn org.gradle.api.artifacts.component.ComponentIdentifier
-dontwarn org.gradle.api.artifacts.component.ComponentSelector
-dontwarn org.gradle.api.artifacts.component.ModuleComponentSelector
-dontwarn org.gradle.api.artifacts.result.DependencyResult
-dontwarn org.gradle.api.artifacts.result.ResolutionResult
-dontwarn org.gradle.api.artifacts.result.ResolvedComponentResult
-dontwarn org.gradle.api.file.Directory
-dontwarn org.gradle.api.file.DirectoryProperty
-dontwarn org.gradle.api.file.ProjectLayout
-dontwarn org.gradle.api.file.RegularFile
-dontwarn org.gradle.api.file.RegularFileProperty
-dontwarn org.gradle.api.logging.Logger
-dontwarn org.gradle.api.plugins.AppliedPlugin
-dontwarn org.gradle.api.plugins.ExtensionAware
-dontwarn org.gradle.api.plugins.ExtensionContainer
-dontwarn org.gradle.api.plugins.PluginManager
-dontwarn org.gradle.api.provider.Property
-dontwarn org.gradle.api.provider.Provider
-dontwarn org.gradle.api.tasks.CacheableTask
-dontwarn org.gradle.api.tasks.Input
-dontwarn org.gradle.api.tasks.InputFiles
-dontwarn org.gradle.api.tasks.Internal
-dontwarn org.gradle.api.tasks.OutputDirectory
-dontwarn org.gradle.api.tasks.OutputFile
-dontwarn org.gradle.api.tasks.PathSensitive
-dontwarn org.gradle.api.tasks.PathSensitivity
-dontwarn org.gradle.api.tasks.TaskAction
-dontwarn org.gradle.api.tasks.TaskContainer
-dontwarn org.gradle.api.tasks.TaskProvider
-dontwarn org.gradle.configurationcache.extensions.CharSequenceExtensionsKt
