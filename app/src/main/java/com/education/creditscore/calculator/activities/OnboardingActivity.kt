package com.education.creditscore.calculator.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.education.creditscore.calculator.R
import com.education.creditscore.calculator.databinding.ActivityOnboardingBinding
import com.education.creditscore.calculator.viewmodels.MainViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: MainViewModel by viewModels()
    private var selectedLanguage = "English"
    private var selectedCurrency = "USD"

    private val languages = listOf("English", "Español", "Français", "हिन्दी", "العربية", "Português", "Deutsch", "中文", "ગુજરાતી")
    private val currencies = listOf("USD ($)", "EUR (€)", "GBP (£)", "INR (₹)", "JPY (¥)", "CAD (CA$)", "AUD (A$)")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        binding.viewPager.adapter = OnboardingAdapter(this)
        binding.viewPager.isUserInputEnabled = true // Enable swiping for better UX

        setupIndicators()
        setCurrentIndicator(0)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateButtons(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<android.widget.ImageView>(4)
        val layoutParams: android.widget.LinearLayout.LayoutParams =
            android.widget.LinearLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        binding.layoutIndicators.removeAllViews()
        for (i in indicators.indices) {
            indicators[i] = android.widget.ImageView(applicationContext)
            indicators[i]?.apply {
                setImageDrawable(androidx.core.content.ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_dot_inactive
                ))
                this.layoutParams = layoutParams
            }
            binding.layoutIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = binding.layoutIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = binding.layoutIndicators.getChildAt(i) as android.widget.ImageView
            if (i == index) {
                imageView.setImageDrawable(androidx.core.content.ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_dot_active
                ))
                // Simple scale animation for a modern feel
                imageView.animate().scaleX(1.1f).scaleY(1.1f).setDuration(200).start()
            } else {
                imageView.setImageDrawable(androidx.core.content.ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_dot_inactive
                ))
                imageView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
        }
    }

    private fun setupButtons() {
        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < 3) {
                binding.viewPager.currentItem = current + 1
            } else {
                completeOnboarding()
            }
        }

        binding.btnBack.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) binding.viewPager.currentItem = current - 1
        }
    }

    private fun updateButtons(position: Int) {
        binding.btnBack.visibility = if (position > 0) View.VISIBLE else View.GONE
        binding.btnNext.text = if (position == 3) getString(R.string.get_started) else getString(R.string.next)
        binding.btnBack.text = getString(R.string.back)
        updateProgress(position)
    }

    private fun updateProgress(step: Int) {
        val progress = (step + 1) * 25
        binding.progressBar.progress = progress
    }

    fun onLanguageSelected(language: String) { selectedLanguage = language }
    fun onCurrencySelected(currency: String) { selectedCurrency = currency.split(" ").first() }

    private fun completeOnboarding() {
        viewModel.setLanguage(selectedLanguage)
        viewModel.setCurrency(selectedCurrency)
        viewModel.createGuestUser()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    // ── Adapter ───────────────────────────────────────────────────────────────
    inner class OnboardingAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        override fun getItemCount() = 4
        override fun createFragment(position: Int): Fragment = when (position) {
            0 -> FeatureFragment.newInstance("📊", getString(R.string.onboarding_title_1), getString(R.string.onboarding_body_1))
            1 -> FeatureFragment.newInstance("🧮", getString(R.string.onboarding_title_2), getString(R.string.onboarding_body_2))
            2 -> FeatureFragment.newInstance("🏦", getString(R.string.onboarding_title_3), getString(R.string.onboarding_body_3))
            3 -> SelectionFragment()
            else -> FeatureFragment.newInstance("📊", "", "")
        }
    }
}

// ── Feature Fragment ──────────────────────────────────────────────────────────
class FeatureFragment : Fragment() {
    companion object {
        fun newInstance(emoji: String, title: String, body: String) = FeatureFragment().apply {
            arguments = Bundle().apply {
                putString("emoji", emoji)
                putString("title", title)
                putString("body", body)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_onboarding_feature, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<TextView>(R.id.tvEmoji).text = arguments?.getString("emoji")
        view.findViewById<TextView>(R.id.tvTitle).text = arguments?.getString("title")
        view.findViewById<TextView>(R.id.tvBody).text = arguments?.getString("body")
    }
}

// ── Selection Fragment ────────────────────────────────────────────────────────
class SelectionFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_onboarding_selection, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val activity = requireActivity() as OnboardingActivity

        val languages = listOf("English", "Español", "Français", "हिन्दी", "العربية", "Português", "Deutsch", "中文", "ગુજરાતી")
        val currencies = listOf("USD ($)", "EUR (€)", "GBP (£)", "INR (₹)", "JPY (¥)", "CAD (CA$)", "AUD (A$)")
        val langGroup = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupLanguage)
        val currGroup = view.findViewById<com.google.android.material.chip.ChipGroup>(R.id.chipGroupCurrency)

        languages.forEach { lang ->
            val chip = Chip(requireContext()).apply {
                text = lang
                isCheckable = true
                isChecked = lang == "English"
                setOnCheckedChangeListener { _, checked -> if (checked) activity.onLanguageSelected(lang) }
            }
            langGroup.addView(chip)
        }
        currencies.forEach { cur ->
            val chip = Chip(requireContext()).apply {
                text = cur
                isCheckable = true
                isChecked = cur.startsWith("USD")
                setOnCheckedChangeListener { _, checked -> if (checked) activity.onCurrencySelected(cur) }
            }
            currGroup.addView(chip)
        }
    }
}


