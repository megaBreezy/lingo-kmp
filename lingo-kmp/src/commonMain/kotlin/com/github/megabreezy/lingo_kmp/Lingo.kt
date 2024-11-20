package com.github.megabreezy.lingo_kmp

object Lingo {
    private var translations: Map<String, Map<String, String>> = mapOf()
    private var language_code: String = "en"

    fun register_translations(translations_provider: () -> Map<String, Map<String, String>>) {
        translations = translations_provider()
    }

    fun set_language(code: String) {
        language_code = code
    }

    fun get_string(key: String): String {
        val language_map = translations[key] ?: return "Missing translation for key: $key"
        return language_map[language_code] ?: "Missing translation for key: $key in language: $language_code"
    }
}
