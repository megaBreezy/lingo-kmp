package com.github.megabreezy.lingo.translate

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class Generate_Translations_Task : DefaultTask() {
    @InputDirectory
    val input_dir = project.file("src/commonMain/resources/translations")

    @OutputDirectory
    val output_dir = project.buildDir.resolve("generated/source/lingo")

    @TaskAction
    fun generate_translations() {
        logger.lifecycle("Generating translations in: ${output_dir.absolutePath}")

        if (!input_dir.exists() || !input_dir.isDirectory) {
            logger.lifecycle("No translations directory found. Skipping translations generation.")
            create_placeholder_files()
            return
        }

        val translations = mutableMapOf<String, MutableMap<String, String>>()
        val json_parser = Json { ignoreUnknownKeys = true }

        input_dir.listFiles()?.forEach { file ->
            val language_code = file.nameWithoutExtension
            val json_content = file.readText().trim()
            val parsed_json = json_parser.parseToJsonElement(json_content).jsonObject

            parsed_json.forEach { key, value ->
                val key_translations = translations.getOrPut(key) { mutableMapOf() }
                key_translations[language_code] = value.jsonPrimitive.content
            }
        }

        generate_translation_key_file(translations.keys)
        generate_translations_file(translations)
        generate_initializer_file()

        logger.lifecycle("Generated translations successfully.")
    }

    private fun generate_translation_key_file(keys: Set<String>) {
        val keys_content = """
            |package com.github.megabreezy.lingo.generated
            |
            |import com.github.megabreezy.lingo_kmp.Translatable_Key
            |
            |sealed class Translation_Key(override val id: String) : Translatable_Key {
            |${keys.joinToString("\n") { key ->
            """    object ${key.replaceFirstChar { it.uppercase() }} : Translation_Key("$key")"""
        }}
            |}
        """.trimMargin()

        File(output_dir, "Translation_Key.kt").writeText(keys_content)
    }

    private fun generate_translations_file(translations: Map<String, Map<String, String>>) {
        val translations_content = """
            |package com.github.megabreezy.lingo.generated
            |
            |object Translations {
            |    fun get_all_translations() = mapOf(
            |${translations.entries.joinToString(",\n") { (key, values) ->
            """
                |        "$key" to mapOf(
                |${values.entries.joinToString(",\n") { (lang, value) ->
                """            "$lang" to "$value""""
            }}
                |        )
                """.trimMargin()
        }}
            |    )
            |}
        """.trimMargin()

        File(output_dir, "Translations.kt").writeText(translations_content)
    }

    private fun generate_initializer_file() {
        val initializer_content = """
            |package com.github.megabreezy.lingo.generated
            |
            |import com.github.megabreezy.lingo_kmp.Lingo
            |
            |object Translations_Initializer {
            |    fun initialize() {
            |        Lingo.register_translations { Translations.get_all_translations() }
            |    }
            |}
        """.trimMargin()

        File(output_dir, "Translations_Initializer.kt").writeText(initializer_content)
    }

    private fun create_placeholder_files() {
        output_dir.mkdirs()

        val placeholder_translations = """
            |package com.github.megabreezy.lingo.generated
            |
            |object Translations {
            |    fun get_all_translations() = emptyMap<String, Map<String, String>>()
            |}
        """.trimMargin()

        val placeholder_initializer = """
            |package com.github.megabreezy.lingo.generated
            |
            |import com.github.megabreezy.lingo_kmp.Lingo
            |
            |object Translations_Initializer {
            |    fun initialize() {
            |        Lingo.register_translations { Translations.get_all_translations() }
            |    }
            |}
        """.trimMargin()

        File(output_dir, "Translations.kt").writeText(placeholder_translations)
        File(output_dir, "Translations_Initializer.kt").writeText(placeholder_initializer)
    }
}
