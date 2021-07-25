package com.example.android.productiva;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.android.productiva.NoteActivities.noteCreateActivity;
import com.example.android.productiva.NoteAdapter.NoteAdapter;
import com.example.android.productiva.NoteDatabase.NoteDatabase;
import com.example.android.productiva.NoteEntities.Note;
import com.example.android.productiva.NoteListeners.NoteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class notepad extends AppCompatActivity implements NoteListener {
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    public static final int REQUEST_CODE_SHOW_NOTE = 3;
    public static final int REQUEST_CODE_SELECT_IMAGE = 4;
    public static final int REQUEST_CODE_STORAGE_PERMISSION = 5;

    private RecyclerView notesRecyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;

    private int noteClickedPosition = -1;

    private AlertDialog dialogAddURL;

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

        FloatingActionButton noteFaB = findViewById(R.id.fab_addNote);
        noteFaB.setOnClickListener(v -> startActivityForResult(new Intent(getApplicationContext(), noteCreateActivity.class), REQUEST_CODE_ADD_NOTE));


        notesRecyclerView = findViewById(R.id.notesRecyclerView);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(noteList, this);
        notesRecyclerView.setAdapter(noteAdapter);

        getNotes(REQUEST_CODE_SHOW_NOTE, false);

        EditText inputSearch = findViewById(R.id.inputSearch);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                noteAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (noteList.size() != 0) {
                    noteAdapter.searchNotes(s.toString());
                }
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Not Granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filepath;
        Cursor cursor = getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            filepath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filepath = cursor.getString(index);
            cursor.close();
        }
        return filepath;
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), noteCreateActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted) {
        @SuppressLint("StaticFieldLeak")
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDatabase.getNoteDatabase(getApplicationContext()).noteDAO().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_CODE_SHOW_NOTE) {
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    noteList.add(0, notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    noteList.remove(noteClickedPosition);

                    if (isNoteDeleted) {
                        noteAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        noteList.add(noteClickedPosition, notes.get(noteClickedPosition));
                        noteAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        String selectedImagePath = getPathFromUri(selectedImageUri);
                        Intent intent = new Intent(getApplicationContext(), noteCreateActivity.class);
                        intent.putExtra("isFromQuickActions", true);
                        intent.putExtra("quickActionType", "image");
                        intent.putExtra("imagePath", selectedImagePath);
                        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(notepad.this);
            View view = LayoutInflater.from(this).inflate(R.layout.note_add_url, (ViewGroup) findViewById(R.id.layoutAddURLContainer));
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(v -> {
                if (inputURL.getText().toString().trim().isEmpty()) {
                    Toast.makeText(notepad.this, "Enter URL", Toast.LENGTH_SHORT).show();
                } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                    Toast.makeText(notepad.this, "Please Enter A Valid URL", Toast.LENGTH_SHORT).show();
                } else {
                    dialogAddURL.dismiss();
                    Intent intent = new Intent(getApplicationContext(), noteCreateActivity.class);
                    intent.putExtra("isFromQuickActions", true);
                    intent.putExtra("quickActionType", "URL");
                    intent.putExtra("URL", inputURL.getText().toString());
                    startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogAddURL.dismiss());
        }
        dialogAddURL.show();
    }
}