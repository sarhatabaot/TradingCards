plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
    id("org.sonarqube") version "4.0.0.2929"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.10")
}
repositories {
    // Use the plugin portal to apply community plugins in convention plugins.
    gradlePluginPortal()
}

sonarqube {
    properties {
        property("sonar.projectKey", "TreasureIslandMC_TradingCards")
        property("sonar.organization", "treasureislandmc")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}