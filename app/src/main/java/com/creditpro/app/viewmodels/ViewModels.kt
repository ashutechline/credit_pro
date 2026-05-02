package com.creditpro.app.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.creditpro.app.R
import com.creditpro.app.models.*
import com.creditpro.app.repository.CreditProRepository
import com.creditpro.app.services.DataService
import com.creditpro.app.utils.LocaleHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Main ViewModel ────────────────────────────────────────────────────────────
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: CreditProRepository
) : ViewModel() {

    val user: LiveData<User?> = repo.getUser()
    val creditScore: LiveData<CreditScore?> = repo.getLatestScore()
    val isOnboarded = repo.isOnboarded.asLiveData()
    val language = repo.language.asLiveData()
    val currency = repo.currency.asLiveData()
    val notificationsEnabled = repo.notificationsEnabled.asLiveData()

    fun saveUser(user: User) = viewModelScope.launch { repo.saveUser(user) }
    fun updateUser(user: User) = viewModelScope.launch { repo.updateUser(user) }
    fun setOnboarded() = viewModelScope.launch { repo.setOnboarded() }
    fun setLanguage(lang: String) = viewModelScope.launch {
        repo.setLanguage(lang)
        LocaleHelper.applyLocale(lang)
    }
    fun setCurrency(currency: String) = viewModelScope.launch { repo.setCurrency(currency) }
    fun setNotifications(enabled: Boolean) = viewModelScope.launch { repo.setNotificationsEnabled(enabled) }

    fun saveScore(score: CreditScore) = viewModelScope.launch { repo.saveScore(score) }

    fun signOut() = viewModelScope.launch {
        repo.clearAllData()
    }

    fun createGuestUser() = viewModelScope.launch {
        val guest = User(
            id = "guest_${System.currentTimeMillis()}",
            name = "",
            email = "guest@creditpro.app"
        )
        repo.saveUser(guest)
        repo.saveScore(CreditScore())
        repo.setOnboarded()
    }

    suspend fun isOnboardedOnce(): Boolean = repo.isOnboarded.first()

    fun createUserFromOnboarding(name: String, email: String) = viewModelScope.launch {
        val user = User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email
        )
        repo.saveUser(user)
        repo.saveScore(CreditScore())
        repo.setOnboarded()
    }

    @StringRes
    fun getGreeting(): Int {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour < 12 -> R.string.good_morning
            hour < 17 -> R.string.good_afternoon
            else -> R.string.good_evening
        }
    }
}

