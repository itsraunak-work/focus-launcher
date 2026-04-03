package com.focuslauncher.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LauncherSettingsActivity : AppCompatActivity() {

    // Cycle through options
    private val fonts = listOf("sans forgetica", "light", "thin")
    private val textSizes = listOf("small", "medium", "large")
    private val clockFormats = listOf("24h", "12h")
    private val sortOrders = listOf("A → Z", "Z → A")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher_settings)

        val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)

        val tvBack = findViewById<TextView>(R.id.tvSettingsBack)
        val tvFont = findViewById<TextView>(R.id.tvFontValue)
        val tvSize = findViewById<TextView>(R.id.tvTextSizeValue)
        val tvClock = findViewById<TextView>(R.id.tvClockFormat)
        val tvSort = findViewById<TextView>(R.id.tvSortOrder)
        val tvGrayscale = findViewById<TextView>(R.id.tvGrayscaleToggle)
        val tvAccessibility = findViewById<TextView>(R.id.tvAccessibilityBtn)

        // Load saved values
        tvFont.text = prefs.getString("pref_font", fonts[0])
        tvSize.text = prefs.getString("pref_text_size", textSizes[1])
        tvClock.text = prefs.getString("pref_clock_format", clockFormats[0])
        tvSort.text = prefs.getString("pref_sort_order", sortOrders[0])
        val grayscaleOn = prefs.getBoolean("pref_grayscale", false)
        tvGrayscale.text = if (grayscaleOn) "●" else "○"

        tvBack.setOnClickListener { finish() }

        // Cycle font
        tvFont.setOnClickListener {
            val next = cycleNext(fonts, tvFont.text.toString())
            tvFont.text = next
            prefs.edit().putString("pref_font", next).apply()
        }

        // Cycle text size
        tvSize.setOnClickListener {
            val next = cycleNext(textSizes, tvSize.text.toString())
            tvSize.text = next
            prefs.edit().putString("pref_text_size", next).apply()
        }

        // Cycle clock format
        tvClock.setOnClickListener {
            val next = cycleNext(clockFormats, tvClock.text.toString())
            tvClock.text = next
            prefs.edit().putString("pref_clock_format", next).apply()
        }

        // Cycle sort order
        tvSort.setOnClickListener {
            val next = cycleNext(sortOrders, tvSort.text.toString())
            tvSort.text = next
            prefs.edit().putString("pref_sort_order", next).apply()
        }

        // Toggle grayscale
        tvGrayscale.setOnClickListener {
            val current = prefs.getBoolean("pref_grayscale", false)
            val newVal = !current
            prefs.edit().putBoolean("pref_grayscale", newVal).apply()
            tvGrayscale.text = if (newVal) "●" else "○"
        }

        // Open accessibility settings
        tvAccessibility.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    private fun cycleNext(options: List<String>, current: String): String {
        val idx = options.indexOf(current)
        return options[(idx + 1) % options.size]
    }
}