package com.example.todo_final.DeletedTask

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_final.R

class DeletedTaskAdapter(private val deletedTasks: Array<DeletedTask>) : RecyclerView.Adapter<DeletedTaskAdapter.DeletedTaskHolder>(){
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): DeletedTaskHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.deleted_concat, viewGroup, false)
        return DeletedTaskHolder(view)

    }

    override fun getItemCount() = deletedTasks.size

    override fun onBindViewHolder(holder: DeletedTaskHolder, position: Int) {

        holder.id.text = deletedTasks[position].id.toString()
        holder.creationDate.text = deletedTasks[position].creationDate
        holder.deletionDate.text = deletedTasks[position].deletionDate
    }

    class DeletedTaskHolder(view: View) : RecyclerView.ViewHolder(view) {

        val id: TextView = view.findViewById(R.id.deletedId)
        val creationDate: TextView = view.findViewById(R.id.creationDate)
        val deletionDate: TextView = view.findViewById(R.id.deletionDate)
    }
}

