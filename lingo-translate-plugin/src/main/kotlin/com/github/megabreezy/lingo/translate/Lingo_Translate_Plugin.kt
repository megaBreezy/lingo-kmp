package com.github.megabreezy.lingo.translate

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class Lingo_Translate_Plugin : Plugin<Project>
{
    override fun apply(project: Project)
    {
        val generate_translations_task = project.tasks.register(
            "generate_translations",
            Generate_Translations_Task::class.java
        ) {
            group = "lingo"
            description = "Generate a Translations.kt file from provided JSON files."
        }

        project.plugins.withType(org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper::class.java) {
            // Access the Kotlin Multiplatform extension
            val kotlinExtension = project.extensions.findByType(KotlinMultiplatformExtension::class.java)
            kotlinExtension?.sourceSets?.getByName("commonMain")?.kotlin?.srcDir(
                "${project.buildDir}/generated/source/lingo"
            )
        }

        project.afterEvaluate {
            // Ensure the generated directory is included in the source set
            project.extensions.findByType(KotlinMultiplatformExtension::class.java)
                ?.sourceSets
                ?.getByName("commonMain")
                ?.kotlin
                ?.srcDir("${project.buildDir}/generated/source/lingo")
        }

        project.tasks.matching()
        {
            it.name in listOf("compileKotlin", "test", "build", "prepareKotlinIdeaImport")
        }.forEach()
        { task ->
            task.dependsOn(generate_translations_task)
        }
    }
}