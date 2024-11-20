package com.github.megabreezy.lingo_kmp

interface Translatable_Key { val id: String }

object Lingo {
    private var translations: Map<String, Map<String, String>> = mapOf()
    private var language_code: String = "en"
    private var is_initialized = false

    fun set_language(code: String) {
        language_code = code
    }

    fun get_string(key: Translatable_Key): String {
        val language_map = translations[key.id] ?: return "Missing translation for key: ${key.id}"
        return language_map[language_code] ?: "Missing translation for key: ${key.id} in language: $language_code"
    }

    fun register_translations(translations_provider: () -> Map<String, Map<String, String>>) {
        translations = translations_provider()
        is_initialized = true
    }
}
