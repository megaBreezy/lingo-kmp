package com.github.megabreezy.lingo_kmp

object Lingo
{
    private var translations: Map<String, Map<String, String>> = mapOf()
    private var language_code: String? = null
    private var is_initialized = false

    fun set_language(code: String) { language_code = code }

    fun get_string(key: Translatable_Key): String
    {
        val current_language = language_code ?: get_current_device_language()
        println("current language: $current_language")
        println("results of getting current device language: ${get_current_device_language()}")
        val language_map = translations[key.id] ?: return "Missing translation for key: ${key.id}"
        return language_map[current_language] ?: "Missing translation for key: ${key.id} in language: $current_language"
    }

    fun register_translations(translations_provider: () -> Map<String, Map<String, String>>)
    {
        translations = translations_provider()
        is_initialized = true
    }
}

interface Translatable_Key { val id: String }

