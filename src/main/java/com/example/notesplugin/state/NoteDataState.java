package com.example.notesplugin.state;

import com.example.notesplugin.data.DataCenter;
import com.example.notesplugin.model.NoteData;

public class NoteDataState {

    private String noteText;
    private String content;
    private String fileName;
    private String fileType;
    private String lineNumberStart;
    private String lineNumberOffset;

    public NoteDataState() {
    }

    private NoteDataState(String noteText, String content, String fileName, String lineNumberStart, String fileType, String lineNumberOffset) {
        this.noteText = noteText;
        this.content = content;
        this.fileName = fileName;
        this.fileType = fileType;
        this.lineNumberStart = lineNumberStart;
        this.lineNumberOffset = lineNumberOffset;
    }

    public NoteDataState(String noteText, String content, String fileType ) {
        this(noteText,
                content,
                DataCenter.FILE_NAME,
                DataCenter.SELECT_TEXT_LINE_START,
                fileType,
                DataCenter.SELECT_TEXT_OFFSET
        );
    }



    public NoteDataState(NoteData from) {
        this(from.getNoteText(), from.getContent(), from.getFileName(), from.getLineNumberStart(), from.getFileType(),from.getLineNumberOffset()
        );
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getLineNumberStart() {
        return lineNumberStart;
    }

    public void setLineNumberStart(String lineNumberStart) {
        this.lineNumberStart = lineNumberStart;
    }

    public String getLineNumberOffset() {
        return lineNumberOffset;
    }

    public void setLineNumberOffset(String lineNumberOffset) {
        this.lineNumberOffset = lineNumberOffset;
    }

    @Override
    public String toString() {
        return "NoteData{" +
                " noteText='" + noteText + '\'' +
                ", content='" + content + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }

}
