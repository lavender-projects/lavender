import android.annotation.SuppressLint

plugins {
    val versions = de.honoka.gradle.buildsrc.Versions.App
    //plugins
    id("com.android.application") version versions.android
    kotlin("android") version versions.kotlin
}

android {
    namespace = "de.honoka.lavender.android"
    compileSdk = 33

    defaultConfig {
        applicationId = "de.honoka.lavender.android"
        minSdk = 26
        @SuppressLint("OldTargetApi")
        targetSdk = 33
        versionCode = 1
        versionName = "1.0.0-dev"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            @Suppress("UnstableApiUsage")
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
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("de.honoka.sdk:honoka-android-utils:1.0.1")
    implementation("cn.hutool:hutool-all:5.8.18")
    implementation("org.nanohttpd:nanohttpd:2.3.1")
    implementation("com.j256.ormlite:ormlite-android:5.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}