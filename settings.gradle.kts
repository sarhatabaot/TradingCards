rootProject.name = "TradingCards"

include(":tradingcards-extras")
include(":tradingcards-api")
include(":tradingcards-plugin")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("jooq", "3.17.8")
            library("jooq", "org.jooq","jooq").versionRef("jooq")
            library("jooq-codegen", "org.jooq", "jooq-codegen").versionRef("jooq")
            library("jooq-meta", "org.jooq", "jooq-meta").versionRef("jooq")
            library("jooq-meta-extensions","org.jooq","jooq-meta-extensions").versionRef("jooq")
            library("annotations", "org.jetbrains:annotations:24.0.1")
            library("spigot-api", "org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
            library("nbt-api", "de.tr7zw:item-nbt-api-plugin:2.11.2")
            library("kraken-core", "com.github.sarhatabaot:krakencore:1.6.2")
            library("vault-api", "com.github.MilkBowl:VaultAPI:1.7.1")
            library("treasury-api", "me.lokka30:treasury-api:1.2.1")
            
            version("configurate", "4.1.2")
            library("configurate-core", "org.spongepowered", "configurate-core").versionRef("configurate")
            library("configurate-yaml", "org.spongepowered", "configurate-yaml").versionRef("configurate")
            
            library("lombok","org.projectlombok:lombok:1.18.26")
            
            library("adventure-api", "net.kyori:adventure-api:4.12.0")
            library("adventure-bukkit","net.kyori:adventure-platform-bukkit:4.2.0")
            
            library("bstats", "org.bstats:bstats-bukkit:3.0.1")
            library("acf", "co.aikar:acf-paper:0.5.1-SNAPSHOT")
            library("placeholder-api","me.clip:placeholderapi:2.11.2")
            library("mockito", "org.mockito:mockito-core:5.1.1")
            library("mockbukkit", "com.github.seeseemelk:MockBukkit-v1.18:2.85.2")
            library("hikaricp", "com.zaxxer:HikariCP:5.0.1")
            library("zip4j", "net.lingala.zip4j:zip4j:2.11.4")
            library("flyway", "org.flywaydb:flyway-mysql:9.15.0")
            
            version("rng", "1.5")
            library("rng-core", "org.apache.commons","commons-rng-core").versionRef("rng")
            library("rng-simple", "org.apache.commons", "commons-rng-simple").versionRef("rng")
            library("rng-sampling", "org.apache.commons", "commons-rng-sampling").versionRef("rng")
            
            library("junit-platform","org.junit:junit-bom:5.9.2")
            library("junit-jupiter", "org.junit.jupiter","junit-jupiter").withoutVersion()
        }
    }
}

pluginManagement {
    resolutionStrategy {
        eachPlugin {
            requested.apply {
                if ("$id".startsWith("com.github.sarhatabaot")) {
                    useModule("com.github.sarhatabaot.messages-gradle-plugin:com.github.sarhatabaot.messages.gradle.plugin:$version")
                }
            }
        }
    }
    repositories {
        gradlePluginPortal()
        maven (
            url = uri("https://jitpack.io")
        )
    }
}