plugins {
    id("net.tinetwork.tradingcards.java-conventions")
}

version = "5.7.20"

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.spigot.api)
    compileOnly(libs.nbt.api)
    compileOnly(libs.vault.api)
    compileOnly(libs.annotations)
    compileOnly(libs.treasury.api)
    compileOnly(libs.configurate.core)
    compileOnly(libs.configurate.yaml)
    compileOnly(libs.caffeine)
    implementation(libs.kraken.core)
}