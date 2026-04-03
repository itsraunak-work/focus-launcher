package com.focuslauncher.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class TodoFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var etNewTask: EditText
    private lateinit var tvAddTask: TextView
    private lateinit var tvDate: TextView
    private lateinit var todoAdapter: TodoAdapter

    private val tasks = mutableListOf<TodoItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_todo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewTasks)
        etNewTask = view.findViewById(R.id.etNewTask)
        tvAddTask = view.findViewById(R.id.tvAddTask)
        tvDate = view.findViewById(R.id.tvTodoDate)

        // Show day name in lowercase
        val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        tvDate.text = dayFormat.format(Date()).lowercase()

        loadTasks()

        todoAdapter = TodoAdapter(
            tasks,
            onToggle = { task ->
                task.isDone = !task.isDone
                saveTasks()
                todoAdapter.notifyDataSetChanged()
            },
            onDelete = { task ->
                tasks.remove(task)
                saveTasks()
                todoAdapter.notifyDataSetChanged()
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = todoAdapter
        recyclerView.itemAnimator = null

        tvAddTask.setOnClickListener { addTask() }

        etNewTask.setOnEditorActionListener { _, _, _ ->
            addTask()
            true
        }
    }

    private fun addTask() {
        val text = etNewTask.text.toString().trim()
        if (text.isNotEmpty()) {
            tasks.add(0, TodoItem(System.currentTimeMillis(), text))
            saveTasks()
            todoAdapter.notifyDataSetChanged()
            etNewTask.setText("")
        }
    }

    private fun saveTasks() {
        val prefs = requireContext()
            .getSharedPreferences("focus_prefs", 0)
        val array = JSONArray()
        tasks.forEach { task ->
            val obj = JSONObject()
            obj.put("id", task.id)
            obj.put("text", task.text)
            obj.put("done", task.isDone)
            array.put(obj)
        }
        prefs.edit().putString("tasks", array.toString()).apply()
    }

    private fun loadTasks() {
        val prefs = requireContext()
            .getSharedPreferences("focus_prefs", 0)
        val json = prefs.getString("tasks", "[]") ?: "[]"
        val array = JSONArray(json)
        tasks.clear()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            tasks.add(
                TodoItem(
                    id = obj.getLong("id"),
                    text = obj.getString("text"),
                    isDone = obj.getBoolean("done")
                )
            )
        }
    }
}