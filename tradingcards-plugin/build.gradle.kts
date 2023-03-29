import java.time.Instant
import java.time.temporal.ChronoUnit

plugins {
    id("net.tinetwork.tradingcards.java-conventions")
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
    id("com.github.johnrengelman.shadow") version "8.1.0"
    id("nu.studer.jooq") version "8.1"
    id("com.github.sarhatabaot.messages") version "1.0.6"

    jacoco
}

version = "5.7.16"

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
    
    compileOnly(libs.kraken.core)
    implementation(project(":tradingcards-api"))
    implementation(libs.nbt.api)
    implementation(libs.acf)
    library(libs.bstats) //test
    
    
    
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
    jooqGenerator("com.mysql:mysql-connector-j:8.0.32")
    jooqGenerator(libs.jooq.meta.extensions)
    
    testCompileOnly(libs.mockito)
    testCompileOnly(libs.mockbukkit)
}

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
    val profile: String by project
    // release
    build {
        if(profile == "development") {
            dependsOn(jooq)
        }
    
        
        dependsOn(shadowJar)
    }
    
    val finalName: String = getFinalName(profile)
    val schemaVersion: String by project

    jooq {
        version.set(libs.versions.jooq)
        edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)
        
        configurations {
            create("main") {
                jooqConfiguration.apply {
                    jdbc = null
                    generator.apply {
//                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        strategy.name = "net.tinetwork.tradingcards.PrefixNamingStrategy"
                        database.apply {
                            name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                            inputSchema = "src/main/resources/db/base/${schemaVersion}"
                        }
                        target.apply {
                            packageName = "net.tinetwork.tradingcards.tradingcardsplugin.storage.impl.remote.generated"
                            directory = "/src/main/java"
                        }
                    }
                    
                }
            }
        }
    }

    
    shadowJar {
        minimize()
        archiveFileName.set("\"${finalName}\"") //We must escape the '"' here otherwise this won't work. Probably a bug.
        archiveClassifier.set("shadow")
        
        relocate("co.aikar.commands", "${group}.acf")
        relocate("co.aikar.locales", "${group}.locales")
        relocate("de.tr7zw.changeme.nbtapi", "${group}.nbt")
//        relocate("org.bstats", "${group}.bstats")
    
        dependencies {
            exclude("com.h2database:h2")
            exclude("org.jooq:jooq")
            exclude("org.jooq:jooq-codegen-maven")
            exclude("org.jooq:jooq-meta-extensions")
        }
    }
}

fun getFinalName(profile: String): String {
    val buildNumber: String by project
    return when(profile) {
        "release" -> "TradingCards-${project.version}.jar"
        "bleeding-edge" -> "TradingCards-${project.version}-$buildNumber.jar"
        else -> {
            val time = Instant.now().truncatedTo(ChronoUnit.SECONDS).toString()
            "TradingCards-${project.version}-${time}.jar"
        }
    }
}

jacoco {
    toolVersion = "0.8.8"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}
