package com.education.creditscore.calculator.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.work.WorkManager
import com.education.creditscore.calculator.services.NotificationReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "creditpro_prefs")

@Singleton
class PreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val REPORT_ANSWERS = stringPreferencesKey("report_answers")
    }

    val isOnboarded: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.ONBOARDED] ?: false }

    val language: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.LANGUAGE] ?: "English" }

    val currency: Flow<String> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.CURRENCY] ?: "USD" }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.NOTIFICATIONS_ENABLED] ?: true }

    suspend fun setOnboarded(value: Boolean) {
        context.dataStore.edit { it[Keys.ONBOARDED] = value }
    }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[Keys.LANGUAGE] = lang }
    }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { it[Keys.CURRENCY] = currency }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}

// ── Main Repository ───────────────────────────────────────────────────────────

@Singleton
class CreditProRepository @Inject constructor(
    private val userDao: UserDao,
    private val creditScoreDao: CreditScoreDao,
    private val prefs: PreferencesRepository,
    @ApplicationContext private val context: Context
) {
    // User
    fun getUser() = userDao.getUser()
    suspend fun getUserOnce() = userDao.getUserOnce()
    suspend fun saveUser(user: com.education.creditscore.calculator.models.User) = userDao.insertUser(user)
    suspend fun updateUser(user: com.education.creditscore.calculator.models.User) = userDao.updateUser(user)
    suspend fun deleteUser() = userDao.deleteAll()

    // Credit Score
    fun getLatestScore() = creditScoreDao.getLatestScore()
    suspend fun saveScore(score: com.education.creditscore.calculator.models.CreditScore) = creditScoreDao.insertScore(score)
    suspend fun getScoreHistory() = creditScoreDao.getAllScores()

    // Preferences
    val isOnboarded = prefs.isOnboarded
    val language = prefs.language
    val currency = prefs.currency
    val notificationsEnabled = prefs.notificationsEnabled

    suspend fun setOnboarded() = prefs.setOnboarded(true)
    suspend fun setLanguage(lang: String) = prefs.setLanguage(lang)
    suspend fun setCurrency(currency: String) = prefs.setCurrency(currency)
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        prefs.setNotificationsEnabled(enabled)
        if (enabled) {
            NotificationReceiver.scheduleNotifications(context)
        } else {
            WorkManager.getInstance(context).cancelUniqueWork("daily_tip")
        }
    }
    suspend fun clearAllData() {
        userDao.deleteAll()
        creditScoreDao.deleteAllScores()
        prefs.clearAll()
    }
}
