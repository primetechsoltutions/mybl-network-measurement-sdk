plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
}

android {
    namespace = "com.ptsl.networksdk_event"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ptsl.networksdk_event"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources=true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources=false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(project(mapOf("path" to ":network-sdk")))
//    implementation("com.ptsl:net-monitor:1.0.0")
//    implementation("com.github.primetechsoltutions:mybl-network-measurement-sdk:1.0.0")


    implementation("com.google.dagger:hilt-android:2.57")
    implementation("androidx.hilt:hilt-work:1.1.0-alpha01")
    kapt("com.google.dagger:hilt-android-compiler:2.57")
    implementation("com.google.android.gms:play-services-location:21.3.0")



}