plugins {
    id("maven-publish")
    `kotlin-dsl` // Necessary for writing Gradle plugins in Kotlin
    kotlin("plugin.serialization") version "2.0.20"
}

group = "com.github.megabreezy.lingo"
version = "0.0.1"

repositories {
    mavenCentral()
    google() // Optional: if you have Android-specific dependencies
}

dependencies {
    // Add the Kotlin standard library
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

// Configure the Gradle Plugin Development plugin
gradlePlugin {
    plugins {
        create("lingoTranslatePlugin") {
            id = "com.github.megabreezy.lingo.translate" // Unique ID for the plugin
            implementationClass = "com.github.megabreezy.lingo.translate.Lingo_Translate_Plugin"
        }
    }
}

// Configure duplicate handling for resource processing
tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}