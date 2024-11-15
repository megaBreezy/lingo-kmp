package com.github.megabreezy.lingo.translate

import kotlinx.serialization.json.Json
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.collections.component1
import kotlin.collections.component2

abstract class Generate_Translations_Task : DefaultTask() {

    // Directory containing JSON translation files
    @get:InputFiles
    abstract var translation_file_dir: FileCollection

    // Output file for generated Translations.kt
    @OutputDirectory
    val output_dir: DirectoryProperty = project.objects.directoryProperty().apply {
        set(project.layout.buildDirectory.dir("generated/source/lingo"))
    }

    @get:OutputFile
    val output_file: File = project.layout.buildDirectory.file("generated/source/lingo/Translations.kt").get().asFile

    init {
        group = "translation"
        description = "Generates a Translations.kt file from JSON translation files."
    }

    @TaskAction
    fun generate() {
        logger.lifecycle("Running generate_translations task...")

        println("Running generateTranslations...")

        val output_dir_file = output_dir.get().asFile
        if (!output_dir_file.exists()) output_dir_file.mkdirs()

        val translationKeysContent = StringBuilder(
            """ 
            |class Translations {
            |
            """.trimMargin()
        )

        // Use the configured translationsDirs instead of accessing project properties
        println("translation_file_dir: $translation_file_dir")
        translation_file_dir.files.forEach { module_dir ->
            println("Checking ${module_dir.name}")
            // Navigate up to the module root (four levels up from `translations`)
            val translations_dir = File(module_dir, "translations")

            if (!translations_dir.exists() || !translations_dir.isDirectory) {
                println("No translations found for ${module_dir.name}")
                return@forEach
            }

            val moduleTranslations = mutableMapOf<String, MutableMap<String, String>>()

            translations_dir.listFiles { _, name -> name.endsWith(".json") }?.forEach { file ->
                val languageCode = file.nameWithoutExtension
                val translations = parse_json_file(file)

                translations.forEach { (key, value) ->
                    moduleTranslations.computeIfAbsent(key) { mutableMapOf() }[languageCode] = value
                }
            }

            val moduleRoot = module_dir.parentFile.parentFile.parentFile.parentFile
            val moduleName = moduleRoot.name // This should give you "dependentTestModule" or other module names
            val enumClassName = moduleName.replaceFirstChar { it.uppercase() }

            val moduleEnumContent = StringBuilder(
                """
                    |    enum class $enumClassName : Localizable
                    |    {
                    |
                    """.trimMargin()
            )

            moduleTranslations.entries.forEachIndexed { index, (key, translationsByLanguage) ->
                val translationsString = translationsByLanguage.entries.joinToString(
                    prefix = "mapOf(",
                    postfix = ")"
                ) { (lang, value) -> "\"$lang\" to \"$value\"" }

                moduleEnumContent.append(
                    """
                        |        ${key.uppercase()} {
                        |            override val value = $translationsString
                        |            override fun get_localized_string(language_code: String) = value[language_code]
                        |        }
                        """.trimMargin()
                )
                if (index < moduleTranslations.size - 1) moduleEnumContent.append(",\n")
            }

            moduleEnumContent.append(
                """
                    |
                    |        ;
                    |
                    |        protected abstract val value: Map<String, String>
                    |        abstract override fun get_localized_string(language_code: String): String?
                    |    }
                    |
                    """.trimMargin()
            )

            translationKeysContent.append(moduleEnumContent.toString())
        }

        translationKeysContent.append(
            """
            |    
            |    companion object
            |    {
            |        val shared = Translations()
            |    }
            |}
            |
            |interface Localizable
            |{
            |    fun get_localized_string(language_code: String): String?
            |}
            |
        """.trimMargin()
        )

        output_file.writeText(translationKeysContent.toString())
        println("Generated Translations file at: ${output_file.path}")
    }

    private fun parse_json_file(file: File): Map<String, String>
    {
        val jsonContent = file.readText()
        return Json.decodeFromString(jsonContent)
    }
}
