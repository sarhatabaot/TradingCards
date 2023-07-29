import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm")
}


group = "net.tinetwork.tradingcards"

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "17"
}

repositories {
    mavenCentral()
    maven(
        url = "https://papermc.io/repo/repository/maven-public/"
    )
    maven(
        url = "https://repo.codemc.org/repository/maven-public/"
    )
    maven(
        url = "https://jitpack.io"
    )
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = groupId
            artifactId = artifactId
            version = version
            
            from(components["java"])
        }
    }
    repositories {
        val mavenUrl: String? by project
        val mavenSnapshotUrl: String? by project

        (if(version.toString().endsWith("SNAPSHOT")) mavenSnapshotUrl else mavenUrl)?.let { url ->
            maven(url) {
                val mavenUsername: String? by project
                val mavenPassword: String? by project
                if(mavenUsername != null && mavenPassword != null) {
                    credentials {
                        username = mavenUsername
                        password = mavenPassword
                    }
                }
            }
        }
    }
}


