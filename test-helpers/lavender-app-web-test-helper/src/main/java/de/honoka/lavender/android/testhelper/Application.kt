@file:JvmName("LavenderAndroidTestHelperApplicationKt")

package de.honoka.lavender.android.testhelper

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class LavenderAndroidTestHelperApplication

@EnableConfigurationProperties(MainProperties::class)
@Configuration
class MainConfig

@ConfigurationProperties("lavender.test-helper")
data class MainProperties(

    var proxyPassUrlPrefix: String? = null
)

fun main(args: Array<String>) {
    runApplication<LavenderAndroidTestHelperApplication>(*args)
}