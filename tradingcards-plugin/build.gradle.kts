import org.jooq.meta.jaxb.Property
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    id("net.tinetwork.tradingcards.java-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("nu.studer.jooq") version "8.2.1"
    id("com.github.sarhatabaot.messages") version "1.0.6"
    
    jacoco
}

version = "5.7.17"

repositories {
    maven(
        url = "https://repo.aikar.co/content/groups/aikar/"
    )
    maven(
        url = "https://mvn.lumine.io/repository/maven-snapshots/"
    )
    maven(
        url = "https://repo.extendedclip.com/content/repositories/placeholderapi/"
    )
}


dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.vault.api)
    compileOnly(libs.treasury.api)
    compileOnly(libs.placeholder.api)
    
    implementation(libs.kraken.core)
    implementation(project(":tradingcards-api"))
    implementation(libs.nbt.api)
    implementation(libs.acf)
    implementation(libs.bstats)
    
    library(libs.zip4j)
    library(libs.hikaricp)
    library(libs.flyway)
    library(libs.annotations)
    library(libs.configurate.yaml)
    library(libs.adventure.api)
    library(libs.adventure.bukkit)
    library(libs.jooq)
    library(libs.jooq.codegen)
    library(libs.jooq.meta)
    library(libs.rng.core)
    library(libs.rng.sampling)
    library(libs.rng.simple)
    
    jooqGenerator(project(":tradingcards-extras"))
    jooqGenerator("com.mysql:mysql-connector-j:8.0.33")
    jooqGenerator(libs.jooq.meta.extensions)
    
    testImplementation(libs.mockito)
    testImplementation(libs.mockbukkit)
    testImplementation(platform(libs.junit.platform))
    testImplementation(libs.junit.jupiter)
}

val profile: String by project
lateinit var time: Instant

tasks {
    generateMessages {
        messages {
            register("internal") {
                fileType = com.github.sarhatabaot.messages.model.FileType.JSON
                isOverwriteClasses = true
                sourceFolder = "internal-messages"
                targetPackage = "net.tinetwork.tradingcards.tradingcardsplugin.messages.internal"
                privateConstructor = "InternalExceptions.UTIL_CLASS"
                basePath = "src/main/java/"
                baseDir = "$projectDir"
            }
            
            register("settings") {
                fileType = com.github.sarhatabaot.messages.model.FileType.YAML
                isOverwriteClasses = true
                sourceFolder = "${projectDir}/src/main/resources/settings"
                targetPackage = "net.tinetwork.tradingcards.tradingcardsplugin.messages.settings"
                privateConstructor = "InternalExceptions.UTIL_CLASS"
                basePath = "src/main/java/"
                baseDir = "$projectDir"
            }
        }
    }
    
    compileJava {
        options.compilerArgs.add("-parameters")
        options.isFork = true
    }
    build {
        dependsOn(shadowJar)
    }
    time = Instant.now()
    val finalName: String = getFinalName(profile, time)
    val schemaVersion: String by project
    val schemaPath = "src/main/resources/db/base/$schemaVersion"
    
    jooq {
        version.set(libs.versions.jooq)
        edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)
        
        configurations {
            create("main") {
                if(profile != "development") {
                    generateSchemaSourceOnCompilation.set(false)
                }
                jooqConfiguration.apply {
                    jdbc = null
                    generator.apply {
                        strategy.name = "net.tinetwork.tradingcards.PrefixNamingStrategy"
                        database.apply {
                            name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                            properties.add(Property().withKey("scripts").withValue(schemaPath))
                            properties.add(Property().withKey("dialect").withValue("MYSQL"))
                            properties.add(Property().withKey("sort").withValue("flyway"))
                            properties.add(Property().withKey("unqualifiedSchema").withValue("none"))
                        }
                        target.apply {
                            packageName = "net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated"
                            directory = "src/main/generated/"
                        }
                    }
                    
                }
            }
        }
    }
    
    
    shadowJar {
        minimize()
        archiveFileName.set(finalName)
        archiveClassifier.set("shadow")
        relocate("co.aikar.commands", "${project.group}.acf")
        relocate("co.aikar.locales", "${project.group}.locales")
        relocate("de.tr7zw.nbtapi", "${project.group}.nbt")
        relocate("org.bstats", "${project.group}}.bstats")
        
        dependencies {
            exclude(dependency("com.h2database:h2"))
            exclude(dependency("org.jooq:.*:.*"))
        }
    }
}

fun getFinalName(profile: String, instant: Instant): String {
    val buildNumber: String by project
    return when(profile) {
        "release" -> "TradingCards-${project.version}.jar"
        "bleeding-edge" -> "TradingCards-${project.version}-${buildNumber}.jar"
        else -> {
            val time = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm", Locale.ENGLISH)
                .withZone(ZoneId.systemDefault())
                .format(instant)
            "TradingCards-${project.version}-${time}.jar"
        }
    }
}
fun getPluginVersion(profile: String, instant: Instant): String {
    return when(profile) {
        "release" -> project.version.toString()
        "bleeding-edge" -> {
            val buildNumber: String by project
            "${project.version}-${buildNumber}"
        }
        else -> {
            val time = DateTimeFormatter.ofPattern("yyyyMMdd-HHmm", Locale.ENGLISH)
                .withZone(ZoneId.systemDefault())
                .format(instant)
            "${project.version}-${profile}-${time}"
        }
    }
}
bukkit {
    name = rootProject.name
    version = getPluginVersion(profile, time)
    description = project.description.toString()
    main = "net.tinetwork.tradingcards.tradingcardsplugin.TradingCards"
    website = "https://github.com/sarhatabaot/TradingCards"
    authors = listOf("Xenoyia", "sarhatabaot")
    apiVersion = "1.18"
}

jacoco {
    toolVersion = "0.8.10"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
