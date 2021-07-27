package com.example.android.productiva.Notes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.productiva.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class NotesActivity extends AppCompatActivity {

    ArrayList<NoteData> allNoteData;
    private RecyclerView recyclerView;
    private NoteRecyclerAdapter noteRecyclerAdapter;
    private Timer timer;
    private EditText searchBox;
    private NoteDatabaseHelper noteDatabaseHelper;


    //Debounce for Search Query
    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable arg0) {
            // user typed: start the timer
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    NotesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchNote();
                        }
                    });
                }
            }, 1000); // 1000ms delay before the timer executes the „run“ method from TimerTask
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // nothing to do here
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // user is typing: reset already started timer (if existing)
            if (timer != null) {
                timer.cancel();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notepad);
        setTitle("");

        Toolbar toolbar = findViewById(R.id.note_toolbar);

        //Setup toolbar as action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(v -> finish());

        noteDatabaseHelper = new NoteDatabaseHelper(getApplicationContext());
        allNoteData = noteDatabaseHelper.getAllData();

        FloatingActionButton fabAddNote = findViewById(R.id.fab_addNote);
        fabAddNote.setOnClickListener(v -> startActivity(new Intent(this, NoteCreateActivity.class)));

        recyclerView = findViewById(R.id.note_recyclerview);
        createRecyclerView();

        searchBox = findViewById(R.id.noteSearch);
        searchBox.addTextChangedListener(searchTextWatcher);

    }


    //This creates the RecyclerView
    private void createRecyclerView() {

        noteRecyclerAdapter = new NoteRecyclerAdapter(allNoteData, this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(noteRecyclerAdapter);

        noteRecyclerAdapter.setOnItemClickListener(position -> {
            //This opens a bottom sheet with details from recyclerView item
            Intent intent = new Intent(this, NoteCreateActivity.class);
            intent.putExtra("update", true);
            intent.putExtra("id", allNoteData.get(position).getId());
            startActivity(intent);
        });
    }

    private void searchNote() {
        String searchTerm = searchBox.getText().toString();
        allNoteData = noteDatabaseHelper.getSearchedData(searchTerm);
        createRecyclerView();
    }

}