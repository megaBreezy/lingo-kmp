package com.github.megabreezy.lingo_kmp

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun get_current_device_language(): String? = try
{
    NSLocale.currentLocale.languageCode
}
catch (_: Exception)
{
    null
}