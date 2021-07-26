package com.example.android.productiva.Notes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class NoteDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ProductivaNotes.db";
    private static final int VERSION_NUMBER = 1;
    private static final String TABLE_NAME = "Notes";
    private static final String ID = "ID";
    private static final String TITLE = "Title";
    private static final String SUBTITLE = "Subtitle";
    private static final String NOTE = "Note";
    private static final String TIMESTAMP = "Timestamp";
    private static final String COLOR = "Color";


    private final Context context;
    private SQLiteDatabase database;


    //Constructor
    public NoteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
        this.context = context;
    }


    //This creates the database for the 1st time
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String createTable = "CREATE TABLE " + TABLE_NAME + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TITLE + " TEXT, "
                + SUBTITLE + " TEXT, "
                + NOTE + " TEXT, "
                + TIMESTAMP + " INTEGER, "
                + COLOR + " TEXT);";

        try {
            db.execSQL(createTable);

        } catch (Exception e) {
            Toast.makeText(context, "Exception: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    //This checks if a table exists and upgrades table version when called
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            Toast.makeText(context, "Table Upgraded", Toast.LENGTH_SHORT).show();

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);

        } catch (Exception e) {
            Toast.makeText(context, "Exception: " + e, Toast.LENGTH_SHORT).show();
        }

    }

    //This inserts data into table
    public void insertData(String title, String subtitle, String note, long timestamp, String colorHex) {

        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TITLE, title);
        contentValues.put(SUBTITLE, subtitle);
        contentValues.put(NOTE, note);
        contentValues.put(TIMESTAMP, timestamp);
        contentValues.put(COLOR, colorHex);

        long rowID = database.insert(TABLE_NAME, null, contentValues);
        database.close();

        if (rowID == -1) {
            Toast.makeText(context, "Failed to insert", Toast.LENGTH_SHORT).show();
        }

    }

    //This updates existing data
    public void updateData(int id, String title, String subtitle, String note, long timestamp, String colorHex) {
        database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(TITLE, title);
        contentValues.put(SUBTITLE, subtitle);
        contentValues.put(NOTE, note);
        contentValues.put(TIMESTAMP, timestamp);
        contentValues.put(COLOR, colorHex);


        long rowID = database.update(TABLE_NAME, contentValues, ID + "= ?", new String[]{String.valueOf(id)});
        database.close();

        if (rowID == -1) {
            Toast.makeText(context, "Failed to update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show();
        }
    }

    //This removes a table and creates new table
    public void deleteTable() {
        database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTs " + TABLE_NAME);

        Toast.makeText(context, "All data cleared", Toast.LENGTH_SHORT).show();
        onCreate(database);

    }

    //This removes a table and creates new table
    public void deleteSingle(int id) {
        database = this.getWritableDatabase();
        database.delete(TABLE_NAME, ID + "=" + id, null);
        database.close();

    }


    //This queries all data from table
    public ArrayList<NoteData> getAllData() {
        ArrayList<NoteData> allData = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                NoteData noteData = new NoteData();

                noteData.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                noteData.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                noteData.setSubtitle(cursor.getString(cursor.getColumnIndex(SUBTITLE)));
                noteData.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
                noteData.setTimestamp(cursor.getLong(cursor.getColumnIndex(TIMESTAMP)));
                noteData.setColorHex(cursor.getString(cursor.getColumnIndex(COLOR)));


                allData.add(noteData);

            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return allData;
    }

    public NoteData getSingleNote(int id) {
        NoteData noteData = new NoteData();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                + ID + " = " + id, null);

        if (cursor.moveToFirst()) {
            do {
                noteData.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                noteData.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                noteData.setSubtitle(cursor.getString(cursor.getColumnIndex(SUBTITLE)));
                noteData.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
                noteData.setTimestamp(cursor.getLong(cursor.getColumnIndex(TIMESTAMP)));
                noteData.setColorHex(cursor.getString(cursor.getColumnIndex(COLOR)));


            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();

        return noteData;
    }

    //This queries searched data from table
    public ArrayList<NoteData> getSearchedData(String searchText) {
        ArrayList<NoteData> allData = new ArrayList<>();
        database = this.getReadableDatabase();
        try {
            Cursor cursor = database.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "
                    + TITLE + " LIKE '%" + searchText + "%' OR "
                    + SUBTITLE + " LIKE '%" + searchText + "%' OR "
                    + NOTE + " LIKE '%" + searchText + "%'", null);

            if (cursor.moveToFirst()) {
                do {
                    NoteData noteData = new NoteData();

                    noteData.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                    noteData.setTitle(cursor.getString(cursor.getColumnIndex(TITLE)));
                    noteData.setSubtitle(cursor.getString(cursor.getColumnIndex(SUBTITLE)));
                    noteData.setNote(cursor.getString(cursor.getColumnIndex(NOTE)));
                    noteData.setTimestamp(cursor.getLong(cursor.getColumnIndex(TIMESTAMP)));
                    noteData.setColorHex(cursor.getString(cursor.getColumnIndex(COLOR)));

                    allData.add(noteData);

                } while (cursor.moveToNext());
            }
            cursor.close();
            database.close();

        } catch (Exception e) {
            Toast.makeText(context, "Exception: " + e, Toast.LENGTH_SHORT).show();
        }

        return allData;
    }


}
