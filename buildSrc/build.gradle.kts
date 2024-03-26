plugins {
    // Support convention plugins written in Kotlin. Convention plugins are build scripts in 'src/main' that automatically become available as plugins in the main build.
    `kotlin-dsl`
    id("org.sonarqube") version "5.0.0.4638"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
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