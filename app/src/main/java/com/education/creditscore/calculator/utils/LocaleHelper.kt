package com.education.creditscore.calculator.utils

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object LocaleHelper {

    private val languageToTag = mapOf(
        "English"    to "en",
        "Español"    to "es",
        "Français"   to "fr",
        "हिन्दी"      to "hi",
        "العربية"     to "ar",
        "Português"  to "pt",
        "Deutsch"    to "de",
        "中文"        to "zh",
        "ગુજરાતી"     to "gu"
    )

    fun applyLocale(language: String) {
        val tag = languageToTag[language] ?: "en"
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
