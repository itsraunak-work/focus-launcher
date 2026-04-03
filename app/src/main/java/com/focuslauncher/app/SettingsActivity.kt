package com.focuslauncher.app

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val tvBack = findViewById<TextView>(R.id.tvBack)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBlock)

        tvBack.setOnClickListener {
            finish()
        }

        // Load all installed apps
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps = packageManager
            .queryIntentActivities(intent, 0)
            .map {
                AppInfo(
                    appName = it.loadLabel(packageManager).toString(),
                    packageName = it.activityInfo.packageName
                )
            }
            .sortedBy { it.appName.lowercase() }

        val prefs = getSharedPreferences("focus_prefs", MODE_PRIVATE)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = BlockAppAdapter(apps, prefs)
        recyclerView.itemAnimator = null
    }
}