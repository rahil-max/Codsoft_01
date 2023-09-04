package com.rahilcodsoft.todolist

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.content.Context
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    private lateinit var todoListView: ListView
    private lateinit var todoInput: EditText
    private lateinit var addButton: ImageButton
    private lateinit var finishButton: Button
    private lateinit var deleteButton: Button

    private val todoList: ArrayList<String> = ArrayList()
    private var selectedTaskIndex: Int = ListView.INVALID_POSITION

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoListView = findViewById(R.id.todoListView)
        todoInput = findViewById(R.id.todoInput)
        addButton = findViewById(R.id.addButton)
        finishButton = findViewById(R.id.finishButton)
        deleteButton = findViewById(R.id.deleteButton)

        loadTasks()
        val adapter = ArrayAdapter(this, R.layout.todo_item, todoList)
        todoListView.adapter = adapter

        todoListView.setOnItemClickListener { _, view, position, _ ->
            selectedTaskIndex = position
            view.isSelected = true
        }

        finishButton.setOnClickListener {
            if (selectedTaskIndex != ListView.INVALID_POSITION) {
                val completedTask = todoList[selectedTaskIndex]
                todoList.removeAt(selectedTaskIndex)
                adapter.notifyDataSetChanged()

                todoListView.setItemChecked(selectedTaskIndex, false)
                selectedTaskIndex = ListView.INVALID_POSITION

                showSuccessDialog("Successfully Completed: $completedTask")
            } else {
                Toast.makeText(this, "Please select a task to finish", Toast.LENGTH_SHORT).show()
            }
        }

        deleteButton.setOnClickListener {
            if (selectedTaskIndex != ListView.INVALID_POSITION) {
                val deletedTask = todoList[selectedTaskIndex]
                todoList.removeAt(selectedTaskIndex)
                saveTasks()
                adapter.notifyDataSetChanged()

                todoListView.setItemChecked(selectedTaskIndex, false)
                selectedTaskIndex = ListView.INVALID_POSITION

                showSuccessDialog("Successfully Deleted: $deletedTask")
            } else {
                Toast.makeText(this, "Please select a task to delete", Toast.LENGTH_SHORT).show()
            }
        }

        addButton.setOnClickListener {
            val task = todoInput.text.toString()
            if (task.isNotEmpty()) {
                todoList.add(task)
                saveTasks()
                adapter.notifyDataSetChanged()
                todoInput.text.clear()
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Success")
        builder.setMessage(message)
        builder.setPositiveButton("OK", null)
        val dialog = builder.create()
        dialog.show()
    }

    private fun saveTasks() {
        val sharedPreferences = getSharedPreferences("MyTodoList", Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        val tasksSet = HashSet(todoList)
        editor.putStringSet("tasks", tasksSet)
        editor.apply()
    }

    private fun loadTasks() {
        val sharedPreferences = getSharedPreferences("MyTodoList", Context.MODE_PRIVATE)
        val tasksSet = sharedPreferences.getStringSet("tasks", null)
        tasksSet?.let {
            todoList.addAll(it)
        }
    }
}
