package com.example.notesplugin.model;

import com.example.notesplugin.data.DataCenter;
import com.intellij.util.xmlb.annotations.Transient;

public class NoteData {

    private String uuid;
    private String noteText;
    private String content;
    private String fileName;
    private String fileType;
    private String lineNumberStart;
    private String lineNumberOffset;
    private String path;
    private String checksum;
    @Transient
    private String numberOfDuplicateOffsetsFound;

    @Transient
    private String lineNumber;
    private boolean stale;

    public NoteData(){
    }

    private NoteData(String uuid, String noteText, String content, String fileName, String lineNumberStart, String fileType, String lineNumberOffset, String path, String checksum) {
        this.noteText = noteText;
        this.content = content;
        this.fileName = fileName;
        this.fileType = fileType;
        this.lineNumberStart = lineNumberStart;
        this.lineNumberOffset = lineNumberOffset;
        this.path = path;
        this.checksum = checksum;
        this.uuid = uuid;
    }

    public NoteData(String uuid, String noteText, String content, String fileType, String checksum) {
        this(uuid, noteText,
                content,
                DataCenter.FILE_NAME,
                DataCenter.SELECT_TEXT_LINE_START,
                fileType,
                DataCenter.SELECT_TEXT_OFFSET,
                DataCenter.FILE_PATH, checksum
        );
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public Integer getLineNumberOffsetInt() {
        return Integer.valueOf(lineNumberOffset);
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

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getNumberOfDuplicateOffsetsFound() {
        return numberOfDuplicateOffsetsFound;
    }

    public void setNumberOfDuplicateOffsetsFound(String numberOfDuplicateOffsetsFound) {
        this.numberOfDuplicateOffsetsFound = numberOfDuplicateOffsetsFound;
    }

    public void setStale(boolean stale) {
        this.stale = stale;
    }

    public boolean getStale() {
        return stale;
    }
}