// ── Credit Score ViewModel ────────────────────────────────────────────────────
@HiltViewModel
class CreditScoreViewModel @Inject constructor(
    private val repo: CreditProRepository
) : ViewModel() {

    val creditScore: LiveData<CreditScore?> = repo.getLatestScore()

    // Simulator state
    private val _simPayment = MutableLiveData(92f)
    val simPayment: LiveData<Float> = _simPayment

    private val _simUtilization = MutableLiveData(34f)
    val simUtilization: LiveData<Float> = _simUtilization

    private val _simAge = MutableLiveData(6)
    val simAge: LiveData<Int> = _simAge

    private val _simInquiries = MutableLiveData(2)
    val simInquiries: LiveData<Int> = _simInquiries

    private val _simMix = MutableLiveData(3)
    val simMix: LiveData<Int> = _simMix

    private val _simulatedScore = MutableLiveData(712)
    val simulatedScore: LiveData<Int> = _simulatedScore

    fun setPayment(v: Float) { _simPayment.value = v; recompute() }
    fun setUtilization(v: Float) { _simUtilization.value = v; recompute() }
    fun setAge(v: Int) { _simAge.value = v; recompute() }
    fun setInquiries(v: Int) { _simInquiries.value = v; recompute() }
    fun setMix(v: Int) { _simMix.value = v; recompute() }

    private fun recompute() {
        _simulatedScore.value = DataService.simulateScore(
            paymentHistory = _simPayment.value ?: 92f,
            utilization = _simUtilization.value ?: 34f,
            accountAge = _simAge.value ?: 6,
            inquiries = _simInquiries.value ?: 2,
            creditMix = _simMix.value ?: 3
        )
    }

    fun getImprovementTips(): List<String> {
        val tips = mutableListOf<String>()
        val localTips = com.creditpro.app.services.TranslationManager.getImprovementTips()
        
        val tip1 = localTips.getOrNull(0) ?: "Improve payment history"
        val tip2 = localTips.getOrNull(1) ?: "Reduce credit utilization"
        val tip3 = localTips.getOrNull(2) ?: "Avoid closing old accounts"
        val tip4 = localTips.getOrNull(3) ?: "Limit new credit applications"
        val tip5 = localTips.getOrNull(4) ?: "Diversify credit"

        if ((_simPayment.value ?: 92f) < 80) tips.add(tip1)
        if ((_simUtilization.value ?: 34f) > 30) tips.add(tip2)
        if ((_simAge.value ?: 6) < 3) tips.add(tip3)
        if ((_simInquiries.value ?: 2) > 3) tips.add(tip4)
        if ((_simMix.value ?: 3) < 2) tips.add(tip5)
        return tips
    }

    fun saveSimulatedScore() = viewModelScope.launch {
        val score = CreditScore(
            score = _simulatedScore.value ?: 712,
            paymentHistory = _simPayment.value ?: 92f,
            creditUtilization = _simUtilization.value ?: 34f,
            accountAgeYears = _simAge.value ?: 6,
            hardInquiries = _simInquiries.value ?: 2,
            creditMixCount = _simMix.value ?: 3
        )
        repo.saveScore(score)
    }

    private var scoreInitialized = false

    fun initFromScore(score: CreditScore) {
        if (scoreInitialized) return
        scoreInitialized = true
        setPayment(score.paymentHistory.coerceIn(0f, 100f))
        setUtilization(score.creditUtilization.coerceIn(0f, 100f))
        setAge(score.accountAgeYears.coerceIn(0, 20))
        setInquiries(score.hardInquiries.coerceIn(0, 10))
        setMix(score.creditMixCount.coerceIn(0, 6))
    }

    // Full report
    private val reportAnswers = mutableMapOf<String, String>()

    fun setReportAnswer(key: String, value: String) { reportAnswers[key] = value }
    fun computeReportScore() = DataService.computeReportScore(reportAnswers)

    fun saveReportScore(score: Int) = viewModelScope.launch {
        repo.saveScore(CreditScore(score = score))
    }
}

// ── Calculator ViewModel ──────────────────────────────────────────────────────
@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private val _result = MutableLiveData<CalcResult?>()
    val result: LiveData<CalcResult?> = _result

    private val _calcId = MutableLiveData<String>()
    val calcId: LiveData<String> = _calcId

    fun setCalculator(id: String) {
        _calcId.value = id
        _result.value = null
    }

    fun compute(inputs: Map<String, Double>) {
        val id = _calcId.value ?: return
        _result.value = DataService.calculate(id, inputs)
    }

    fun clearResult() { _result.value = null }
}

// ── Bank ViewModel ────────────────────────────────────────────────────────────
@HiltViewModel
class BankViewModel @Inject constructor() : ViewModel() {

    private val _query = MutableLiveData("")
    val query: LiveData<String> = _query

    private val _selectedCountry = MutableLiveData("All")
    val selectedCountry: LiveData<String> = _selectedCountry

    val filteredBanks: LiveData<List<Bank>> = MediatorLiveData<List<Bank>>().apply {
        fun filter() {
            val q = _query.value ?: ""
            val country = _selectedCountry.value ?: "All"
            value = DataService.banks.filter { bank ->
                val matchesQuery = bank.name.contains(q, ignoreCase = true) || bank.customerCare.contains(q)
                val matchesCountry = country == "All" || bank.country == country
                matchesQuery && matchesCountry
            }
        }
        addSource(_query) { filter() }
        addSource(_selectedCountry) { filter() }
        filter()
    }

    val countries: List<String> get() =
        listOf("All") + DataService.banks.map { it.country }.distinct().sorted()

    fun setQuery(q: String) { _query.value = q }
    fun setCountry(c: String) { _selectedCountry.value = c }
}
