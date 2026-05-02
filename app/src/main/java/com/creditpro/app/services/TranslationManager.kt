package com.creditpro.app.services

import android.content.Context
import com.creditpro.app.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

object TranslationManager {

    private val gson = Gson()
    private var currentData: Map<String, Any> = emptyMap()

    fun getAppLocale(): String {
        val appLocales = androidx.appcompat.app.AppCompatDelegate.getApplicationLocales()
        return if (!appLocales.isEmpty) {
            appLocales[0]?.language ?: "en"
        } else {
            java.util.Locale.getDefault().language
        }
    }

    fun loadTranslations(context: Context, langCode: String) {
        try {
            val fileName = "lang/$langCode.json"
            val inputStream = context.assets.open(fileName)
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<Map<String, Any>>() {}.type
            currentData = gson.fromJson(reader, type)
            reader.close()
        } catch (e: Exception) {
            e.printStackTrace()
            if (langCode != "en") loadTranslations(context, "en")
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getTips(): List<Tip> {
        val list = currentData["tips"] as? List<Map<String, Any>> ?: return emptyList()
        return list.map {
            Tip(
                id = (it["id"] as Double).toInt(),
                title = it["title"] as String,
                body = it["content"] as String,
                category = it["category"] as String,
                emoji = it["emoji"] as String
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getCalculators(): List<Calculator> {
        val list = currentData["calculators"] as? List<Map<String, Any>> ?: return emptyList()
        return list.map {
            Calculator(
                id = it["id"] as String,
                name = it["name"] as String,
                description = it["description"] as String,
                emoji = it["emoji"] as String,
                category = it["category"] as String
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getBanks(): List<Bank> {
        val list = currentData["banks"] as? List<Map<String, Any>> ?: return emptyList()
        return list.map {
            Bank(
                name = it["name"] as String,
                country = it["country"] as String,
                customerCare = it["customerCare"] as String,
                logoEmoji = it["logo"] as String,
                website = it["website"] as String
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getFaqs(): List<Pair<String, String>> {
        val list = currentData["faqs"] as? List<Map<String, String>> ?: return emptyList()
        return list.map { (it["q"] ?: "") to (it["a"] ?: "") }
    }

    @Suppress("UNCHECKED_CAST")
    fun getReportQuestions(): List<ReportQuestion> {
        val list = currentData["report_questions"] as? List<Map<String, Any>> ?: return emptyList()
        return list.map {
            val optionsList = it["options"] as List<Map<String, String>>
            ReportQuestion(
                title = it["title"] as String,
                question = it["question"] as String,
                key = it["key"] as String,
                options = optionsList.map { opt ->
                    ReportOption(
                        emoji = opt["emoji"] ?: "",
                        label = opt["label"] ?: "",
                        value = opt["value"] ?: ""
                    )
                }
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun getLabel(key: String, fallback: String): String {
        val labels = currentData["labels"] as? Map<String, String> ?: return fallback
        return labels[key] ?: fallback
    }

    @Suppress("UNCHECKED_CAST")
    fun getImprovementTips(): List<String> {
        val tips = currentData["improvement_tips"] as? List<String> ?: return emptyList()
        return tips
    }
}
