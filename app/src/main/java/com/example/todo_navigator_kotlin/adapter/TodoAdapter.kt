package com.example.todo_navigator_kotlin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.todo_navigator_kotlin.R
import com.example.todo_navigator_kotlin.model.Todo

class TodoAdapter(
    private val todos: MutableList<Todo>,
    private val onItemClick: (Todo) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val todoContent: TextView = view.findViewById(R.id.todoContent)
        private val cbDone: CheckBox = view.findViewById(R.id.checkboxDone)

        fun bind(todo: Todo) {
            todoContent.text = todo.content
            cbDone.isChecked = todo.isDone

            cbDone.setOnCheckedChangeListener { _, isChecked ->
                todo.isDone = isChecked
            }

            itemView.setOnClickListener {
                onItemClick(todo)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount(): Int {
        return todos.size
    }

    fun updateTodos(newTodos: List<Todo>) {
        todos.clear()
        todos.addAll(newTodos)
        notifyDataSetChanged()
    }
}
