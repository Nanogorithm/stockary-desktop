import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.stockary"
version = "1.0-SNAPSHOT"


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(project(":common"))
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "stockary"
            packageVersion = "1.0.1"
            description = "Stockary"
            copyright = "© 2023 Stockary. All rights reserved."
            vendor = "Stockary"

            macOS {
                iconFile.set(project.file("icon.icns"))
            }

            windows {
                iconFile.set(project.file("icon.ico"))
                menuGroup = "stockary"
                upgradeUuid = "1d5b6cb4-1061-4db2-8f40-53d3c9a68305"
                console = true
            }

            linux {
                iconFile.set(project.file("icon.png"))
            }
        }
    }
}
