plugins {
    id("net.tinetwork.tradingcards.java-conventions")
}

version = "5.7.12"

repositories {
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

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.nbt.api)
    compileOnly(libs.vault.api)
    compileOnly(libs.annotations)
    compileOnly(libs.treasury.api)
    implementation(libs.kraken.core)
}