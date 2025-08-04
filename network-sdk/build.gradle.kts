//plugins {
//    alias(libs.plugins.android.library)
//    alias(libs.plugins.jetbrains.kotlin.android)
//    id("kotlin-kapt")
//    id("kotlin-parcelize")
//    id("com.google.dagger.hilt.android")
//    id("maven-publish")
//}
//
//android {
//    namespace = "com.ptsl.network_sdk"
//    compileSdk = 34
//    buildFeatures{
//        buildConfig = true
//    }
//
//    defaultConfig {
//        minSdk = 21
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
//
//        val baseURL = project.findProperty("BASE_URL") as String? ?: "BASE_URL"
//        buildConfigField("String", "BASE_URL", "\"$baseURL\"")
//
//        val token = project.findProperty("TOKEN") as String? ?: "TOKEN"
//        buildConfigField("String", "TOKEN", "\"$token\"")
//
//        val sdkVersion = project.findProperty("SdkVersion") as String? ?: "SdkVersion"
//        buildConfigField("String", "SdkVersion", "\"$sdkVersion\"")
//
//    }
//
//    buildTypes {
//        release {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//        debug {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
//    }
//    kotlinOptions {
//        jvmTarget = "1.8"
//    }
//}
//
//dependencies {
//
//    implementation(libs.androidx.core.ktx)
//    implementation(libs.androidx.appcompat)
//    implementation(libs.material)
//    testImplementation(libs.junit)
//    androidTestImplementation(libs.androidx.junit)
//    androidTestImplementation(libs.androidx.espresso.core)
//
//    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.9")
//
//    api("com.google.dagger:hilt-android:2.48")
//    api("androidx.hilt:hilt-common:1.1.0")
//    api("androidx.work:work-runtime-ktx:2.8.1")
//    kapt ("androidx.hilt:hilt-compiler:1.1.0")
//    api ("androidx.hilt:hilt-work:1.1.0-alpha01")
//    kapt("com.google.dagger:hilt-android-compiler:2.48")
//    api("androidx.room:room-ktx:2.6.1")
//    kapt("androidx.room:room-compiler:2.6.1")
//
//    api("app.netmonster:core:1.2.0")
//    api("com.google.android.gms:play-services-location:21.0.1")
//    api ("com.guolindev.permissionx:permissionx:1.8.1")
//
//
//    api("com.squareup.okhttp3:okhttp:4.9.0")
//    api("com.squareup.okhttp3:logging-interceptor:4.9.0")
//    api("com.squareup.retrofit2:retrofit:2.9.0")
//    api("com.squareup.retrofit2:converter-gson:2.9.0")
//}
//
//kapt {
//    correctErrorTypes = true
//}
//
//// Task to create a source jar
//tasks.register<Jar>("sourceJar") {
//    archiveClassifier.set("sources")
//    from(android.sourceSets["main"].java.srcDirs)
//}
//
//publishing {
//    publications {
//        create<MavenPublication>("release") {
//            groupId = "com.ptsl"
//            artifactId = "net-monitor"
//            version = "1.0.0"
//
//            // Include sources
//            artifact(tasks["sourceJar"])
//
//            // Define the artifact (.aar file)
//            artifact("$buildDir/outputs/aar/${project.name}-release.aar") // Use release here
//
//            pom.withXml {
//                asNode().appendNode("dependencies").apply {
//                    configurations["implementation"].allDependencies.forEach { dep ->
//                        if (dep.group != null) {
//                            appendNode("dependency").apply {
//                                appendNode("groupId", dep.group)
//                                appendNode("artifactId", dep.name)
//                                appendNode("version", dep.version ?: "unspecified")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
////        create<MavenPublication>("debug") {
////            groupId = "com.ptsl"
////            artifactId = "net-monitor"
////            version = "1.0.0"
////
////            // Include sources
////            artifact(tasks["sourceJar"])
////
////            // Define the artifact (.aar file)
////            artifact("$buildDir/outputs/aar/${project.name}-debug.aar") // Use release here
////
////            pom.withXml {
////                asNode().appendNode("dependencies").apply {
////                    configurations["implementation"].allDependencies.forEach { dep ->
////                        if (dep.group != null) {
////                            appendNode("dependency").apply {
////                                appendNode("groupId", dep.group)
////                                appendNode("artifactId", dep.name)
////                                appendNode("version", dep.version ?: "unspecified")
////                            }
////                        }
////                    }
////                }
////            }
////        }
//    }
//
//    repositories {
//        mavenLocal()
//        maven {
//            url = uri("$buildDir/outputs/aar") // Use a separate directory for the Maven repository
//        }
//    }
//}


//------------------------------


plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("maven-publish")
}

android {
    namespace = "com.ptsl.network_sdk"
    compileSdk = 34
    buildFeatures{
        buildConfig = true
    }

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val baseURL = project.findProperty("BASE_URL") as String? ?: "BASE_URL"
        buildConfigField("String", "BASE_URL", "\"$baseURL\"")

        val token = project.findProperty("TOKEN") as String? ?: "TOKEN"
        buildConfigField("String", "TOKEN", "\"$token\"")

        val sdkVersion = project.findProperty("SdkVersion") as String? ?: "SdkVersion"
        buildConfigField("String", "SdkVersion", "\"$sdkVersion\"")

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.9")

    // Hilt and WorkManager
    implementation("com.google.dagger:hilt-android:2.48")
    implementation("androidx.hilt:hilt-common:1.1.0")
    implementation("androidx.hilt:hilt-work:1.1.0-alpha01")
    kapt("com.google.dagger:hilt-android-compiler:2.48")
    kapt("androidx.hilt:hilt-compiler:1.1.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Room
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Permissions and location
    implementation("com.guolindev.permissionx:permissionx:1.8.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // NetMonster
    implementation("app.netmonster:core:1.2.0")

    // Networking (Retrofit + OkHttp)
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}

// ✅ Maven publishing block with dependency injection into POM
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.github.primetechsoltutions"
                artifactId = "mybl-network-measurement-sdk"
                version = "1.0.0"

                pom {
                    name.set("MyBL Network Measurement SDK")
                    description.set("Network measurement and data uploader SDK.")
                    url.set("https://github.com/primetechsoltutions/mybl-network-measurement-sdk")
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("primetechsoltutions")
                            name.set("PrimeTech Solutions")
                            email.set("you@example.com") // optional
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/primetechsoltutions/mybl-network-measurement-sdk.git")
                        developerConnection.set("scm:git:ssh://git@github.com:primetechsoltutions/mybl-network-measurement-sdk.git")
                        url.set("https://github.com/primetechsoltutions/mybl-network-measurement-sdk")
                    }

                    withXml {
                        val dependenciesNode = asNode().appendNode("dependencies")
                        configurations.implementation.get().dependencies.forEach {
                            if (it.group != null && it.name != null) {
                                val depNode = dependenciesNode.appendNode("dependency")
                                depNode.appendNode("groupId", it.group)
                                depNode.appendNode("artifactId", it.name)
                                depNode.appendNode("version", it.version ?: "unspecified")
                                depNode.appendNode("scope", "compile")
                            }
                        }
                    }
                }
            }
        }
    }
}
