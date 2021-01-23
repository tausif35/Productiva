package com.example.android.productiva.ToDoUtils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.example.android.productiva.ToDoModel.ToDoModel;

import java.util.ArrayList;
import java.util.List;

public class ToDoDatabaseHandler extends SQLiteOpenHelper {

    private Context context;
    private SQLiteDatabase db;

    public static final int VERSION = 1;
    public static final String NAME = "toDoListDatabase";
    public static final String TODO_TABLE = "todo";
    public static final String ID = "id";
    public static final String TASK = "task";
    public static final String STATUS = "status";


    public ToDoDatabaseHandler(Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TODO_TABLE = "CREATE TABLE " + TODO_TABLE + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TASK + " TEXT, "
                + STATUS + " INTEGER)";
        try {
            Toast.makeText(context, "New table created", Toast.LENGTH_SHORT).show();
            db.execSQL(CREATE_TODO_TABLE);

        } catch (Exception e) {
            Toast.makeText(context, "Exception: " + e, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        try {
            Toast.makeText(context, "Table Upgraded", Toast.LENGTH_SHORT).show();

            db.execSQL("DROP TABLE IF EXISTs " + TODO_TABLE);
            onCreate(db);

        } catch (Exception e) {
            Toast.makeText(context, "Exception: " + e, Toast.LENGTH_SHORT).show();
        }
    }


    public void insertTask(ToDoModel task) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASK, task.getTask());
        cv.put(STATUS, 0);
        long rowID = db.insert(TODO_TABLE, null, cv);

        if (rowID == -1) {
            Toast.makeText(context, "Failed to insert data", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully inserted data", Toast.LENGTH_SHORT).show();
        }
    }


    public List<ToDoModel> getAllTasks() {
        List<ToDoModel> taskList = new ArrayList<>();
        Cursor cur = null;
        db = this.getReadableDatabase();
        try {
            cur = db.query(TODO_TABLE, null, null, null, null, null, null, null);
            if (cur.moveToFirst()) {
                do {
                    ToDoModel task = new ToDoModel();
                    task.setId(cur.getInt(cur.getColumnIndex(ID)));
                    task.setTask(cur.getString(cur.getColumnIndex(TASK)));
                    task.setStatus(cur.getInt(cur.getColumnIndex(STATUS)));
                    taskList.add(task);
                }
                while (cur.moveToNext());
            }
            cur.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return taskList;
    }


    public void updateStatus(int id, int status) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(STATUS, status);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
    }


    public void updateTask(int id, String task) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TASK, task);
        db.update(TODO_TABLE, cv, ID + "= ?", new String[]{String.valueOf(id)});
    }


    public void deleteTask(int id) {
        db = this.getWritableDatabase();
        db.delete(TODO_TABLE, ID + "= ?", new String[]{String.valueOf(id)});
    }
}