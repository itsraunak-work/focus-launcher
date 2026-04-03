package com.focuslauncher.app

import android.content.Intent
import android.content.pm.ResolveInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class LauncherFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var tvTime: TextView
    private lateinit var tvDate: TextView
    private lateinit var etSearch: EditText
    private lateinit var tvBlock: TextView
    private lateinit var tvLockBtn: TextView

    // Add this variable declaration at the top of the class (with the others)
    private lateinit var tvSettings: TextView
    private lateinit var appAdapter: AppAdapter

    private var allApps: List<AppInfo> = emptyList()

    private val handler = Handler(Looper.getMainLooper())

    private val clockRunnable = object : Runnable {
        override fun run() {
            updateClock()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_launcher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Connect views
        tvTime = view.findViewById(R.id.tvTime)
        tvDate = view.findViewById(R.id.tvDate)
        recyclerView = view.findViewById(R.id.recyclerView)
        etSearch = view.findViewById(R.id.etSearch)
        tvBlock = view.findViewById(R.id.tvBlock)
        tvLockBtn = view.findViewById(R.id.tvLockBtn)

        setupAppList()
        setupSearch()

        // Open block settings
        tvBlock.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        // Open lock screen (year droplets)
        tvLockBtn.setOnClickListener {
            startActivity(Intent(requireContext(), LockScreenActivity::class.java))
        }

        // Settings button
        tvSettings = view.findViewById(R.id.tvSettings)
        tvSettings.setOnClickListener {
            startActivity(Intent(requireContext(), LauncherSettingsActivity::class.java))
        }
    }

    private fun setupAppList() {
        allApps = getInstalledApps()
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        appAdapter = AppAdapter(requireContext(), allApps.toMutableList())
        recyclerView.adapter = appAdapter

        // Smooth scroll animation
        recyclerView.itemAnimator = null
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().trim().lowercase()
                val filtered = if (query.isEmpty()) {
                    allApps
                } else {
                    allApps.filter { it.appName.lowercase().contains(query) }
                }
                appAdapter.updateApps(filtered)
            }
        })
    }

    private fun getInstalledApps(): List<AppInfo> {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfoList: List<ResolveInfo> = requireContext().packageManager
            .queryIntentActivities(intent, 0)

        return resolveInfoList
            .map { resolveInfo ->
                AppInfo(
                    appName = resolveInfo.loadLabel(requireContext().packageManager).toString(),
                    packageName = resolveInfo.activityInfo.packageName
                )
            }
            .sortedBy { it.appName.lowercase() }
    }

    private fun updateClock() {
        val now = Calendar.getInstance()
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
        tvTime.text = timeFormat.format(now.time)
        tvDate.text = dateFormat.format(now.time).lowercase()
    }

    override fun onResume() {
        super.onResume()
        handler.post(clockRunnable)
        setupAppList()
        etSearch.setText("")
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(clockRunnable)
    }
}