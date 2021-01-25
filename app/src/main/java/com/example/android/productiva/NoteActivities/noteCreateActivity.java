package com.example.android.productiva.NoteActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.productiva.NoteDatabase.NoteDatabase;
import com.example.android.productiva.NoteEntities.Note;
import com.example.android.productiva.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

//    (⌐▀͡ ̯ʖ▀)    fixed lines 126-129,302-303,

public class noteCreateActivity extends AppCompatActivity {

    private EditText inputNoteTitle, inputNoteSubtitle, inputNoteText;
    private TextView textDateTime;
    private View ViewSubtitleIndicator;
    private String selectedNoteColor;
    private String selectedImagePath;
    private ImageView imageNote;
    private TextView textWebURL;
    private LinearLayout layoutWebURL;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    private AlertDialog dialogAddURL;
    private AlertDialog dialogeDeleteNote;

    private Note alreadyAvailableNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_create_activity);

        ImageView noteGoBack = findViewById(R.id.noteGoBack);
        noteGoBack.setOnClickListener(v -> onBackPressed());
        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        ViewSubtitleIndicator = findViewById(R.id.ViewSubtitleIndicator);
        imageNote = findViewById(R.id.imageNote);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);


        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date()));

        ImageView imageSave = findViewById(R.id.imageSave);
        imageSave.setOnClickListener(v -> saveNote());
        selectedNoteColor = "#333333";
        selectedImagePath = "";

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)){
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemoveURL).setOnClickListener(v -> {
            textWebURL.setText(null);
            layoutWebURL.setVisibility(View.GONE);
        });
        findViewById(R.id.imageRemoveImage).setOnClickListener(v -> {
            imageNote.setImageBitmap(null);
            imageNote.setVisibility(View.GONE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
            selectedImagePath = "";
        });

        if(getIntent().getBooleanExtra("isFromQuickActions", false)){
            String type = getIntent().getStringExtra("quickActionType");
            if (type!=null){
                if (type.equals("image")){
                    selectedImagePath = getIntent().getStringExtra("imagePath");
                    imageNote.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
                    imageNote.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                }else if (type.equals("URL")){
                    textWebURL.setText(getIntent().getStringExtra("URL"));
                    layoutWebURL.setVisibility(View.VISIBLE);
                }
            }
        }

        initMiscellaneous();
        setSubtitleIndicatorColor();
    }

    private void setViewOrUpdateNote(){
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNote_text());
        textDateTime.setText(alreadyAvailableNote.getDate_time());

        if (alreadyAvailableNote.getImage_path() !=null && !alreadyAvailableNote.getImage_path().trim().isEmpty()){
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImage_path()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImage_path();
        }
        if (alreadyAvailableNote.getWeb_link()!= null && !alreadyAvailableNote.getWeb_link().trim().isEmpty()){
            textWebURL.setText(alreadyAvailableNote.getWeb_link());
            layoutWebURL.setVisibility(View.VISIBLE);
        }

    }

    private void saveNote() {
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Title Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputNoteSubtitle.getText().toString().trim().isEmpty() && inputNoteText.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note Cannot be Empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString());
        note.setSubtitle(inputNoteSubtitle.getText().toString());
        note.setNote_text(inputNoteText.getText().toString());
        note.setDate_time(textDateTime.getText().toString());
        note.setColor(selectedNoteColor);
        note.setImage_path(selectedImagePath);

        if (layoutWebURL.getVisibility()==View.VISIBLE){
            note.setWeb_link(textWebURL.getText().toString());
        }

        if (alreadyAvailableNote!=null){
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class SaveNoteTask extends AsyncTask<Void, Void, Void>{
            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getNoteDatabase(getApplicationContext()).noteDAO().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new SaveNoteTask().execute();
    }

    private void initMiscellaneous(){
        final LinearLayout noteMiscLayout = findViewById(R.id.noteMiscLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(noteMiscLayout);
        noteMiscLayout.findViewById(R.id.textMisc).setOnClickListener(v -> {
            if (bottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        final ImageView imageColor1 = noteMiscLayout.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = noteMiscLayout.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = noteMiscLayout.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = noteMiscLayout.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = noteMiscLayout.findViewById(R.id.imageColor5);

        noteMiscLayout.findViewById(R.id.viewColor1).setOnClickListener(v -> {
            selectedNoteColor = "#333333";
            imageColor1.setImageResource(R.drawable.ic_done);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        noteMiscLayout.findViewById(R.id.viewColor2).setOnClickListener(v -> {
            selectedNoteColor = "#00a0b0";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(R.drawable.ic_done);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        noteMiscLayout.findViewById(R.id.viewColor3).setOnClickListener(v -> {
            selectedNoteColor = "#00b159";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(R.drawable.ic_done);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        noteMiscLayout.findViewById(R.id.viewColor4).setOnClickListener(v -> {
            selectedNoteColor = "#ffcc5c";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(R.drawable.ic_done);
            imageColor5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        noteMiscLayout.findViewById(R.id.viewColor5).setOnClickListener(v -> {
            selectedNoteColor = "#cc2a36";
            imageColor1.setImageResource(0);
            imageColor2.setImageResource(0);
            imageColor3.setImageResource(0);
            imageColor4.setImageResource(0);
            imageColor5.setImageResource(R.drawable.ic_done);
            setSubtitleIndicatorColor();
        });

        if (alreadyAvailableNote !=null && alreadyAvailableNote.getColor()!= null && !alreadyAvailableNote.getColor().trim().isEmpty()){
            switch (alreadyAvailableNote.getColor()){
                case "#00a0b0":
                    noteMiscLayout.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#00b159":
                    noteMiscLayout.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#ffcc5c":
                    noteMiscLayout.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#cc2a36":
                    noteMiscLayout.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        noteMiscLayout.findViewById(R.id.layoutAddImage).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(noteCreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            } else {
                selectImage();
            }

        });
        noteMiscLayout.findViewById(R.id.layoutAddURL).setOnClickListener(v -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });

        if (alreadyAvailableNote!=null){
            noteMiscLayout.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            noteMiscLayout.findViewById(R.id.layoutDeleteNote).setOnClickListener(v -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteNoteDialog();

            });
        }
    }
    private void showDeleteNoteDialog(){
        if (dialogeDeleteNote == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(noteCreateActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.note_delete, (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer));
            builder.setView(view);
            dialogeDeleteNote = builder.create();
            if(dialogeDeleteNote.getWindow() != null){
                dialogeDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    @SuppressLint("StaticFieldLeak")
                    class DeleteNoteTask extends AsyncTask<Void, Void, Void>{

                        @Override
                        protected Void doInBackground(Void... voids) {
                            NoteDatabase.getNoteDatabase(getApplicationContext()).noteDAO().deleteNote(alreadyAvailableNote);
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            Intent intent = new Intent();
                            intent.putExtra("isNotDeleted", true);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    }
                    new DeleteNoteTask().execute();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogeDeleteNote.dismiss());
        }

        dialogeDeleteNote.show();
    }

    private void setSubtitleIndicatorColor(){
        GradientDrawable gradientDrawable = (GradientDrawable) ViewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }
    private void selectImage(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permission Not Granted!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            if (data!=null){
                Uri selectedImageUri = data.getData();
                if (selectedImageUri!= null){
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);

                        selectedImagePath = getPathFromUri(selectedImageUri);
                    }catch (Exception exception){
                        Toast.makeText(this, exception.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
    private String getPathFromUri(Uri contentUri){
        String filepath;
        Cursor cursor = getContentResolver().query(contentUri, null,null,null,null);
        if (cursor == null){
            filepath = contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filepath = cursor.getString(index);
            cursor.close();
        }
        return filepath;
    }

    private void showAddURLDialog(){
        if (dialogAddURL==null){
            AlertDialog.Builder builder = new AlertDialog.Builder(noteCreateActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.note_add_url,(ViewGroup) findViewById(R.id.layoutAddURLContainer));
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow()!=null){
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(v -> {
                if (inputURL.getText().toString().trim().isEmpty()){
                    Toast.makeText(noteCreateActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                }else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()){
                    Toast.makeText(noteCreateActivity.this,"Please Enter A Valid URL", Toast.LENGTH_SHORT).show();
                }else {
                    textWebURL.setText(inputURL.getText().toString());
                    layoutWebURL.setVisibility(View.VISIBLE);
                    dialogAddURL.dismiss();
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(v -> dialogAddURL.dismiss());
        }
        dialogAddURL.show();
    }
}