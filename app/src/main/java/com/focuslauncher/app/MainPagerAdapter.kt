package com.focuslauncher.app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class MainPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    // Page 0: Usage Dashboard
    // Page 1: Droplets (year view)
    // Page 2: Launcher (home — default)
    // Page 3: To-Do
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> UsageDashboardFragment()
        1 -> DropletsFragment()
        2 -> LauncherFragment()
        3 -> TodoFragment()
        else -> LauncherFragment()
    }
}