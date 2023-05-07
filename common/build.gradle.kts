val koin_version = "3.3.2"
val koin_android_version = "3.3.2"
val koin_android_compose_version = "3.4.1"
val koin_ktor = "3.3.0"

val ktorVersion = "2.3.0"

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("org.jetbrains.compose")
    id("com.android.library")
}

group = "com.stockary"
version = "1.0-SNAPSHOT"

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    js(IR) {
        browser()
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.material3)
                api(compose.materialIconsExtended)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)

                api("io.insert-koin:koin-core:$koin_version")

                implementation("io.ktor:ktor-client-core:$ktorVersion")

                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")

                implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
                api("io.github.qdsfdhvh:image-loader:1.4.0")

                implementation(project.dependencies.platform("io.github.jan-tennert.supabase:bom:0.7.5"))
                implementation("io.github.jan-tennert.supabase:gotrue-kt")
                implementation("io.github.jan-tennert.supabase:functions-kt")
                implementation("io.github.jan-tennert.supabase:storage-kt")
                implementation("io.github.jan-tennert.supabase:postgrest-kt")
                implementation("io.github.jan-tennert.supabase:realtime-kt")

                implementation("io.github.copper-leaf:ballast-core:2.3.0")
                implementation("io.github.copper-leaf:ballast-saved-state:2.3.0")
                implementation("io.github.copper-leaf:ballast-repository:2.3.0")
                implementation("io.github.copper-leaf:ballast-firebase-crashlytics:2.3.0")
                implementation("io.github.copper-leaf:ballast-firebase-analytics:2.3.0")
                implementation("io.github.copper-leaf:ballast-debugger:2.3.0")
                implementation("io.github.copper-leaf:ballast-navigation:2.3.0")

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-properties:1.4.1")

                implementation("com.github.librepdf:openpdf:1.3.30")

            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("io.github.copper-leaf:ballast-test:2.3.0")
            }
        }
        val androidMain by getting {
            dependencies {
                api("androidx.appcompat:appcompat:1.2.0")
                api("androidx.core:core-ktx:1.3.1")
                implementation("io.insert-koin:koin-androidx-compose:$koin_android_compose_version")
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        val desktopMain by getting {
            dependencies {
                api(compose.preview)
                implementation("com.alialbaali.kamel:kamel-image:0.4.1")
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
            }
        }
        val desktopTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(compose.uiTestJUnit4)
            }
        }
    }
}

android {
    compileSdkVersion(33)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(33)
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}