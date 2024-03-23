package com.example.notesplugin.state;

import com.example.notesplugin.Cloner;
import com.example.notesplugin.model.NoteData;

import java.util.ArrayList;
import java.util.List;

public class NotesState implements Cloneable  {

    private List<NoteData> notes = new ArrayList<>();

    public void addToNotes(NoteData noteData) {
        notes.removeIf(m -> m.getNoteText().equals(noteData.getNoteText()) && m.getContent().equals(noteData.getContent()));
        notes.add(noteData);
    }

    public void removeFromNotes(NoteData noteData) {
        if (noteData != null) {
            notes.remove(noteData);
        }
    }

    public void removeFromNotes(Integer index) {
        if (index != null) {
            notes.remove( notes.get(index));
        }
    }

    @Override
    public NotesState clone() {
        return Cloner.deepClone(this);
    }

    public List<NoteData> getNotes() {
        return notes;
    }

    public void setNotes(List<NoteData> notes) {
        this.notes = notes;
    }

}
