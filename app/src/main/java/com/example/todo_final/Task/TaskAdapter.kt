package com.example.todo_final.Task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_final.R

class TaskAdapter(private val tasks: Array<Task>) : RecyclerView.Adapter<TaskAdapter.TaskHolder>(){
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): TaskHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_contact, viewGroup, false)
        return TaskHolder(view)

    }

    override fun getItemCount() = tasks.size

    override fun onBindViewHolder(holder: TaskHolder, position: Int) {

        // This ensures that only the first line of the text is displayed in the Tasks list.
        val split =  tasks[position].body.split("\n")

        holder.body.tag = tasks[position].id.toString()
        holder.date.tag = tasks[position].id.toString()

        holder.body.text = split[0]
        holder.date.text = tasks[position].date
    }

    class TaskHolder(view: View) : RecyclerView.ViewHolder(view) {

        val body: TextView = view.findViewById(R.id.tv_title)
        val date: TextView = view.findViewById(R.id.tv_date)

    }

}