package com.focuslauncher.app

import android.app.AppOpsManager
import android.app.UiModeManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    // Listen for screen turning on → apply pixel shift
    private val screenOnReceiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_SCREEN_ON) {
                viewPager.postDelayed({
                    PixelShiftHelper.applyShift(viewPager)
                }, 300)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        forceDarkMode()
        setContentView(R.layout.activity_main)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = MainPagerAdapter(this)

        // Start on Launcher page (index 2)
        viewPager.setCurrentItem(2, false)
        viewPager.offscreenPageLimit = 4

        // Smooth parallax page transition
        viewPager.setPageTransformer { page, position ->
            page.alpha = 1f - (Math.abs(position) * 0.3f)
            page.translationX = -position * page.width * 0.06f
        }

        // Register screen-on receiver for pixel shift
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        registerReceiver(screenOnReceiver, filter)

        startUsageMonitor()

        if (savedInstanceState == null) {
            startActivity(Intent(this, LockScreenActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun forceDarkMode() {
        try {
            val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
        } catch (e: Exception) { }
    }

    private fun startUsageMonitor() {
        val appOps = getSystemService(APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName
        )
        if (mode == AppOpsManager.MODE_ALLOWED) {
            startService(Intent(this, UsageMonitorService::class.java))
        } else {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(screenOnReceiver) } catch (e: Exception) { }
    }
}