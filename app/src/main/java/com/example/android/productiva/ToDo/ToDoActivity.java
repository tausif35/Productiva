package com.example.android.productiva.ToDo;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class ToDoActivity extends AppCompatActivity implements ToDoAddTask.BottomSheetListener, ToDoRecyclerItemTouchHelper.ToDoRecyclerItemTouchListener {

    private ToDoDatabaseHandler db;
    private ToDoAdapter todoAdapter;
    private RecyclerView recyclerView;
    private ItemTouchHelper itemTouchHelper;
    private FloatingActionButton fab;
    private List<ToDoModel> taskList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_to_do_list);
        setTitle("");

        Toolbar toolbar = findViewById(R.id.todo_toolbar);

        //Setup toolbar as action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        //Setup db and fetch data
        db = new ToDoDatabaseHandler(getApplicationContext());
        taskList = db.getAllTasks();

        recyclerView = findViewById(R.id.tasksRecyclerView);
        fab = findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(v -> {
            ToDoAddTask toDoAddTask = new ToDoAddTask();
            toDoAddTask.show(getSupportFragmentManager(), ToDoAddTask.TAG);
        });

        recyclerViewCreation();
    }


    //When add button is pressed on bottom sheet
    @Override
    public void onAddItem(boolean isAdded, int position) {
        if (isAdded) {
            taskList = db.getAllTasks();
            todoAdapter.notifyDataSetChanged();
            recyclerViewCreation();

        } else {
            todoAdapter.notifyItemChanged(position);
        }
    }


    //Create the recyclerView with task list data
    private void recyclerViewCreation() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        todoAdapter = new ToDoAdapter(taskList, getApplicationContext());
        recyclerView.setAdapter(todoAdapter);

        itemTouchHelper = new ItemTouchHelper(new ToDoRecyclerItemTouchHelper(todoAdapter, ToDoActivity.this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    @Override
    public void onItemSwipeLeft(int position, boolean isConfirm) {
        if (isConfirm) {
            //Confirm
            db.deleteTask(taskList.get(position).getId());
            taskList.remove(position);
            todoAdapter.notifyItemRemoved(position);

        } else {
            //Cancel or dismiss
            todoAdapter.notifyItemChanged(position);
        }

    }

    @Override
    public void onItemSwipeRight(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putInt("id", taskList.get(position).getId());
        bundle.putString("task", taskList.get(position).getTask());
        ToDoAddTask toDoAddTask = new ToDoAddTask();
        toDoAddTask.setArguments(bundle);
        toDoAddTask.show(getSupportFragmentManager(), ToDoAddTask.TAG);
    }
}