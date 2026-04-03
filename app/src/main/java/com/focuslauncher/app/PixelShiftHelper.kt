package com.focuslauncher.app

import android.view.View
import kotlin.random.Random

// Shifts the entire UI by 1-2px randomly every time the screen turns on
// This prevents AMOLED burn-in from static elements like the clock
object PixelShiftHelper {

    private val random = Random.Default
    private const val MAX_SHIFT_DP = 2f

    fun applyShift(rootView: View) {
        val dp = rootView.resources.displayMetrics.density
        val maxPx = MAX_SHIFT_DP * dp

        // Random offset between -maxPx and +maxPx
        val dx = (random.nextFloat() * 2f - 1f) * maxPx
        val dy = (random.nextFloat() * 2f - 1f) * maxPx

        rootView.animate()
            .translationX(dx)
            .translationY(dy)
            .setDuration(0)
            .start()
    }
}