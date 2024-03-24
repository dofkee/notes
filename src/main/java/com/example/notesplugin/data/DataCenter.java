package com.example.notesplugin.data;

import com.example.notesplugin.model.FileToInlayModel;
import com.example.notesplugin.model.NoteData;
import com.example.notesplugin.model.NoteToInlay;
import com.example.notesplugin.perf.Response;
import com.example.notesplugin.state.NotesStateComponent;
import com.example.notesplugin.utils.InlayUtils;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ui.EDT;
import org.apache.commons.lang3.BooleanUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.notesplugin.perf.Perf.perf;

public class DataCenter {
    public static String FILE_PATH ;
    public static String SELECT_TEXT_LINE_START;
    public static String SELECT_TEXT_OFFSET;
    public static String SELECT_TEXT;
    public static String FILE_NAME;
    public static Integer FILE_NAME_COLUMN = 3;
    private static final List<NoteData> NOTE_LIST = new LinkedList<>();
    private static final Map<NoteData, String> NOTE_DUPLICATE_DATA_MAP= new HashMap<>();

    public static String[] HEAD = { "!","#","Note", "File", "Code"};

    public static DefaultTableModel TABLE_MODEL = new DefaultTableModel(null, HEAD) {

        @Override
        public Class<?> getColumnClass(int column) {
            switch (column) {
                case 0:
                    return Icon.class;
                default:
                    return super.getColumnClass(column);
            }
        }
    };

    public static void add(@NotNull NoteData noteData, @NotNull Project project, @Nullable VirtualFile file) {
        Objects.requireNonNull(noteData.getUuid(), "noteData.getUuid() can not be null");
        perf( "DataCenter_add" , () -> {
            perf("NOTE_LIST.add" , () ->    DataCenter.NOTE_LIST.add(noteData)  );
            DataCenter.NOTE_DUPLICATE_DATA_MAP.put(noteData, String.valueOf(0));
            perf("DataCenter.TABLE_MODEL.addRow", () -> {
                Object[] convert = perf("convert ", () -> new Response<>("" ,  DataConvert.convert(noteData, noteData.getLineNumber())));
                perf("TABLE_MODEL.addRow" , () -> DataCenter.TABLE_MODEL.addRow(convert));
            });
        });

        if (!EDT.isCurrentThreadEdt()) {
            return;
        }
        perf("getState().addToNotes(noteData)" , () -> NotesStateComponent.getInstance(project).getState().addToNotes(noteData));

        Objects.requireNonNull(file, "file can not be null");
        perf("InlayUtils.createInlays()" , () -> InlayUtils.createInlays(noteData, project, file));
    }

    public static NoteData get(Integer index) {
        return NOTE_LIST.get(index);
    }

    public static void reset(Project project) {
        NOTE_LIST.forEach(noteData -> NotesStateComponent.getInstance(project).getState().removeFromNotes(noteData));
        NOTE_DUPLICATE_DATA_MAP.clear();
        NOTE_LIST.clear();
        TABLE_MODEL.setDataVector(null, HEAD);
    }

    public static void refreshTable(Project project) {
        DataCenter.TABLE_MODEL.setDataVector(null, HEAD);
        for (NoteData noteData : NOTE_LIST) {
            DataCenter.TABLE_MODEL.addRow(DataConvert.convert(noteData, noteData.getLineNumber()));
        }
    }

    public static void update(Integer index, NoteData noteData, Project project, String noteUUID) {
        TABLE_MODEL.removeRow(index);
        NotesStateComponent.getInstance(project).getState().removeFromNotes(index);
        NotesStateComponent.getInstance(project).getState().addToNotes(noteData);
        TABLE_MODEL.insertRow(index, DataConvert.convert(noteData , noteData.getLineNumber()));
        FileToInlayModel fileToInlayModel = InlaysStorage.getInstance().getModel(DataCenter.FILE_PATH);

        for (NoteToInlay inlay : fileToInlayModel.getInlays()) {
            Editor editor = inlay.getInlay().getEditor();
            if (inlay.getNoteData().getUuid().equals(noteUUID)) {
                inlay.setNoteData(noteData);
                InlayUtils.createInlay(inlay.getNoteData().getLineNumberOffsetInt(), inlay.getNoteData(), editor, project);
                inlay.getInlay().dispose();
                break;
            }
        }
    }

    public static void selectNote( NoteData noteData, Project project) {
        FILE_NAME = noteData.getFileName();
        FILE_PATH = noteData.getPath();
        SELECT_TEXT_OFFSET = noteData.getLineNumberOffset();
    }

    public static void delete(NoteData noteData, Project project) {
        NOTE_LIST.remove(noteData);
        NotesStateComponent.getInstance(project).getState().removeFromNotes(noteData);
        FileToInlayModel fileToInlayModel = InlaysStorage.getInstance().getModel(noteData.getPath());
        if (fileToInlayModel != null) {
            NoteToInlay inlayToRemove = null;
            for (NoteToInlay inlay : fileToInlayModel.getInlays()) {
                if (inlay.getNoteData().equals(noteData)) {
                    inlay.getInlay().dispose();
                    inlayToRemove = inlay;
                    break;
                }
            }
            fileToInlayModel.getInlays().remove(inlayToRemove);
        }

        refreshTable(project);
    }

    public static void delete(int index, Project project) {
        NoteData data = NOTE_LIST.remove(index);
        TABLE_MODEL.removeRow(index);
        NotesStateComponent.getInstance(project).getState().removeFromNotes(data);
        FileToInlayModel fileToInlayModel = InlaysStorage.getInstance().getModel(data.getPath());
        if (fileToInlayModel != null) {
            NoteToInlay inlayToRemove = null;
            for (NoteToInlay inlay : fileToInlayModel.getInlays()) {
                if (inlay.getNoteData().getUuid().equals(data.getUuid())) {
                    inlay.getInlay().dispose();
                    inlayToRemove = inlay;
                    break;
                }
            }
            fileToInlayModel.getInlays().remove(inlayToRemove);
        }
    }

    public static class DataConvert {

        public static Icon warnIcon = new ImageIcon("/home/dovis/IdeaProjects/notes/notes/src/main/resources/META-INF/wrnr.png");
        static Integer numberOfColumns = 5;

        public static Object[] convert(NoteData noteData, String lineNumber) {
            Object[] raw = new Object[numberOfColumns];
            int index = 0;
            raw[index++] = BooleanUtils.isTrue(noteData.getStale()) ? warnIcon : null;
            raw[index++] = lineNumber;
            raw[index++] = noteData.getNoteText();
            raw[index++] = noteData.getFileName(); FILE_NAME_COLUMN = index - 1;
            raw[index++] = noteData.getContent();
            return raw;
        }
    }
}
