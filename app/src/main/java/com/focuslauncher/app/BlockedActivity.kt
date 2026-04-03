package com.focuslauncher.app

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BlockedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked)

        // Get which app was blocked — passed from UsageMonitorService
        val blockedAppName = intent.getStringExtra("blocked_app_name") ?: "This app"

        val tvMessage = findViewById<TextView>(R.id.tvBlockedMessage)
        val tvSubMessage = findViewById<TextView>(R.id.tvBlockedSubMessage)
        val btnGoBack = findViewById<Button>(R.id.btnGoBack)

        tvMessage.text = "$blockedAppName blocked"
        tvSubMessage.text = "You've used $blockedAppName for more than 5 minutes. Take a break!"

        // Go back to launcher when button is pressed
        btnGoBack.setOnClickListener {
            finish() // closes this screen, goes back to launcher
        }
    }

    // Prevent going back to the blocked app with back button
    override fun onBackPressed() {
        finish()
    }
}