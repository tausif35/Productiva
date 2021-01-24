package com.example.android.productiva.NoteDAO;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.android.productiva.NoteEntities.Note;

import java.util.List;

@Dao
public interface noteDAO {
    @Query("SELECT * FROM notes ORDER BY id DESC")
    List<Note> getAllNotes();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertNote(Note note);
    @Delete
    void deleteNote(Note note);
}
