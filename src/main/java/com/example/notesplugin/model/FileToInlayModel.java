package com.example.notesplugin.model;

import com.example.notesplugin.model.NoteToInlay;

import java.util.List;

public class FileToInlayModel {

    private String filePath;
    private List<NoteToInlay> inlays;


    public FileToInlayModel(String filePath, List<NoteToInlay> inlays) {
        this.filePath = filePath;
        this.inlays = inlays;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<NoteToInlay> getInlays() {
        return inlays;
    }

    public void setInlays(List<NoteToInlay> inlays) {
        this.inlays = inlays;
    }
}
