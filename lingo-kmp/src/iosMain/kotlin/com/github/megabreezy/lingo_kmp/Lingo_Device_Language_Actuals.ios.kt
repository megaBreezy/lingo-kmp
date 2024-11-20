package com.github.megabreezy.lingo_kmp

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.preferredLanguages

actual fun get_current_device_language(default_language_code: String): String = try
{
    NSLocale.currentLocale.languageCode

    val preferred_language = NSLocale.preferredLanguages.firstOrNull()?.let { "$it" } ?: "en"
    preferred_language.split("-").first()
}
catch (e: Exception)
{
    default_language_code
}