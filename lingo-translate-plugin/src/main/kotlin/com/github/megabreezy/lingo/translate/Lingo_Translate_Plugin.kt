package com.github.megabreezy.lingo.translate

import org.gradle.api.Plugin
import org.gradle.api.Project

class Lingo_Translate_Plugin : Plugin<Project>
{
    override fun apply(target: Project)
    {
        // For now, just log a message when the plugin is applied
        target.logger.lifecycle("Lingo_Translate_Plugin applied!")

        // Register the generate_translations task
        val generate_translations_task = target.tasks.register("generate_translations", Generate_Translations_Task::class.java)
        {
            group = "translation"
            description = "Generates a Translations.kt file from JSON translation files."
            translation_file_dir = project.files(
                project.rootProject.subprojects
                    .mapNotNull { subproject ->
                        val translations_path = subproject.file("src/commonMain/resources")
                        if (translations_path.exists() && translations_path.isDirectory) {
                            println("Found translations directory in ${subproject.name}: $translations_path")
                            translations_path
                        } else {
                            println("No translations directory found in ${subproject.name}")
                            null
                        }
                    }
            )
        }

        target.tasks.matching()
        {
            it.name in listOf("compileKotlin", "test", "build", "prepareKotlinIdeaImport")
        }.forEach()
        { task ->
            task.dependsOn(generate_translations_task)
        }
    }
}