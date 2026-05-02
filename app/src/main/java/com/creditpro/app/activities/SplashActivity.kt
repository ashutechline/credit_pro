package com.creditpro.app.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.creditpro.app.R
import com.creditpro.app.databinding.ActivitySplashBinding
import com.creditpro.app.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load translations based on effective locale
        com.creditpro.app.services.TranslationManager.loadTranslations(this, 
            com.creditpro.app.services.TranslationManager.getAppLocale())

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Animate logo
        val scaleAnim = AnimationUtils.loadAnimation(this, R.anim.scale_in)
        val fadeAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.ivLogo.startAnimation(scaleAnim)
        binding.tvAppName.startAnimation(fadeAnim)
        binding.tvTagline.startAnimation(fadeAnim)

        Handler(Looper.getMainLooper()).postDelayed({
            navigate()
        }, 2200)
    }

    private fun navigate() {
        lifecycleScope.launch {
            val onboarded = viewModel.isOnboardedOnce()
            val intent = if (onboarded) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, OnboardingActivity::class.java)
            }
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}
