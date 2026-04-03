package com.focuslauncher.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ContentResolver
import android.provider.Settings
import android.view.accessibility.AccessibilityEvent

class GrayscaleAccessibilityService : AccessibilityService() {

    private val socialPackages = setOf(
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

    private var lastPackage = ""

    override fun onServiceConnected() {
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return

        // Only act if app actually changed
        if (pkg == lastPackage) return
        lastPackage = pkg

        // Skip our own launcher
        if (pkg == packageName) {
            setGrayscale(false)
            return
        }

        setGrayscale(pkg in socialPackages)
    }

    private fun setGrayscale(enable: Boolean) {
        try {
            val cr: ContentResolver = contentResolver
            if (enable) {
                // 0 = monochromacy (full grayscale)
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer", 0)
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", 1)
            } else {
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", 0)
            }
        } catch (e: SecurityException) {
            // Needs WRITE_SECURE_SETTINGS — see setup instructions below
        }
    }

    override fun onInterrupt() {
        setGrayscale(false)
    }
}