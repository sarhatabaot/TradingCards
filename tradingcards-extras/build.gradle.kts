plugins {
    id("net.tinetwork.tradingcards.java-conventions")
}

version = "0.0.1"


dependencies {
    compileOnly(libs.jooq)
    compileOnly(libs.jooq.codegen)
    
    implementation(libs.annotations)
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()

            dependencies {
                compileOnly(libs.jooq)
                compileOnly(libs.jooq.codegen)
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


