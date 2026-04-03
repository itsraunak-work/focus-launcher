package com.focuslauncher.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import java.util.Calendar

class DropletsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_droplets, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvDaysLeft = view.findViewById<TextView>(R.id.tvDropletsDaysLeft)
        val tvHoursLeft = view.findViewById<TextView>(R.id.tvHoursLeft)
        val dropletsView = view.findViewById<DayDropletView>(R.id.dropletsView)

        // Calculate days left in year
        val cal = Calendar.getInstance()
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val totalDays = cal.getActualMaximum(Calendar.DAY_OF_YEAR)
        val daysLeft = totalDays - dayOfYear
        tvDaysLeft.text = "$daysLeft days remaining in ${cal.get(Calendar.YEAR)}"

        // When today's droplet is tapped — show hours left
        dropletsView.onTodayTapped = {
            val now = Calendar.getInstance()
            val hoursLeft = 23 - now.get(Calendar.HOUR_OF_DAY)
            val minsLeft = 59 - now.get(Calendar.MINUTE)

            tvHoursLeft.text = "$hoursLeft hrs $minsLeft min left today"
            tvHoursLeft.visibility = View.VISIBLE
            tvHoursLeft.alpha = 0f
            tvHoursLeft.animate().alpha(1f).setDuration(500).start()

            // Hide again after 4 seconds
            tvHoursLeft.postDelayed({
                tvHoursLeft.animate().alpha(0f).setDuration(500).withEndAction {
                    tvHoursLeft.visibility = View.INVISIBLE
                }.start()
            }, 4000)
        }
    }
}