package com.github.megabreezy.lingo_kmp

object Lingo
{
    private var translations: Map<String, Map<String, String>> = mapOf()
    private var language_code: String = get_current_device_language()
    private var is_initialized = false

    fun set_language(code: String) { language_code = code }

    fun get_string(key: Translatable_Key): String
    {
        val language_map = translations[key.id] ?: return "Missing translation for key: ${key.id}"

        val normalized_language_code = normalize_language_code(language_code)

        // Try exact match (e.g., "en-gb")
        language_map[normalized_language_code]?.let { return it }

        // Fallback to base language (e.g., "en")
        val base_language_code = normalized_language_code.split("-").first()
        language_map[base_language_code]?.let { return it }

        // Final fallback if no translation is found
        return "Missing translation for key: ${key.id} in language: $language_code or $base_language_code"
    }

    fun register_translations(translations_provider: () -> Map<String, Map<String, String>>)
    {
        translations = translations_provider()
        is_initialized = true
    }

    private fun normalize_language_code(code: String): String
    {
        return code.replace("_", "-").lowercase()
    }
}

interface Translatable_Key { val id: String }

