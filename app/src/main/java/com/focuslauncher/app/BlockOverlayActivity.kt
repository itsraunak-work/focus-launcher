package com.focuslauncher.app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar

class BlockOverlayActivity : AppCompatActivity() {

    private lateinit var tvAppName: TextView
    private lateinit var tvMessage: TextView
    private lateinit var tvBreath: TextView
    private lateinit var clockView: ClockProgressView
    private lateinit var btnClose: Button
    private lateinit var btnContinue: Button
    private var timer: CountDownTimer? = null

    // Only these can show "Continue"
    private val continueAllowed = setOf(
        "com.android.chrome",
        "com.google.android.youtube",
        "org.mozilla.firefox",
        "com.microsoft.edge"
    )

    // These are pure social — close only
    private val socialMedia = setOf(
        "com.instagram.android",
        "com.facebook.katana",
        "com.facebook.orca",
        "com.twitter.android",
        "com.snapchat.android",
        "com.tiktok.android",
        "com.zhiliaoapp.musically",
        "com.reddit.frontpage",
        "com.pinterest",
        "com.linkedin.android"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_block_overlay)

        val pkg = intent.getStringExtra("package_name") ?: ""
        val appName = intent.getStringExtra("blocked_app_name") ?: "App"

        // Count how many times blocked today → determines timer length
        val prefs = getSharedPreferences("focus_prefs", Context.MODE_PRIVATE)
        val countKey = "block_count_${pkg}_${todayKey()}"
        val count = prefs.getInt(countKey, 0) + 1
        prefs.edit().putInt(countKey, count).apply()

        // 1st time = 30s, every time after = 60s
        val timerSec = if (count == 1) 30L else 60L
        val isSocial = pkg in socialMedia
        val canContinue = pkg in continueAllowed

        tvAppName = findViewById(R.id.tvOverlayAppName)
        tvMessage = findViewById(R.id.tvOverlayMessage)
        tvBreath = findViewById(R.id.tvBreath)
        clockView = findViewById(R.id.clockProgressView)
        btnClose = findViewById(R.id.btnCloseApp)
        btnContinue = findViewById(R.id.btnContinueAnyway)

        tvAppName.text = appName
        tvMessage.text = if (isSocial)
            "Social apps are designed to keep you hooked.\n$appName — 10 minutes is enough."
        else
            "You've been using $appName for more than 10 minutes."

        clockView.setProgress(timerSec.toInt(), timerSec.toInt())
        startTimer(timerSec, isSocial, canContinue)
    }

    private fun startTimer(total: Long, isSocial: Boolean, canContinue: Boolean) {
        timer = object : CountDownTimer(total * 1000, 1000) {

            override fun onTick(ms: Long) {
                val sec = (ms / 1000).toInt()
                clockView.setProgress(sec, total.toInt())

                // Pulse the "breathe" text
                tvBreath.animate()
                    .alpha(if (sec % 2 == 0) 0.8f else 0.15f)
                    .setDuration(900)
                    .start()
            }

            override fun onFinish() {
                clockView.setProgress(0, total.toInt())
                tvBreath.visibility = View.GONE
                showButtons(isSocial, canContinue)
            }
        }.start()
    }

    private fun showButtons(isSocial: Boolean, canContinue: Boolean) {
        // Close button always appears
        btnClose.visibility = View.VISIBLE
        btnClose.alpha = 0f
        btnClose.animate().alpha(1f).setDuration(500).start()
        btnClose.setOnClickListener {
            // Go to launcher home
            val home = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            startActivity(home)
            finish()
        }

        // Continue only for Chrome/YouTube (not social media)
        if (canContinue && !isSocial) {
            btnContinue.visibility = View.VISIBLE
            btnContinue.alpha = 0f
            btnContinue.animate().alpha(1f).setDuration(500).setStartDelay(200).start()
            btnContinue.setOnClickListener { finish() }
        }
    }

    private fun todayKey(): String {
        val c = Calendar.getInstance()
        return "${c.get(Calendar.YEAR)}_${c.get(Calendar.DAY_OF_YEAR)}"
    }

    // Block back button during countdown
    @Suppress("DEPRECATION")
    override fun onBackPressed() { }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }
}