package com.focuslauncher.app

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class UsageDashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_usage_dashboard, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvScreenTime = view.findViewById<TextView>(R.id.tvTotalScreenTime)
        val tvUnlocks = view.findViewById<TextView>(R.id.tvUnlockCount)
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerViewUsage)

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.itemAnimator = null

        loadUsageData(tvScreenTime, tvUnlocks, recycler)
    }

    private fun loadUsageData(
        tvScreenTime: TextView,
        tvUnlocks: TextView,
        recycler: RecyclerView
    ) {
        try {
            val usageManager = requireContext()
                .getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

            val endTime = System.currentTimeMillis()
            val startTime = endTime - 24 * 60 * 60 * 1000L  // last 24 hours

            val stats = usageManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            )

            if (stats.isNullOrEmpty()) {
                tvScreenTime.text = "screen time: enable permission first"
                return
            }

            // Filter out system packages and very short use
            val filtered = stats
                .filter { it.totalTimeInForeground > 60_000 } // more than 1 minute
                .filter { it.packageName != requireContext().packageName }
                .sortedByDescending { it.totalTimeInForeground }

            // Calculate total
            val totalMs = filtered.sumOf { it.totalTimeInForeground }
            val totalMin = totalMs / 60_000
            tvScreenTime.text = "screen time: ${formatTime(totalMin)}"
            tvUnlocks.text = "apps tracked: ${filtered.size}"

            // Build usage items
            val maxMs = filtered.firstOrNull()?.totalTimeInForeground ?: 1L

            val items = filtered.take(20).map { stat ->
                val name = try {
                    val info = requireContext().packageManager
                        .getApplicationInfo(stat.packageName, 0)
                    requireContext().packageManager.getApplicationLabel(info).toString()
                } catch (e: Exception) {
                    stat.packageName
                }
                AppUsageInfo(
                    appName = name,
                    packageName = stat.packageName,
                    totalMinutes = stat.totalTimeInForeground / 60_000,
                    percentage = stat.totalTimeInForeground.toFloat() / maxMs.toFloat()
                )
            }

            recycler.adapter = UsageAdapter(items)

        } catch (e: Exception) {
            tvScreenTime.text = "screen time: permission needed"
        }
    }

    private fun formatTime(minutes: Long): String {
        val h = minutes / 60
        val m = minutes % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }
}