group "com.stockary"
version "1.0-SNAPSHOT"

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("io.realm.kotlin:gradle-plugin:1.6.0")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

plugins {
    kotlin("multiplatform") apply false
    kotlin("android") apply false
    kotlin("plugin.serialization") apply false
    id("com.android.application") apply false
    id("com.android.library") apply false
    id("org.jetbrains.compose") apply false
}