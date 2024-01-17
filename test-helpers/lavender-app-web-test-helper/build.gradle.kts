import de.honoka.gradle.buildsrc.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    //plugins块中不会读取import语句中导入的类
    //引入Versions对象时，必须像这样引入（https://github.com/gradle/gradle/issues/9270）
    @Suppress("RemoveRedundantQualifierName")
    val versions = de.honoka.gradle.buildsrc.Versions
    //plugins
    java
    id("org.springframework.boot") version versions.springBoot
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version versions.kotlin
    kotlin("plugin.spring") version versions.kotlin
}

group = "de.honoka.lavender"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}")
    implementation("de.honoka.sdk:honoka-utils:${Versions.honokaUtils}")
    implementation("de.honoka.sdk:honoka-framework-utils:${Versions.honokaFrameworkUtils}")
    implementation("cn.hutool:hutool-all:5.8.18")
    //Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = java.targetCompatibility.toString()
    }

    test {
        useJUnitPlatform()
    }
}