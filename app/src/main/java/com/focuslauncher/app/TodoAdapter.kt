package com.focuslauncher.app

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class TodoItem(
    val id: Long,
    var text: String,
    var isDone: Boolean = false
)

class TodoAdapter(
    private val tasks: MutableList<TodoItem>,
    private val onToggle: (TodoItem) -> Unit,
    private val onDelete: (TodoItem) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCheckbox: TextView = view.findViewById(R.id.tvCheckbox)
        val tvTaskName: TextView = view.findViewById(R.id.tvTaskName)
        val tvDelete: TextView = view.findViewById(R.id.tvDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTaskName.text = task.text

        if (task.isDone) {
            // Completed: gray + strikethrough
            holder.tvCheckbox.text = "●"
            holder.tvCheckbox.setTextColor(0xFF555555.toInt())
            holder.tvTaskName.paintFlags =
                holder.tvTaskName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvTaskName.setTextColor(0xFF3A3A3A.toInt())
        } else {
            // Pending: white + no strikethrough
            holder.tvCheckbox.text = "○"
            holder.tvCheckbox.setTextColor(0xFF3A3A3A.toInt())
            holder.tvTaskName.paintFlags =
                holder.tvTaskName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvTaskName.setTextColor(0xFFCCCCCC.toInt())
        }

        holder.itemView.setOnClickListener { onToggle(task) }
        holder.tvDelete.setOnClickListener { onDelete(task) }
    }

    override fun getItemCount() = tasks.size
}