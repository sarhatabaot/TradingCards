plugins {
    id("net.tinetwork.tradingcards.java-conventions")
}

version = "5.7.20"

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.nbt.api)
    compileOnly(libs.vault.api)
    compileOnly(libs.annotations)
    compileOnly(libs.treasury.api)
    compileOnly(libs.configurate.core)
    compileOnly(libs.configurate.yaml)
    compileOnly(libs.caffeine)
    implementation(libs.kraken.core)
}
