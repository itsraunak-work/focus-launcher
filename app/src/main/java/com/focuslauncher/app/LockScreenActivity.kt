package com.focuslauncher.app

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class LockScreenActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDate: TextView
    private lateinit var tvDaysRemaining: TextView
    private lateinit var gestureDetector: GestureDetector

    private val handler = Handler(Looper.getMainLooper())

    private val clockRunnable = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock_screen)

        tvTime = findViewById(R.id.tvLockTime)
        tvDate = findViewById(R.id.tvLockDate)
        tvDaysRemaining = findViewById(R.id.tvDaysRemaining)

        updateClock()
        updateDaysRemaining()
        handler.post(clockRunnable)

        // Detect swipe up to dismiss lock screen
        gestureDetector = GestureDetector(
            this,
            object : GestureDetector.SimpleOnGestureListener() {
                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float
                ): Boolean {
                    // Negative velocityY = upward swipe
                    if (velocityY < -600) {
                        finish()
                        return true
                    }
                    return false
                }
            }
        )
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private fun updateClock() {
        val now = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        tvTime.text = timeFormat.format(now.time)
        tvDate.text = dateFormat.format(now.time).lowercase()
    }

    private fun updateDaysRemaining() {
        val cal = Calendar.getInstance()
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_YEAR)
        val daysLeft = totalDays - dayOfYear
        val year = cal.get(Calendar.YEAR)
        tvDaysRemaining.text = "$daysLeft days left in $year"
    }

    override fun onResume() {
        super.onResume()
        handler.post(clockRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(clockRunnable)
    }

    // Prevent back button — user must swipe up
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        // intentionally blocked
    }
}