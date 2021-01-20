package com.example.android.productiva;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.android.productiva.ToDoAdapter.ToDoAdapter;
import com.example.android.productiva.ToDoModel.ToDoModel;
import com.example.android.productiva.ToDoUtils.ToDoDatabaseHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ToDoList extends AppCompatActivity implements ToDoDialogCloseListener{

    private ToDoDatabaseHandler db;

    private RecyclerView todoRecyclerView;
    private ToDoAdapter todoAdapter;
    private FloatingActionButton fab;

    private List<ToDoModel> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);
        Objects.requireNonNull(getSupportActionBar()).hide();

        db = new ToDoDatabaseHandler(this);
        db.openDatabase();

        todoRecyclerView = findViewById(R.id.tasksRecyclerView);
        todoRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        todoAdapter = new ToDoAdapter(db,ToDoList.this);
        todoRecyclerView.setAdapter(todoAdapter);

        ItemTouchHelper itemTouchHelper = new
                ItemTouchHelper(new ToDoRecyclerItemTouchHelper(todoAdapter));
        itemTouchHelper.attachToRecyclerView(todoRecyclerView);

        fab = findViewById(R.id.fab);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);

        todoAdapter.setTasks(taskList);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToDoAddTask.newInstance().show(getSupportFragmentManager(), ToDoAddTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog){
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        todoAdapter.setTasks(taskList);
        todoAdapter.notifyDataSetChanged();
    }
}