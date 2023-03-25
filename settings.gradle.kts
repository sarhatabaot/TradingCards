rootProject.name = "TradingCards"

include(":tradingcards-extras")
include(":tradingcards-api")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("jooq", "3.17.8")
            library("jooq", "org.jooq","jooq").versionRef("jooq")
            library("jooq-codegen", "org.jooq", "jooq-codegen").versionRef("jooq")
            library("annotations", "org.jetbrains:annotations:24.0.1")
            library("spigot-api", "org.spigotmc:spigot-api:1.18.2-R0.1-SNAPSHOT")
            library("nbt-api", "de.tr7zw:item-nbt-api-plugin:2.11.2")
            library("kraken-core", "com.github.sarhatabaot:krakencore:1.6.2")
            library("vault-api", "com.github.MilkBowl:VaultAPI:1.7.1")
            library("treasury-api", "me.lokka30:treasury-api:1.2.1")
            version("configurate", "4.1.2")
            library("configurate-yaml", "org.spongepowered", "configurate-yaml").versionRef("configurate")
        }
    }
}