package com.example.android.productiva.NoteListeners;

import com.example.android.productiva.NoteEntities.Note;

public interface NoteListener {
    void onNoteClicked(Note note, int position);
}
