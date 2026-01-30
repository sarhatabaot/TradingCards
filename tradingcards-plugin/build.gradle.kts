import org.jooq.meta.jaxb.Property
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.jooq.meta.kotlin.*

plugins {
    id("net.tinetwork.tradingcards.java-conventions")
    alias(libs.plugins.plugin.yml.bukkit)
    alias(libs.plugins.shadow)
    alias(libs.plugins.jooq)
    
    jacoco
}

version = "5.7.22"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.aikar.co/content/groups/aikar/")
    maven("https://mvn.lumine.io/repository/maven-public/")
    maven("https://mvn.lumine.io/repository/maven-snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.glaremasters.me/repository/towny/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.spigot.api)

    //soft-depends
    compileOnly(libs.vault.api)
    compileOnly(libs.treasury.api)
    compileOnly(libs.placeholder.api)
    compileOnly(libs.towny)
    compileOnly(libs.mobarena)
    compileOnly(libs.mythicmobs)


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
    library(libs.rng.api)
    library(libs.rng.core)
    library(libs.rng.sampling)
    library(libs.rng.simple)
    library(libs.caffeine)
    library(libs.commons.lang)
    
    jooqGenerator(project(":tradingcards-extras"))
    jooqGenerator(libs.jooq.mysql.connector)
    jooqGenerator(libs.jooq.meta.extensions)

    testCompileOnly(libs.spigot.api)
    testImplementation(libs.mockito)
    testImplementation(libs.mockbukkit)
    testImplementation(platform(libs.junit.platform))
    testImplementation(libs.junit.jupiter)
}

val profile: String by project
lateinit var time: Instant

tasks {
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
        
        configurations {
            create("main") {
                if(profile != "development") {
                    generateSchemaSourceOnCompilation.set(false)
                }
                jooqConfiguration {
//
                    generator {
                        strategy.name = "net.tinetwork.tradingcards.PrefixNamingStrategy"
                        database {
                            name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                            properties {
                                property {
                                    key = "scripts"
                                    value = schemaPath
                                }
                                property {
                                    key = "dialect"
                                    value = "MYSQL"
                                }

                                property {
                                    key = "sort"
                                    value = "flyway"
                                }

                                property {
                                    key = "unqualifiedSchema"
                                    value = "none"
                                }
                            }
                        }
                        target {
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
        relocate("de.tr7zw.changeme.nbtapi", "${project.group}.nbt")
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
    description = "Create custom collectible cards."
    main = "net.tinetwork.tradingcards.tradingcardsplugin.TradingCards"
    website = "https://github.com/sarhatabaot/TradingCards"
    authors = listOf("Xenoyia", "sarhatabaot")
    apiVersion = "1.21"
    softDepend = listOf("Vault", "PlaceholderAPI", "Treasury", "TownyAdvanced", "Towny", "MythicMobs", "MobArena")
}

jacoco {
    toolVersion = "0.8.14"
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                implementation(project(":tradingcards-plugin"))
                implementation(libs.spigot.api)
                implementation(libs.junit.jupiter)
            }

            targets {
                all {
                    testTask.configure {
                        useJUnitPlatform()
                    }
                }
            }
        }
    }
}
tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
