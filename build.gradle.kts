plugins {
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}