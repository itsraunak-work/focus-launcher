package com.focuslauncher.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AppUsageInfo(
    val appName: String,
    val packageName: String,
    val totalMinutes: Long,
    val percentage: Float  // 0f to 1f — relative to the most-used app
)

class UsageAdapter(
    private val items: List<AppUsageInfo>
) : RecyclerView.Adapter<UsageAdapter.UsageViewHolder>() {

    class UsageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvUsageAppName)
        val tvTime: TextView = view.findViewById(R.id.tvUsageTime)
        val usageBar: View = view.findViewById(R.id.usageBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_app_usage, parent, false)
        return UsageViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsageViewHolder, position: Int) {
        val item = items[position]
        holder.tvName.text = item.appName
        holder.tvTime.text = formatTime(item.totalMinutes)

        // Animate the usage bar width
        holder.usageBar.post {
            val parentWidth = (holder.usageBar.parent as View).width
            val targetWidth = (parentWidth * item.percentage).toInt()
            holder.usageBar.layoutParams.width = 0
            holder.usageBar.requestLayout()
            holder.usageBar.animate()
                .withStartAction {
                    holder.usageBar.layoutParams.width = 0
                    holder.usageBar.requestLayout()
                }
                .setDuration(600)
                .setStartDelay(position * 40L)
                .start()
            // Manually animate width since animate() doesn't support width directly
            val animator = android.animation.ValueAnimator.ofInt(0, targetWidth)
            animator.duration = 600
            animator.startDelay = position * 40L
            animator.addUpdateListener { anim ->
                holder.usageBar.layoutParams.width = anim.animatedValue as Int
                holder.usageBar.requestLayout()
            }
            animator.start()
        }

        // Tap: expand animation (scale pulse)
        holder.itemView.setOnClickListener {
            it.animate()
                .scaleX(1.02f).scaleY(1.02f).setDuration(100)
                .withEndAction {
                    it.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                }.start()
        }
    }

    private fun formatTime(minutes: Long): String {
        if (minutes < 1) return "< 1m"
        val h = minutes / 60
        val m = minutes % 60
        return if (h > 0) "${h}h ${m}m" else "${m}m"
    }

    override fun getItemCount() = items.size
}