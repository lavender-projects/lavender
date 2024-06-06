import android.annotation.SuppressLint

plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "de.honoka.lavender.android"
    compileSdk = libs.versions.android.sdk.compile.get().toInt()

    defaultConfig {
        applicationId = "de.honoka.lavender.android"
        minSdk = libs.versions.android.sdk.min.get().toInt()
        @SuppressLint("OldTargetApi")
        targetSdk = libs.versions.android.sdk.compile.get().toInt()
        versionCode = libs.versions.app.version.code.get().toInt()
        versionName = libs.versions.app.version.name.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            @Suppress("UnstableApiUsage")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    packaging {
        resources.excludes.addAll(listOf(
            "META-INF/INDEX.LIST",
            "META-INF/io.netty.versions.properties"
        ))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = sourceCompatibility
    }

    kotlinOptions {
        jvmTarget = compileOptions.sourceCompatibility.toString()
    }
}

@Suppress("GradleDependency")
//noinspection UseTomlInstead
dependencies {
    implementation("androidx.core:core-ktx:1.8.0")
    implementation("androidx.appcompat:appcompat:1.5.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    //implementation("de.honoka.lavender:lavsource-app-sdk:${Versions.lavenderAppSdk}")
    implementation("cn.hutool:hutool-all:5.8.18")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}