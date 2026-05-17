package com.education.creditscore.calculator.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.education.creditscore.calculator.R
import com.education.creditscore.calculator.activities.OnboardingActivity
import com.education.creditscore.calculator.adapters.*
import com.education.creditscore.calculator.databinding.*
import com.education.creditscore.calculator.models.*
import com.education.creditscore.calculator.services.DataService
import com.education.creditscore.calculator.services.TranslationManager
import com.education.creditscore.calculator.viewmodels.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

// ── Home Fragment ─────────────────────────────────────────────────────────────
@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeData()
        setupClickListeners()
        setupTipsRecycler()
    }

    private fun observeData() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            binding.tvGreeting.text = getString(R.string.greeting_format, getString(viewModel.getGreeting()))
            binding.tvUserName.text = if (user?.name.isNullOrEmpty()) getString(R.string.guest_user) else user?.name
            val initials = user?.name?.split(" ")?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                ?.take(2)?.joinToString("") ?: "GU"
            binding.tvAvatar.text = initials
        }

        viewModel.creditScore.observe(viewLifecycleOwner) { score ->
            val s = score ?: CreditScore()
            binding.tvScoreHero.text = s.score.toString()
            binding.tvScoreLabel.text = s.label
            binding.progressScoreBar.progress = (s.percentile * 100).toInt()
            binding.progressScoreCircle.progress = (s.percentile * 100).toInt()

            // Payment History factor row
            binding.factorPayment.pbFactor.progress = s.paymentHistory.toInt()
            binding.factorPayment.tvFactorLabel.text = getString(R.string.payment_history)
            binding.factorPayment.tvFactorTag.text = when {
                s.paymentHistory >= 95f -> getString(R.string.excellent)
                s.paymentHistory >= 80f -> getString(R.string.good)
                s.paymentHistory >= 70f -> getString(R.string.fair)
                else -> getString(R.string.poor)
            }

            // Credit Utilization factor row
            binding.factorUtilization.pbFactor.progress = s.creditUtilization.toInt()
            binding.factorUtilization.tvFactorLabel.text = getString(R.string.credit_utilization)
            binding.factorUtilization.tvFactorTag.text = when {
                s.creditUtilization < 10f -> getString(R.string.excellent)
                s.creditUtilization < 30f -> getString(R.string.good)
                s.creditUtilization < 50f -> getString(R.string.fair)
                else -> getString(R.string.high)
            }

            // Account Age factor row
            binding.factorAge.pbFactor.progress = ((s.accountAgeYears / 15f) * 100).toInt().coerceAtMost(100)
            binding.factorAge.tvFactorLabel.text = getString(R.string.account_age)
            binding.factorAge.tvFactorTag.text = when {
                s.accountAgeYears >= 10 -> getString(R.string.excellent)
                s.accountAgeYears >= 5  -> getString(R.string.good)
                s.accountAgeYears >= 3  -> getString(R.string.fair)
                else -> getString(R.string.new_label)
            }

            // Hard Inquiries factor row
            binding.factorInquiries.pbFactor.progress = ((1f - s.hardInquiries / 10f) * 100).toInt().coerceIn(0, 100)
            binding.factorInquiries.tvFactorLabel.text = getString(R.string.hard_inquiries)
            binding.factorInquiries.tvFactorTag.text = when {
                s.hardInquiries == 0 -> getString(R.string.excellent)
                s.hardInquiries <= 2 -> getString(R.string.good)
                s.hardInquiries <= 5 -> getString(R.string.fair)
                else -> getString(R.string.high)
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnCheckScore.setOnClickListener { findNavController().navigate(R.id.action_home_to_creditScore) }
        binding.btnSimulate.setOnClickListener { findNavController().navigate(R.id.action_home_to_simulator) }
        binding.cardCalculators.setOnClickListener { findNavController().navigate(R.id.action_home_to_calculators_tab) }
        binding.cardBanks.setOnClickListener { findNavController().navigate(R.id.action_home_to_banks_tab) }
        binding.cardReport.setOnClickListener { findNavController().navigate(R.id.action_home_to_fullReport) }
        binding.cardMap.setOnClickListener { findNavController().navigate(R.id.action_home_to_map) }
        binding.tvSeeAllTips.setOnClickListener { findNavController().navigate(R.id.action_home_to_tips) }
        binding.tvAvatar.setOnClickListener { findNavController().navigate(R.id.action_home_to_profile_tab) }
    }

    private fun setupTipsRecycler() {
        binding.rvTips.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTips.adapter = TipAdapter(DataService.tips.take(3)) {
            findNavController().navigate(R.id.action_home_to_tips)
        }
        binding.rvTips.isNestedScrollingEnabled = false
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Calculators Fragment ──────────────────────────────────────────────────────
@AndroidEntryPoint
class CalculatorsFragment : Fragment() {

    private var _binding: FragmentCalculatorsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalculatorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val categories = listOf(
            getString(R.string.cat_loan_emi),
            getString(R.string.cat_invest_savings),
            getString(R.string.cat_tax_salary),
            getString(R.string.cat_credit_tools)
        )
        val adapter = CalculatorSectionAdapter(categories, DataService.calculators) { calc ->
            val action = CalculatorsFragmentDirections.actionCalculatorsToCalcDetail(calc.id)
            findNavController().navigate(action)
        }
        binding.rvCalculators.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCalculators.adapter = adapter
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Calculator Detail Fragment ────────────────────────────────────────────────
@AndroidEntryPoint
class CalcDetailFragment : Fragment() {

    private var _binding: FragmentCalcDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CalculatorViewModel by viewModels()
    private val args: CalcDetailFragmentArgs by navArgs()
    private val inputViews = mutableMapOf<String, EditText>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCalcDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val calcId = args.calcId
        viewModel.setCalculator(calcId)

        val calc = DataService.calculators.first { it.id == calcId }
        binding.tvCalcEmoji.text = calc.emoji

        binding.tvCalcDesc.text = calc.description

        buildInputFields(calcId)

        binding.btnCalculate.setOnClickListener { compute() }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            if (result != null) {
                binding.cardResult.visibility = View.VISIBLE
                binding.tvResultValue.text = result.primaryValue
                binding.tvResultLabel.text = result.primaryLabel
                binding.rvResultDetails.layoutManager = LinearLayoutManager(requireContext())
                binding.rvResultDetails.adapter = ResultDetailAdapter(result.details)
            } else {
                binding.cardResult.visibility = View.GONE
            }
        }

    }

    private fun buildInputFields(id: String) {
        val fields = getFields(id)
        binding.llInputs.removeAllViews()
        inputViews.clear()
        fields.forEach { (key, label, hint, prefix, suffix) ->
            val inflated = layoutInflater.inflate(R.layout.item_input_field, binding.llInputs, false)
            inflated.findViewById<TextView>(R.id.tvInputLabel).text = label
            val et = inflated.findViewById<EditText>(R.id.etInput).apply {
                var currentHint = hint
                if (!prefix.isNullOrEmpty()) currentHint = "$prefix $currentHint"
                if (!suffix.isNullOrEmpty()) currentHint = "$currentHint $suffix"
                this.hint = currentHint
                addTextChangedListener { viewModel.clearResult() }
            }
            inputViews[key] = et
            binding.llInputs.addView(inflated)
        }
    }

    private fun compute() {
        val inputs = inputViews.mapValues { it.value.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0 }
        if (inputs.values.all { it == 0.0 }) {
            Snackbar.make(binding.root, getString(R.string.invalid_inputs), Snackbar.LENGTH_SHORT).show()
            return
        }
        viewModel.compute(inputs)
    }

    private data class FieldDef(val key: String, val label: String, val hint: String, val prefix: String? = null, val suffix: String? = null)

    private fun getFields(id: String): List<FieldDef> = when (id) {
        "emi" -> listOf(
            FieldDef("principal", TranslationManager.getLabel("principal", "Loan Amount"), "200000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "10.5", "%"),
            FieldDef("months", TranslationManager.getLabel("months", "Tenure (months)"), "120", "")
        )
        "loan" -> listOf(
            FieldDef("income", TranslationManager.getLabel("income", "Monthly Income"), "50000", "$"),
            FieldDef("expenses", TranslationManager.getLabel("expenses", "Monthly Expenses"), "20000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "9.5", "%")
        )
        "vehicle" -> listOf(
            FieldDef("price", TranslationManager.getLabel("price", "Vehicle Price"), "15000", "$"),
            FieldDef("down", TranslationManager.getLabel("down", "Down Payment"), "3000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "8.5", "%"),
            FieldDef("months", TranslationManager.getLabel("months", "Tenure (months)"), "60", "")
        )
        "prepay" -> listOf(
            FieldDef("balance", TranslationManager.getLabel("balance", "Outstanding Balance"), "100000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "10.0", "%"),
            FieldDef("extra", TranslationManager.getLabel("extra", "Extra Payment"), "5000", "$")
        )
        "sip" -> listOf(
            FieldDef("monthly", TranslationManager.getLabel("monthly", "Monthly Investment"), "500", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "12.0", "%"),
            FieldDef("years", TranslationManager.getLabel("years", "Years"), "10", "")
        )
        "mutual" -> listOf(
            FieldDef("investment", TranslationManager.getLabel("investment", "Investment Amount"), "5000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "15.0", "%"),
            FieldDef("years", TranslationManager.getLabel("years", "Years"), "5", "")
        )
        "fd" -> listOf(
            FieldDef("principal", TranslationManager.getLabel("principal", "Principal"), "10000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "7.0", "%"),
            FieldDef("years", TranslationManager.getLabel("years", "Years"), "5", "")
        )
        "rd" -> listOf(
            FieldDef("monthly", TranslationManager.getLabel("monthly", "Monthly Investment"), "2000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "6.5", "%"),
            FieldDef("months", TranslationManager.getLabel("months", "Tenure (months)"), "24", "")
        )
        "roi" -> listOf(
            FieldDef("invested", TranslationManager.getLabel("invested", "Amount Invested"), "10000", "$"),
            FieldDef("finalVal", TranslationManager.getLabel("finalVal", "Final Value"), "15000", "$"),
            FieldDef("years", TranslationManager.getLabel("years", "Years"), "3", "")
        )
        "epf" -> listOf(
            FieldDef("salary", TranslationManager.getLabel("salary", "Monthly Basic Salary"), "30000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Annual Rate (%)"), "8.15", "%"),
            FieldDef("years", TranslationManager.getLabel("years", "Years"), "20", "")
        )
        "gst" -> listOf(
            FieldDef("amount", TranslationManager.getLabel("amount", "Original Amount"), "1000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Tax Rate (%)"), "18", "%")
        )
        "sales_tax" -> listOf(
            FieldDef("amount", TranslationManager.getLabel("amount", "Original Price"), "1000", "$"),
            FieldDef("rate", TranslationManager.getLabel("rate", "Tax Rate (%)"), "7", "%")
        )
        "salary" -> listOf(
            FieldDef("gross", TranslationManager.getLabel("gross", "Gross Annual Salary"), "100000", "$"),
            FieldDef("tax", TranslationManager.getLabel("tax", "Tax Rate (%)"), "15", "%"),
            FieldDef("deductions", TranslationManager.getLabel("deductions", "Deductions"), "5000", "$")
        )
        "boost" -> listOf(
            FieldDef("current", TranslationManager.getLabel("current", "Current Score"), "650", ""),
            FieldDef("targetUtil", TranslationManager.getLabel("targetUtil", "Target Utilization (%)"), "15", "%"),
            FieldDef("newAccounts", TranslationManager.getLabel("newAccounts", "New Accounts"), "0", "")
        )
        else -> emptyList()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Credit Score Fragment ─────────────────────────────────────────────────────
@AndroidEntryPoint
class CreditScoreFragment : Fragment() {

    private var _binding: FragmentCreditScoreBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreditScoreViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreditScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.creditScore.observe(viewLifecycleOwner) { score ->
            val s = score ?: CreditScore()
            binding.tvBigScore.text = s.score.toString()
            binding.tvScoreLabel.text = s.label
            binding.scoreProgress.progress = (s.percentile * 100).toInt()
        }


        binding.cardOfflineSimulator.setOnClickListener {
            findNavController().navigate(R.id.action_creditScore_to_simulator)
        }
        binding.cardFullReport.setOnClickListener {
            findNavController().navigate(R.id.action_creditScore_to_fullReport)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Simulator Fragment ────────────────────────────────────────────────────────
@AndroidEntryPoint
class SimulatorFragment : Fragment() {

    private var _binding: FragmentSimulatorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreditScoreViewModel by viewModels()
    private var slidersSynced = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSimulatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSliders()
        observeScore()

        viewModel.creditScore.observe(viewLifecycleOwner) { score ->
            if (!slidersSynced && score != null) {
                slidersSynced = true
                viewModel.initFromScore(score)
                binding.sliderPayment.value = score.paymentHistory.coerceIn(0f, 100f)
                binding.sliderUtilization.value = score.creditUtilization.coerceIn(0f, 100f)
                binding.sliderAge.value = score.accountAgeYears.toFloat().coerceIn(0f, 20f)
                binding.sliderInquiries.value = score.hardInquiries.toFloat().coerceIn(0f, 10f)
                binding.sliderMix.value = score.creditMixCount.toFloat().coerceIn(0f, 6f)
            }
        }

        binding.btnSaveScore.setOnClickListener {
            viewModel.saveSimulatedScore()
            Snackbar.make(binding.root, getString(R.string.score_saved), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setupSliders() {
        binding.sliderPayment.addOnChangeListener { _, value, _ ->
            viewModel.setPayment(value)
            binding.tvPaymentVal.text = getString(R.string.percent_format, value.toInt())
        }
        binding.sliderUtilization.addOnChangeListener { _, value, _ ->
            viewModel.setUtilization(value)
            binding.tvUtilVal.text = getString(R.string.percent_format, value.toInt())
        }
        binding.sliderAge.addOnChangeListener { _, value, _ ->
            viewModel.setAge(value.toInt())
            binding.tvAgeVal.text = getString(R.string.years_format, value.toInt())
        }
        binding.sliderInquiries.addOnChangeListener { _, value, _ ->
            viewModel.setInquiries(value.toInt())
            binding.tvInqVal.text = "${value.toInt()}"
        }
        binding.sliderMix.addOnChangeListener { _, value, _ ->
            viewModel.setMix(value.toInt())
            binding.tvMixVal.text = "${value.toInt()}"
        }
    }

    private fun observeScore() {
        viewModel.simulatedScore.observe(viewLifecycleOwner) { score ->
            binding.tvSimScore.text = score.toString()
            binding.tvSimLabel.text = CreditScore(score = score).label
            binding.scoreGradientBar.progress = ((score - 300f) / 550f * 100).toInt()

            val tips = viewModel.getImprovementTips()
            binding.llTips.removeAllViews()
            if (tips.isEmpty()) {
                val tv = TextView(requireContext()).apply {
                    text = getString(R.string.excellent_profile_msg)
                    setTextColor(resources.getColor(R.color.green, null))
                    textSize = 14f
                    setPadding(0, 8, 0, 8)
                }
                binding.llTips.addView(tv)
            } else {
                tips.forEach { tip ->
                    val tv = TextView(requireContext()).apply {
                        text = getString(R.string.tip_format, tip)
                        textSize = 13f
                        setPadding(0, 6, 0, 6)
                    }
                    binding.llTips.addView(tv)
                }
            }
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Full Report Fragment ──────────────────────────────────────────────────────
@AndroidEntryPoint
class FullReportFragment : Fragment() {

    private var _binding: FragmentFullReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreditScoreViewModel by viewModels()
    private val questions = DataService.reportQuestions
    private var currentStep = 0
    private var finalScore = 0
    private var pendingOptionValue: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentFullReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showQuestion(0)
    }

    private fun showQuestion(index: Int) {
        if (index >= questions.size) { showResult(); return }
        currentStep = index
        pendingOptionValue = null
        val q = questions[index]

        binding.tvStepLabel.text = getString(R.string.step_count_format, index + 1, questions.size)
        binding.progressReport.progress = ((index + 1) * 100 / questions.size)
        binding.tvQuestionTitle.text = q.title
        binding.tvQuestionText.text = q.question

        binding.llOptions.removeAllViews()
        q.options.forEach { option ->
            val itemView = layoutInflater.inflate(R.layout.item_report_option, binding.llOptions, false)
            itemView.findViewById<TextView>(R.id.tvOptionEmoji).text = option.emoji
            itemView.findViewById<TextView>(R.id.tvOptionLabel).text = option.label
            itemView.setOnClickListener {
                // Reset all option cards to unselected state
                for (i in 0 until binding.llOptions.childCount) {
                    val child = binding.llOptions.getChildAt(i)
                    (child as? androidx.cardview.widget.CardView)?.apply {
                        setCardBackgroundColor(resources.getColor(R.color.card, null))
                        alpha = 0.65f
                    }
                }
                // Highlight the selected card
                (itemView as? androidx.cardview.widget.CardView)?.apply {
                    setCardBackgroundColor(resources.getColor(R.color.primary_light, null))
                    alpha = 1f
                }
                pendingOptionValue = option.value
                binding.tvSelectHint.visibility = View.GONE
                binding.btnNextReport.visibility = View.VISIBLE
            }
            binding.llOptions.addView(itemView)
        }

        // Reset state for new question
        binding.btnNextReport.visibility = View.GONE
        binding.tvSelectHint.visibility = View.VISIBLE

        binding.btnNextReport.setOnClickListener {
            pendingOptionValue?.let { value ->
                viewModel.setReportAnswer(q.key, value)
                showQuestion(index + 1)
            }
        }

        binding.btnBackReport.visibility = if (index > 0) View.VISIBLE else View.GONE
        binding.btnBackReport.setOnClickListener { showQuestion(index - 1) }

        binding.layoutReport.visibility = View.VISIBLE
        binding.layoutResult.visibility = View.GONE
    }

    private fun showResult() {
        finalScore = viewModel.computeReportScore()
        viewModel.saveReportScore(finalScore)
        binding.layoutReport.visibility = View.GONE
        binding.layoutResult.visibility = View.VISIBLE

        binding.tvFinalScore.text = finalScore.toString()
        binding.tvFinalLabel.text = CreditScore(score = finalScore).label
        binding.progressFinalScore.progress = ((finalScore - 300f) / 550f * 100).toInt()

        binding.btnDoneReport.setOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Bank Directory Fragment ───────────────────────────────────────────────────
@AndroidEntryPoint
class BankDirectoryFragment : Fragment() {

    private var _binding: FragmentBankDirectoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: BankViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBankDirectoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        setupSearch()
        setupCountryFilter()

        binding.btnFindAtm.setOnClickListener {
            findNavController().navigate(R.id.action_banks_to_map)
        }
    }

    private fun setupRecycler() {
        val adapter = BankAdapter(
            onBankClick = { bank ->
                val action = BankDirectoryFragmentDirections.actionBanksToBankDetail(bank.name)
                findNavController().navigate(action)
            },
            onCall = { bank ->
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${bank.customerCare.replace(Regex("[^0-9+]"), "")}"))
                try { startActivity(intent) } catch (e: Exception) {
                    Snackbar.make(binding.root, getString(R.string.cannot_call), Snackbar.LENGTH_SHORT).show()
                }
            }
        )
        binding.rvBanks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBanks.adapter = adapter

        viewModel.filteredBanks.observe(viewLifecycleOwner) { banks ->
            adapter.submitList(banks)
            binding.tvBankCount.text = getString(R.string.banks_found_format, banks.size)
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { viewModel.setQuery(it.toString()) }
    }

    private fun setupCountryFilter() {
        val countries = viewModel.countries
        val chipGroup = binding.chipGroupCountry
        chipGroup.removeAllViews()
        countries.forEach { country ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = if (country == "All") getString(R.string.cat_all) else country
                isCheckable = true
                isChecked = country == "All"
                setOnCheckedChangeListener { _, checked -> if (checked) viewModel.setCountry(country) }
            }
            chipGroup.addView(chip)
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Map View Fragment ─────────────────────────────────────────────────────────
@AndroidEntryPoint
class MapViewFragment : Fragment() {

    private data class NearbyLocation(
        val name: String, val lat: Double, val lng: Double,
        val type: String, val emoji: String
    )

    private val allLocations by lazy {
        listOf(
            NearbyLocation(TranslationManager.getLabel("chase_atm", "Chase ATM"), 40.7128, -74.0060, getString(R.string.atm), "🏧"),
            NearbyLocation(TranslationManager.getLabel("bofa", "Bank of America"), 40.7148, -74.0040, getString(R.string.bank), "🏦"),
            NearbyLocation(TranslationManager.getLabel("wells_fargo_atm", "Wells Fargo ATM"), 40.7108, -74.0080, getString(R.string.atm), "🏧"),
            NearbyLocation(TranslationManager.getLabel("citibank_branch", "Citibank Branch"), 40.7168, -74.0020, getString(R.string.bank), "🏦")
        )
    }
    private val markerMap = mutableMapOf<String, com.google.android.gms.maps.model.Marker>()

    private var _binding: FragmentMapViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMapViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupNearbyRecycler()
        setupChipFilters()

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapFragment) as? com.google.android.gms.maps.SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            val nyc = com.google.android.gms.maps.model.LatLng(40.7128, -74.0060)
            googleMap.moveCamera(com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(nyc, 13f))
            allLocations.forEach { loc ->
                val marker = googleMap.addMarker(
                    com.google.android.gms.maps.model.MarkerOptions()
                        .position(com.google.android.gms.maps.model.LatLng(loc.lat, loc.lng))
                        .title(loc.name)
                )
                if (marker != null) markerMap[loc.name] = marker
            }
        }
    }

    private fun setupNearbyRecycler() {
        binding.rvNearby.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvNearby.adapter = NearbyLocationAdapter(allLocations)
    }

    private fun setupChipFilters() {
        fun applyFilter(typeFilter: String?) {
            allLocations.forEach { loc ->
                markerMap[loc.name]?.isVisible = typeFilter == null || loc.type == typeFilter
            }
            val filtered = if (typeFilter == null) allLocations else allLocations.filter { it.type == typeFilter }
            (binding.rvNearby.adapter as? NearbyLocationAdapter)?.updateList(filtered)
        }
        binding.chipAll.setOnCheckedChangeListener  { _, checked -> if (checked) applyFilter(null) }
        binding.chipAtm.setOnCheckedChangeListener  { _, checked -> if (checked) applyFilter(getString(R.string.atm)) }
        binding.chipBank.setOnCheckedChangeListener { _, checked -> if (checked) applyFilter(getString(R.string.bank)) }
    }

    private inner class NearbyLocationAdapter(
        private var items: List<NearbyLocation>
    ) : RecyclerView.Adapter<NearbyLocationAdapter.VH>() {

        fun updateList(newItems: List<NearbyLocation>) { items = newItems; notifyDataSetChanged() }

        inner class VH(v: View) : RecyclerView.ViewHolder(v) {
            val tvEmoji: TextView = v.findViewById(R.id.tvNearbyEmoji)
            val tvName:  TextView = v.findViewById(R.id.tvNearbyName)
            val tvType:  TextView = v.findViewById(R.id.tvNearbyType)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(LayoutInflater.from(parent.context).inflate(R.layout.item_nearby_location, parent, false))

        override fun onBindViewHolder(holder: VH, position: Int) {
            val loc = items[position]
            holder.tvEmoji.text = loc.emoji
            holder.tvName.text  = loc.name
            holder.tvType.text  = loc.type
        }

        override fun getItemCount() = items.size
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Tips Fragment ─────────────────────────────────────────────────────────────
@AndroidEntryPoint
class TipsFragment : Fragment() {

    private var _binding: FragmentTipsBinding? = null
    private val binding get() = _binding!!
    private var selectedCategory = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTipsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        selectedCategory = getString(R.string.cat_all)
        binding.rvTips.layoutManager = LinearLayoutManager(requireContext())
        setupCategoryFilter()
        loadTips()
        setupFaqs()
    }

    private fun setupCategoryFilter() {
        val categories = listOf(
            getString(R.string.cat_all),
            getString(R.string.cat_credit_score),
            getString(R.string.cat_savings),
            getString(R.string.cat_investment)
        )
        val chipGroup = binding.chipGroupCategory
        chipGroup.removeAllViews()
        categories.forEach { cat ->
            val chip = com.google.android.material.chip.Chip(requireContext()).apply {
                text = cat
                isCheckable = true
                isChecked = cat == getString(R.string.cat_all)
                setOnCheckedChangeListener { _, checked ->
                    if (checked) { selectedCategory = cat; loadTips() }
                }
            }
            chipGroup.addView(chip)
        }
    }

    private fun loadTips() {
        val tips = if (selectedCategory == getString(R.string.cat_all)) DataService.tips
                   else DataService.tips.filter { it.category == selectedCategory }
        
        binding.rvTips.adapter = TipAdapter(tips) { tip ->
            val action = TipsFragmentDirections.actionTipsToTipDetail(tip.id)
            findNavController().navigate(action)
        }
    }

    private fun setupFaqs() {
        binding.rvFaqs.layoutManager = LinearLayoutManager(requireContext())
        binding.rvFaqs.adapter = FaqAdapter(DataService.faqs)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Tip Detail Fragment ───────────────────────────────────────────────────────
@AndroidEntryPoint
class TipDetailFragment : Fragment() {

    private var _binding: FragmentTipDetailBinding? = null
    private val binding get() = _binding!!
    private val args: TipDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTipDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tip = DataService.tips.find { it.id == args.tipId } ?: return
        

        binding.tvDetailFullTitle.text = tip.title
        binding.tvDetailEmoji.text = tip.emoji
        binding.tvDetailCategory.text = tip.category
        binding.tvDetailBody.text = tip.body


    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Profile Fragment ──────────────────────────────────────────────────────────
@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
        setupClickListeners()
        setupMenuLabels()
    }

    private fun setupMenuLabels() {


        binding.menuSettings.tvMenuEmoji.text = "🌍"
        binding.menuSettings.tvMenuLabel.text = getString(R.string.lang_settings)

        binding.menuNotifications.tvMenuEmoji.text = "🔔"
        binding.menuNotifications.tvMenuLabel.text = getString(R.string.notifications)

        binding.menuTips.tvMenuEmoji.text = "💡"
        binding.menuTips.tvMenuLabel.text = getString(R.string.tips_faqs)

        binding.menuSignOut.tvMenuEmoji.text = "🚪"
        binding.menuSignOut.tvMenuLabel.text = getString(R.string.sign_out)
    }

    private fun observeData() {
        viewModel.user.observe(viewLifecycleOwner) { user ->
            val name = if (user?.name.isNullOrEmpty()) getString(R.string.guest_user) else user?.name ?: ""
            val initials = name.split(" ").mapNotNull { it.firstOrNull()?.uppercaseChar() }.take(2).joinToString("")
            binding.tvProfileAvatar.text = initials
            binding.tvProfileName.text = name
            binding.tvProfileEmail.text = user?.email ?: ""
        }

        viewModel.creditScore.observe(viewLifecycleOwner) { score ->
            val s = score ?: CreditScore()
            binding.tvStatScore.text = s.score.toString()
            binding.tvStatLabel.text = s.label
            binding.tvStatAge.text = getString(R.string.years_format, s.accountAgeYears)
            binding.tvStatAccounts.text = s.creditMixCount.toString()
        }
    }

    private fun setupClickListeners() {

        binding.menuSettings.root.setOnClickListener { findNavController().navigate(R.id.action_profile_to_settings) }
        binding.menuNotifications.root.setOnClickListener { findNavController().navigate(R.id.action_profile_to_notifications) }
        binding.menuTips.root.setOnClickListener { findNavController().navigate(R.id.action_profile_to_tips) }
        binding.menuSignOut.root.setOnClickListener { confirmSignOut() }
    }

    private fun confirmSignOut() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.sign_out))
            .setMessage(getString(R.string.sign_out_message))
            .setPositiveButton(getString(R.string.sign_out)) { _, _ ->
                viewModel.signOut()
                val intent = Intent(requireContext(), OnboardingActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}



// ── Settings Fragment ─────────────────────────────────────────────────────────
@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        observeSettings()
    }

    private fun setupMenu() {
        // Main sections
        binding.menuNotifications.setOnClickListener { findNavController().navigate(R.id.action_settings_to_notifications) }
        
        // General section (Language & Currency)
        binding.menuLanguage.apply {
            tvMenuEmoji.text = "🌐"
            tvMenuLabel.text = getString(R.string.language_label)
            tvMenuSubtitle.visibility = View.VISIBLE
            root.setOnClickListener { findNavController().navigate(R.id.action_settings_to_language) }
        }
        
        binding.menuCurrency.apply {
            tvMenuEmoji.text = "💰"
            tvMenuLabel.text = getString(R.string.currency_label)
            tvMenuSubtitle.visibility = View.VISIBLE
            divider.visibility = View.GONE
            root.setOnClickListener { findNavController().navigate(R.id.action_settings_to_currency) }
        }

        binding.menuLegal.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/ashu-t-99"))
            startActivity(intent)
        }
        binding.menuRate.setOnClickListener {
            Toast.makeText(requireContext(), getString(R.string.rating_coming_soon), Toast.LENGTH_SHORT).show()
        }
        binding.menuShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text))
            }
            startActivity(Intent.createChooser(intent, getString(R.string.share_via)))
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setNotifications(isChecked)
        }
    }

    private fun observeSettings() {
        viewModel.language.observe(viewLifecycleOwner) { lang ->
            binding.menuLanguage.tvMenuSubtitle.text = lang
        }

        viewModel.currency.observe(viewLifecycleOwner) { cur ->
            binding.menuCurrency.tvMenuSubtitle.text = cur
        }

        viewModel.notificationsEnabled.observe(viewLifecycleOwner) { enabled ->
            if (binding.switchNotifications.isChecked != enabled) binding.switchNotifications.isChecked = enabled
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}


// ── Notifications Fragment ────────────────────────────────────────────────────
@AndroidEntryPoint
class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    private val notifications by lazy {
        listOf(
            AppNotification("📊", getString(R.string.notif_score_updated_title), getString(R.string.notif_score_updated_body), getString(R.string.time_2h_ago), false),
            AppNotification("💡", getString(R.string.notif_money_tip_title), getString(R.string.notif_money_tip_body), getString(R.string.time_1d_ago), false),
            AppNotification("🎉", getString(R.string.notif_welcome_title), getString(R.string.notif_welcome_body), getString(R.string.time_3d_ago), true),
            AppNotification("⚠️", getString(R.string.notif_utilization_alert_title), getString(R.string.notif_utilization_alert_body), getString(R.string.time_5d_ago), true)
        )
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvNotifications.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNotifications.adapter = NotificationAdapter(notifications)
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Language Selection Fragment ───────────────────────────────────────────────
@AndroidEntryPoint
class LanguageSelectionFragment : Fragment() {

    private var _binding: FragmentLanguageSelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLanguageSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val languages = listOf("English", "Español", "Français", "हिन्दी", "العربية", "Português", "Deutsch", "中文", "ગુજરાતી")
        val currentLang = viewModel.language.value ?: "English"
        
        binding.rvLanguage.layoutManager = LinearLayoutManager(requireContext())
        binding.rvLanguage.adapter = com.education.creditscore.calculator.adapters.SelectionAdapter(languages, currentLang) { lang ->
            viewModel.setLanguage(lang)
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Currency Selection Fragment ───────────────────────────────────────────────
@AndroidEntryPoint
class CurrencySelectionFragment : Fragment() {

    private var _binding: FragmentCurrencySelectionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCurrencySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currencies = listOf("USD", "EUR", "GBP", "INR", "JPY", "CAD", "AUD")
        val currentCur = viewModel.currency.value ?: "USD"

        binding.rvCurrency.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCurrency.adapter = com.education.creditscore.calculator.adapters.SelectionAdapter(currencies, currentCur) { cur ->
            viewModel.setCurrency(cur)
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}

// ── Bank Detail Fragment ──────────────────────────────────────────────────────
@AndroidEntryPoint
class BankDetailFragment : Fragment() {

    private var _binding: FragmentBankDetailBinding? = null
    private val binding get() = _binding!!
    private val args: BankDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentBankDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bank = com.education.creditscore.calculator.services.DataService.banks
            .find { it.name == args.bankName } ?: return

        binding.tvBankDetailEmoji.text = bank.logoEmoji
        binding.tvBankDetailName.text = bank.name
        binding.tvBankDetailCountryBadge.text = bank.country
        binding.tvBankDetailCountry.text = bank.country
        binding.tvBankDetailPhone.text = bank.customerCare

        if (bank.website.isNotBlank()) {
            binding.tvBankDetailWebsite.text = bank.website
        } else {
            binding.tvBankDetailWebsite.text = getString(R.string.no_website)
            binding.tvVisitWebsite.visibility = View.GONE
            binding.cardWebsite.isClickable = false
        }

        binding.cardCall.setOnClickListener { dialBank(bank.customerCare) }
        binding.btnCallBank.setOnClickListener { dialBank(bank.customerCare) }

        binding.cardWebsite.setOnClickListener {
            if (bank.website.isNotBlank()) openWebsite(bank.website)
        }
    }

    private fun dialBank(number: String) {
        val cleaned = number.replace(Regex("[^0-9+]"), "")
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleaned"))
        try { startActivity(intent) } catch (e: Exception) {
            Snackbar.make(binding.root, getString(R.string.cannot_call), Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun openWebsite(website: String) {
        val url = if (website.startsWith("http")) website else "https://$website"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try { startActivity(intent) } catch (e: Exception) {
            Snackbar.make(binding.root, "Cannot open website", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() { super.onDestroyView(); _binding = null }
}
