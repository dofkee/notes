package com.example.notesplugin.model;

import com.example.notesplugin.model.NoteData;
import com.intellij.openapi.editor.Inlay;

public class NoteToInlay {

    Integer index;
    NoteData noteData;
    Inlay inlay;

    public NoteToInlay( NoteData noteData, Inlay inlay) {
        this.index = index;
        this.noteData = noteData;
        this.inlay = inlay;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public NoteData getNoteData() {
        return noteData;
    }

    public void setNoteData(NoteData noteData) {
        this.noteData = noteData;
    }

    public Inlay getInlay() {
        return inlay;
    }

    public void setInlay(Inlay inlay) {
        this.inlay = inlay;
    }
}
