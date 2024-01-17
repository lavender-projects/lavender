@file:JvmName("LavenderAndroidTestHelperApplicationKt")

package de.honoka.lavender.android.testhelper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class LavenderAndroidTestHelperApplication

fun main(args: Array<String>) {
    runApplication<LavenderAndroidTestHelperApplication>(*args)
}