package com.example.android.productiva.Notes;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NoteCreateActivity extends AppCompatActivity {

    private EditText inputTitle, inputSubtitle, inputNoteText;
    private TextView tvNoteDateTime;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm a", Locale.getDefault());
    private boolean isEdit;
    private long timestamp;
    private String color;
    private RecyclerView colorPicker;
    private ColorRecyclerAdapter colorRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_create);

        Toolbar toolbar = findViewById(R.id.noteCreate_toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        //Check if the call is for adding new date or update existing data
        isEdit = getIntent().getBooleanExtra("update", false);

        inputTitle = findViewById(R.id.input_noteTitle);
        inputSubtitle = findViewById(R.id.input_noteSubtitle);
        inputNoteText = findViewById(R.id.input_noteText);
        tvNoteDateTime = findViewById(R.id.textView_noteDateTime);
        colorPicker = findViewById(R.id.noteColorPicker);

        //Set toolbar title and check if it's for record update or not
        if (!isEdit) { //Add new record
            setTitle("Add note");
            setViewsNew();

        } else { //Edit existing record
            setTitle("Edit note");
            setViewsEdit();
        }

        createColorRecyclerView();

    }

    //This creates the color RecyclerView
    private void createColorRecyclerView() {
        int[] colorArray = getResources().getIntArray(R.array.colorArray);
        colorRecyclerAdapter = new ColorRecyclerAdapter(colorArray, this);
        colorPicker.setHasFixedSize(true);
        colorPicker.setAdapter(colorRecyclerAdapter);

        colorRecyclerAdapter.setOnItemClickListener(position -> {
            color = "#" + Integer.toHexString(colorArray[position]);
            inputTitle.setBackgroundColor(Color.parseColor(color));
        });

    }


    //This is for when we want to edit note
    private void setViewsEdit() {
        NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
        NoteData noteData = noteDatabaseHelper.getSingleNote(getIntent().getIntExtra("id", 0));
        timestamp = noteData.getTimestamp();
        tvNoteDateTime.setText(simpleDateFormat.format(timestamp));
        inputTitle.setText(noteData.getTitle());
        inputSubtitle.setText(noteData.getSubtitle());
        inputNoteText.setText(noteData.getNote());
        color = noteData.getColorHex();
        inputTitle.setBackgroundColor(Color.parseColor(color));

    }

    //This is for adding new note
    private void setViewsNew() {
        timestamp = new Date().getTime();
        tvNoteDateTime.setText(simpleDateFormat.format(timestamp));
        color = "#" + Integer.toHexString(ActivityCompat.getColor(this, R.color.colorPrimary));

    }


    // Toolbar menu inflate
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        if (!isEdit)
            menu.findItem(R.id.menu_delete).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    // Toolbar menu item select
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save) {
            saveNote();
        } else if (item.getItemId() == R.id.menu_colorPicker) {
            if (colorPicker.getVisibility() == View.VISIBLE)
                colorPicker.setVisibility(View.GONE);
            else
                colorPicker.setVisibility(View.VISIBLE);

        } else if (item.getItemId() == R.id.menu_delete) {
            deleteNote();
        }
        return super.onOptionsItemSelected(item);
    }


    //This will save the note
    private void saveNote() {
        if (inputTitle.getText().toString().trim().length() > 0) {

            if (inputSubtitle.getText().toString().trim().length() > 0) {

                NoteDatabaseHelper noteDatabaseHelper = new NoteDatabaseHelper(this);
                String title, subtitle, noteText = null;
                title = inputTitle.getText().toString();
                subtitle = inputSubtitle.getText().toString();
                noteText = inputNoteText.getText().toString();

                //If not update data, insert new data
                if (!isEdit) {
                    noteDatabaseHelper.insertData(title, subtitle, noteText, timestamp, color);
                }
                //If update, update existing data
                else {
                    int id = getIntent().getIntExtra("id", -1);
                    noteDatabaseHelper.updateData(id, title, subtitle, noteText, timestamp, color);
                }

                Intent intent = new Intent(this, NotesActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));


            } else {
                Toast.makeText(this, "Please set subtitle for the note", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(this, "Please set note title", Toast.LENGTH_SHORT).show();
        }

    }

    //This will delete current note
    private void deleteNote() {
        // Show warning alert dialog
        new AlertDialog.Builder(this)
                .setTitle("Are you sure?")
                .setMessage("This will delete this note's data completely")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", (dialog, which) -> { // When user confirms
                    new NoteDatabaseHelper(this).deleteSingle(getIntent().getIntExtra("id", -1));
                    Intent intent = new Intent(this, NotesActivity.class);
                    startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                })
                .show();

    }

}