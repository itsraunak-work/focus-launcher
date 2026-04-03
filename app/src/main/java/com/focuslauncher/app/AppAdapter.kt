package com.focuslauncher.app

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class AppInfo(
    val appName: String,
    val packageName: String
)

class AppAdapter(
    private val context: Context,
    private var apps: MutableList<AppInfo>
) : RecyclerView.Adapter<AppAdapter.AppViewHolder>() {

    class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAppName: TextView = itemView.findViewById(R.id.tvAppName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        holder.tvAppName.text = app.appName

        holder.itemView.setOnClickListener {
            launchApp(app.packageName)
        }
    }

    override fun getItemCount(): Int = apps.size

    // Called by search to filter the visible list
    fun updateApps(newApps: List<AppInfo>) {
        apps = newApps.toMutableList()
        notifyDataSetChanged()
    }

    private fun launchApp(packageName: String) {
        val intent = context.packageManager
            .getLaunchIntentForPackage(packageName)
        intent?.let { context.startActivity(it) }
    }
}