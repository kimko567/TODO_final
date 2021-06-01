package com.example.todo_final

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.todo_final.DeletedTask.DeletedTask
import com.example.todo_final.DeletedTask.DeletedTaskAdapter
import com.example.todo_final.DeletedTask.DeletedTaskDao
import com.example.todo_final.Task.Task
import com.example.todo_final.Task.TaskAdapter
import com.example.todo_final.Task.TaskDao
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext


class MainActivity() : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main


    private lateinit var db: AppDataBase

    private lateinit var taskDao: TaskDao
    private lateinit var taskAdapter: TaskAdapter

    private lateinit var deletedTaskDao: DeletedTaskDao
    private lateinit var deletedTaskAdapter: DeletedTaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Creates Room database and assigns it to a variable.
        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java, "task_database"
        ).fallbackToDestructiveMigration().build()

        // Creates Task class/entity DAO.
        taskDao = db.taskDao()

        // Creates DeletedTask class/entity DAO.
        deletedTaskDao = db.deletedTaskDao()

        // Ensures the search bar works.
        searchAction()

        // Sets the layout manager for RecylcerView.
        findViewById<RecyclerView>(R.id.rvItems).layoutManager = LinearLayoutManager(this)

        // A coroutine the executes data begin loaded from database.
        launch {
            // Assigns proper adapter to Task entity.
            inita()

            // Assigns proper adapter to DeletedTask entity.
            initaDeleted()

            // Sets RecyclerView adapter to the TaskAdapter class adapter.
            setRecyclerViewTasks()
        }
    }

    /**
     * Assigns proper adapter to Task entity based on search TextInputLayout.
     */
    suspend fun getContains(search: String){
        async(Dispatchers.IO){taskAdapter = TaskAdapter(taskDao.getContains(search)) }.await()
    }


    /**
     * Assigns proper adapter to Task entity with all table entries.
     */
    suspend fun inita(){
        async(Dispatchers.IO){taskAdapter = TaskAdapter(taskDao.getAll()) }.await()
    }

    /**
     * Assigns proper adapter to DeletedTask entity with all table entries.
     */
    suspend fun initaDeleted(){
        async(Dispatchers.IO){deletedTaskAdapter = DeletedTaskAdapter(deletedTaskDao.getAll()) }.await()
    }

    /**
     * Expects body parameter of type String to be used as the description/body of the Task.
     * Inserts a new Task into data base.
     */
    fun insertIntoDb(body: String){
        launch(Dispatchers.IO) {
            taskDao.insertItem(Task(body))
        }
    }

    /**
     * Sets RecyclerView adapter to be Task entity adapter.
     */
    fun setRecyclerViewTasks(){
        findViewById<RecyclerView>(R.id.rvItems).adapter = taskAdapter
    }

    /**
     * Sets RecyclerView adapter to be DeletedTask entity adapter.
     */
    fun setRecyclerViewDeletedTasks(){
        findViewById<RecyclerView>(R.id.rvDeleted).adapter = deletedTaskAdapter
    }

    /**
     * Changes content view for the layout to create a new Task.
     */
    fun createNewItem(view: View){
        setContentView(R.layout.item_create)
        findViewById<Button>(R.id.saveButton).setText("Create")
    }

    /**
     * Changes contetn view for the layout to create a new Task.
     */
    fun createNewItem(){
        setContentView(R.layout.item_create)
        findViewById<Button>(R.id.saveButton).setText("Create")
    }

    /**
     * Reloads the Task list / activity_main.xml
     */
    fun cancel(view: View){
        reLoad()
    }

    /**
     * Determines if the Task is new or already exists.
     * If Task is determined to be new, it is inserted into data base.
     * Otherwise the existing Task is being updated.
     */
    fun createOrSave(view: View) {
        // Gets the body/description of the task from the EditText.
        val body: String? = findViewById<TextInputEditText>(R.id.bodyInput).text?.toString()

        // Assigns the button to a value.
        val button = findViewById<Button>(R.id.saveButton)

        // Checks if the button is in "create mode".
        // "Create mode" means that it will insert a value into data base.
        if (button.text == "Create") {
            // An empty Task won't be inserted into data base.
            if (body != null && body != "")
                // Inserts into data base.
                insertIntoDb(body)
        }
        // Checks if the button is in "update mode".
        // "Update mode" means the Task will be updated.
        else {
            // Checks if there is a body to be updated.
            // If there is no body, the Tas kis deleted instead.
            if (body == null || body == "") {
                launch {
                    async(Dispatchers.IO) {
                        // Assigns the Task to be deleted to a value.
                        val tmpItem = taskDao.get(button.tag.toString().toInt())

                        // Deletes the Task.
                        taskDao.deleteItem(tmpItem)
                    }.await()
                }
            }
            // In case an update is about to happen.
            else {
                launch {
                    async(Dispatchers.IO) {
                        // Gets the id of the Task that is to be updated.
                        // The id is stored as a tag for the button component.
                        var id = button.tag.toString().toInt()

                        // Assigns value to the Task that is to be updated.
                        val tmpItem = Task(id, body)

                        // Updates the Task.
                        taskDao.updateItem(tmpItem)
                    }.await()
                }
            }
        }
        // Reloads the Task list.
         reLoad()
    }

    /**
     * Ensures the everything is ready for an update to be made.
     */
    fun update(view: View) {
        // Changes content view to where the Task can be updated.
        setContentView(R.layout.item_edit)

        // Changes the Button name to Save instead of Create.
        findViewById<Button>(R.id.saveButton).setText("Save")

        // Assigns a tag to the buttons to keep the id of the Task that is about to be updated..
        findViewById<Button>(R.id.saveButton).tag = view.tag.toString()
        findViewById<Button>(R.id.deleteButton).tag = view.tag.toString()

        launch {
            // Assigns Task's body to a value.
            val body: String = async(Dispatchers.IO) { taskDao.get(view.tag.toString().toInt()).body }.await()

            // Sets the EdtText to have the body already shown.
            findViewById<TextInputEditText>(R.id.bodyInput).setText(body)
        }
    }

    /**
     * Deletes the active Task.
     */
    fun delete(view: View){
        // Assigns the save button to a value.
        val button = findViewById<Button>(R.id.saveButton)
        launch {
            // Gets the active Task and assigns it to a variable.
            var task: Task = async(Dispatchers.IO) { taskDao.get(view.tag.toString().toInt()) }.await()

            // Assigns the Task to be deleted to a value.
            val tmpItem = Task(task.id, task.body)
             async(Dispatchers.IO) {
                 // eletes the Task.
                 taskDao.deleteItem(tmpItem)

                 // Inserts creation date and Task id into the deleted tasks table.
                 deletedTaskDao.insert(DeletedTask(task.id, task.date))}.await()

            // Reloads the tasks list.
            reLoad()
        }

    }

    /**
     * Creates the correct options menu list.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu1, menu)
        return true
    }

    /**
     * Assigns actions to all options menu items.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            // Reloads the tasks list.
            R.id.taskList -> reLoad()

            // Sets up everything for creating a new Task.
            R.id.menuAdd -> createNewItem()

            // Shows the deleted Tasks.
            R.id.deleted -> {setContentView(R.layout.deleted_list)
                findViewById<RecyclerView>(R.id.rvDeleted).layoutManager = LinearLayoutManager(this)
                launch {
                    initaDeleted()
                    setRecyclerViewDeletedTasks()
                }
            }
            // Exits the programm.
            R.id.menuExit -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Reloads the tasks list.
     */
    fun reLoad(){
        setContentView(R.layout.activity_main)
        findViewById<RecyclerView>(R.id.rvItems).layoutManager = LinearLayoutManager(this)
        launch {
            inita()
            setRecyclerViewTasks()
        }
        searchAction()
    }

    /**
     * Ensures the search bar works properly by adding TextChangedListener.
     */
    fun searchAction(){
        findViewById<EditText>(R.id.searchInput).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                launch {
                    getContains(findViewById<EditText>(R.id.searchInput).text.toString())
                    setRecyclerViewTasks()
                }
            }
        })
    }
}
