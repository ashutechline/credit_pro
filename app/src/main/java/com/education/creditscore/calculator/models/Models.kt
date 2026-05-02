package com.education.creditscore.calculator.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

// ── User ──────────────────────────────────────────────────────────────────────
@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val currency: String = "USD",
    val language: String = "English",
    val isPremium: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

// ── Credit Score ──────────────────────────────────────────────────────────────
@Parcelize
@Entity(tableName = "credit_scores")
data class CreditScore(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val score: Int = 712,
    val paymentHistory: Float = 92f,
    val creditUtilization: Float = 34f,
    val accountAgeYears: Int = 6,
    val hardInquiries: Int = 2,
    val creditMixCount: Int = 3,
    val updatedAt: Long = System.currentTimeMillis()
) : Parcelable {
    val label: String get() = when {
        score >= 800 -> "Exceptional"
        score >= 740 -> "Very Good"
        score >= 670 -> "Good"
        score >= 580 -> "Fair"
        else -> "Poor"
    }
    val percentile: Float get() = (score - 300f) / (850f - 300f)
}

// ── Bank ──────────────────────────────────────────────────────────────────────
@Parcelize
data class Bank(
    val name: String,
    val country: String,
    val customerCare: String,
    val logoEmoji: String = "🏦",
    val website: String = ""
) : Parcelable

// ── Calculator ────────────────────────────────────────────────────────────────
@Parcelize
data class Calculator(
    val id: String,
    val name: String,
    val description: String,
    val emoji: String,
    val category: String
) : Parcelable

// ── Tip ───────────────────────────────────────────────────────────────────────
@Parcelize
data class Tip(
    val id: Int,
    val title: String,
    val body: String,
    val category: String,
    val emoji: String
) : Parcelable

// ── Calculator Result ─────────────────────────────────────────────────────────
data class CalcResult(
    val primaryLabel: String,
    val primaryValue: String,
    val details: List<Pair<String, String>> = emptyList()
)

// ── Report Answer ─────────────────────────────────────────────────────────────
data class ReportQuestion(
    val title: String,
    val question: String,
    val key: String,
    val options: List<ReportOption>
)

data class ReportOption(
    val emoji: String,
    val label: String,
    val value: String
)

// ── Notification ──────────────────────────────────────────────────────────────
data class AppNotification(
    val emoji: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isRead: Boolean
)
