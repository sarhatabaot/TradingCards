plugins {
    id("net.tinetwork.tradingcards.java-conventions")
}

version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.jooq)
    compileOnly(libs.jooq.codegen)
    
    implementation(libs.annotations)
}


