package com.github.megabreezy.lingo.translate

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class Generate_Translations_Task : DefaultTask() {
    @InputDirectory
    val input_dir = project.file("src/commonMain/resources/translations")

    @OutputFile
    val output_file = project.buildDir.resolve("generated/source/lingo/Translations.kt")

    @TaskAction
    fun generate_translations() {
        logger.lifecycle("Generating Translations.kt and Translations_Initializer.kt...")

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

        val translations_content = """
            |package com.github.megabreezy.lingo.generated
            |
            |object Translations {
            |    fun get_all_translations() = mapOf(
            |${translations.entries.joinToString(",\n") { (key, values) ->
            """
                |        "$key" to mapOf(
                |${values.entries.joinToString(",\n") { (lang, value) ->
                """            "$lang" to "$value" """
            }}
                |        )
                """.trimMargin()
        }}
            |    )
            |}
        """.trimMargin()

        val initializer_content = """
            |package com.github.megabreezy.lingo.generated
            |
            |import com.github.megabreezy.lingo_kmp.Lingo
            |
            |object Translations_Initializer 
            |{
            |    init
            |    {
            |        Lingo.register_translations { Translations.get_all_translations() }
            |    }
            |
            |    fun initialize()
            |    {
            |        Lingo.register_translations { Translations.get_all_translations() }
            |    }
            |}
        """.trimMargin()

        val output_dir = output_file.parentFile
        output_dir.mkdirs()
        output_file.writeText(translations_content)

        val initializer_file = File(output_dir, "Translations_Initializer.kt")
        initializer_file.writeText(initializer_content)

        logger.lifecycle("Generated Translations.kt and Translations_Initializer.kt at ${output_dir.absolutePath}")
    }

    private fun create_placeholder_files() {
        val output_dir = output_file.parentFile
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

        output_file.writeText(placeholder_translations)
        val initializer_file = File(output_file.parentFile, "Translations_Initializer.kt")
        initializer_file.writeText(placeholder_initializer)
    }
}
