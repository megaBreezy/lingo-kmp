package com.github.megabreezy.lingo_kmp

object Lingo
{
    private val translations: Map<String, String> = mapOf()

    fun get_string(key: String): String
    {
        return translations[key] ?: "Missing translation for $key"
    }
}