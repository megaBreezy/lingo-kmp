package com.github.megabreezy.lingo_kmp

import java.util.Locale

actual fun get_current_device_language(default_language_code: String): String = try
{
    Locale.getDefault().language
}
catch (_: Exception)
{
    default_language_code
}