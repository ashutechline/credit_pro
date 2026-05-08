package com.education.creditscore.calculator.utils

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

/**
 * Centralized AdMob helper.
 *
 * All ad unit IDs below are Google's official test IDs — replace with your real
 * ca-app-pub-XXXXX/.../XXXXX IDs before submitting to the Play Store.
 *
 * Policy rules enforced here:
 * - Interstitials are only shown after a meaningful user action (calculator result).
 * - Interstitials are NOT shown on every action — only every Nth calculation.
 * - Banners are loaded lazily per-fragment; never shown on splash/onboarding.
 * - A pre-loaded interstitial is automatically refreshed after it is dismissed.
 */
object AdManager {

    // ── Replace these with your real unit IDs before release ──────────────────
    const val BANNER_UNIT_ID       = "ca-app-pub-3940256099942544/6300978111"
    const val INTERSTITIAL_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
    // ──────────────────────────────────────────────────────────────────────────

    /** Show interstitial every Nth meaningful interaction (not every click). */
    private const val INTERSTITIAL_FREQUENCY = 3

    private var interstitialAd: InterstitialAd? = null
    private var interstitialLoadedAt: Long = 0L
    private var interstitialActionCount = 0

    /** Four-hour validity window for a loaded interstitial. */
    private const val INTERSTITIAL_EXPIRY_MS = 4 * 60 * 60 * 1000L

    // ── Banner ─────────────────────────────────────────────────────────────────

    /**
     * Loads a banner ad into the given [AdView].
     * Call this in [Fragment.onViewCreated] after the view is ready.
     */
    fun loadBanner(adView: AdView) {
        adView.adListener = object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
                // Hide the container so the empty space doesn't confuse users.
                adView.visibility = android.view.View.GONE
            }
            override fun onAdLoaded() {
                adView.visibility = android.view.View.VISIBLE
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    // ── Interstitial ───────────────────────────────────────────────────────────

    /** Pre-loads an interstitial so it is ready when needed. */
    fun preloadInterstitial(context: Context) {
        if (interstitialAd != null && !isInterstitialExpired()) return
        InterstitialAd.load(
            context,
            INTERSTITIAL_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    interstitialLoadedAt = System.currentTimeMillis()
                }
                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    /**
     * Records a meaningful user action (e.g., tapping Calculate).
     * Shows the interstitial every [INTERSTITIAL_FREQUENCY] calls.
     * Does nothing if no ad is loaded or the user has not yet reached the threshold.
     *
     * @param activity    The foreground Activity required by [InterstitialAd.show].
     * @param onComplete  Runs after the ad closes (or immediately if no ad shown).
     *                    Use this to refresh UI state if needed.
     */
    fun recordInteraction(activity: Activity, onComplete: () -> Unit = {}) {
        interstitialActionCount++
        val ad = interstitialAd
        if (ad != null && !isInterstitialExpired() &&
            interstitialActionCount % INTERSTITIAL_FREQUENCY == 0
        ) {
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null
                    onComplete()
                    preloadInterstitial(activity)
                }
                override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                    interstitialAd = null
                    onComplete()
                    preloadInterstitial(activity)
                }
            }
            ad.show(activity)
        } else {
            onComplete()
            if (interstitialAd == null) preloadInterstitial(activity)
        }
    }

    private fun isInterstitialExpired() =
        System.currentTimeMillis() - interstitialLoadedAt > INTERSTITIAL_EXPIRY_MS
}
