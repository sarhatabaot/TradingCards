rootProject.name = "TradingCards"

include(":tradingcards-extras")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("jooq", "3.17.8")
            library("jooq", "org.jooq","jooq").versionRef("jooq")
            library("jooq-codegen", "org.jooq", "jooq-codegen").versionRef("jooq")
            library("annotations", "org.jetbrains:annotations:24.0.1")
        }
    }
}