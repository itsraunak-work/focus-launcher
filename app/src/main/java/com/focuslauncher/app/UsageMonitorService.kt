package com.focuslauncher.app

import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper

class UsageMonitorService : Service() {

    private val autoBlockPackages = setOf(
        "com.instagram.android", "com.facebook.katana",
        "com.facebook.orca", "com.google.android.youtube",
        "com.twitter.android", "com.snapchat.android",
        "com.tiktok.android", "com.zhiliaoapp.musically",
        "com.reddit.frontpage", "com.pinterest",
        "com.linkedin.android", "com.android.chrome"
    )

    private val handler = Handler(Looper.getMainLooper())
    private val recentlyBlocked = mutableSetOf<String>()
    private var isRunning = false

    private val checker = object : Runnable {
        override fun run() {
            checkForeground()
            handler.postDelayed(this, 15_000)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) { isRunning = true; handler.post(checker) }
        return START_STICKY
    }

    private fun checkForeground() {
        val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val now = System.currentTimeMillis()
        val stats = usm.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, now - 3_600_000L, now
        ) ?: return

        val recent = stats.filter { it.totalTimeInForeground > 0 }
            .maxByOrNull { it.lastTimeUsed } ?: return

        val pkg = recent.packageName
        if (pkg == packageName || pkg in recentlyBlocked) return

        val prefs = getSharedPreferences("focus_prefs", Context.MODE_PRIVATE)
        val isAuto = pkg in autoBlockPackages
        val isBlocked = prefs.getBoolean("block_$pkg", isAuto)
        if (!isBlocked) return

        val defaultMin = if (isAuto) 10 else 5
        val limitMin = prefs.getInt("time_$pkg", defaultMin)
        val limitMs = limitMin * 60_000L

        if (recent.totalTimeInForeground >= limitMs) {
            showBlockOverlay(pkg)
            recentlyBlocked.add(pkg)
            handler.postDelayed({ recentlyBlocked.remove(pkg) }, 5 * 60_000L)
        }
    }

    private fun showBlockOverlay(pkg: String) {
        val appName = try {
            val info = packageManager.getApplicationInfo(pkg, 0)
            packageManager.getApplicationLabel(info).toString()
        } catch (e: Exception) { pkg }

        // Use BlockOverlayActivity (the new timer-based one)
        val intent = Intent(this, BlockOverlayActivity::class.java).apply {
            putExtra("package_name", pkg)
            putExtra("blocked_app_name", appName)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(checker)
        isRunning = false
    }
}