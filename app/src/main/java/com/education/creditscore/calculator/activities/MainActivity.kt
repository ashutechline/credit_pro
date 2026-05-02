package com.education.creditscore.calculator.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.navigation.ui.navigateUp
import com.education.creditscore.calculator.R
import com.education.creditscore.calculator.databinding.ActivityMainBinding
import com.education.creditscore.calculator.services.TranslationManager

import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Load translations based on effective locale
        TranslationManager.loadTranslations(this, TranslationManager.getAppLocale())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Setup Toolbar
        setSupportActionBar(binding.toolbar)
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.calculatorsFragment,
            R.id.bankDirectoryFragment,
            R.id.profileFragment
        ))
        setupActionBarWithNavController(navController, appBarConfiguration)
        
        binding.bottomNav.setupWithNavController(navController)

        // Hide/Show Toolbar and Bottom Nav
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val isTopLevel = destination.id in listOf(
                R.id.homeFragment,
                R.id.calculatorsFragment,
                R.id.bankDirectoryFragment,
                R.id.profileFragment
            )
            
            // Show toolbar only on non-top-level screens (except maybe splash/onboarding if they were here)
            // But onboarding is a separate activity.
            binding.toolbar.visibility = if (isTopLevel) android.view.View.GONE else android.view.View.VISIBLE
            
            binding.bottomNav.visibility = if (isTopLevel)
                android.view.View.VISIBLE else android.view.View.GONE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}
