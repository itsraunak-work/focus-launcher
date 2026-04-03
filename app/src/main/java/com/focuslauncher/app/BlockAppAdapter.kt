package com.focuslauncher.app

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BlockAppAdapter(
    private val apps: List<AppInfo>,
    private val prefs: SharedPreferences
) : RecyclerView.Adapter<BlockAppAdapter.BlockViewHolder>() {

    class BlockViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAppName: TextView = view.findViewById(R.id.tvBlockAppName)
        val tvToggle: TextView = view.findViewById(R.id.tvToggle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_block_app, parent, false)
        return BlockViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlockViewHolder, position: Int) {
        val app = apps[position]
        val key = "block_${app.packageName}"
        val isBlocked = prefs.getBoolean(key, false)

        holder.tvAppName.text = app.appName
        updateToggle(holder.tvToggle, isBlocked)

        holder.itemView.setOnClickListener {
            val newState = !prefs.getBoolean(key, false)
            prefs.edit().putBoolean(key, newState).apply()
            updateToggle(holder.tvToggle, newState)
        }
    }

    private fun updateToggle(tv: TextView, isBlocked: Boolean) {
        if (isBlocked) {
            tv.text = "●"
            tv.setTextColor(0xFFFFFFFF.toInt())
        } else {
            tv.text = "○"
            tv.setTextColor(0xFF333333.toInt())
        }
    }

    override fun getItemCount() = apps.size
}