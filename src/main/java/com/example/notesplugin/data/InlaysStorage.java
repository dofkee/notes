package com.example.notesplugin.data;

import com.example.notesplugin.model.FileToInlayModel;
import com.example.notesplugin.model.NoteData;
import com.example.notesplugin.model.NoteToInlay;
import com.example.notesplugin.state.NotesState;
import com.example.notesplugin.state.NotesStateComponent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InlaysStorage {

    public static final Key<NoteData> KEY = Key.create("NotesInlay");

    private static InlaysStorage INSTANCE = new InlaysStorage();

    //Key - filepath
    private Map<String, FileToInlayModel> inlays = new ConcurrentHashMap<>();
    private Integer index = 0;

    public void addInlay(String filePath, NoteToInlay inlay) {
        Map<String, FileToInlayModel> inlays = INSTANCE.getInlays();
        FileToInlayModel inlayModel = inlays.get(filePath);
        if (inlayModel == null) {
            List<NoteToInlay> inlayList = new ArrayList<>();
            inlayList.add(inlay);
            FileToInlayModel fileToInlayModel = new FileToInlayModel(filePath, inlayList);
            inlays.put(filePath, fileToInlayModel);
        } else {
            inlayModel.getInlays().add(inlay);
        }
        inlay.setIndex(index++);
    }


    public boolean removeBy(String path, NoteToInlay inlay) {
        FileToInlayModel model = INSTANCE.getModel(path);
        return model.getInlays().remove(inlay);
    }

    @Nullable
    public NoteToInlay findBy(String path, Inlay inlay) {
        FileToInlayModel fileToInlayModel = INSTANCE.getModel(path);
        return fileToInlayModel.getInlays().stream().filter(inl -> inl.getInlay().equals(inlay)).findFirst().orElse(null);
    }

    public static InlaysStorage getInstance() {
        return INSTANCE;
    }

    public Map<String, FileToInlayModel> getInlays() {
        return inlays;
    }

    public FileToInlayModel getModel(String path) {
        return inlays.get(path);
    }

    public void setInlays(Map<String, FileToInlayModel> inlays) {
        this.inlays = inlays;
    }

    /**
     * Synchronize InlayStorage -> NotesState
     */
    public void synchronize(@NotNull String filePath, @NotNull Editor editor, @NotNull String fileChecksum, @NotNull Project project) {
        NotesState notesState = NotesStateComponent.getInstance(project).getState();

        FileToInlayModel fileInlays = inlays.get(filePath);
        if (fileInlays == null) {
            return;
        }

        notesState.getNotes()
                .stream()
                .filter(note -> filePath.equals(note.getPath()))
                .forEach(note -> {
                    for (NoteToInlay inlay : fileInlays.getInlays()) {
                        NoteData userData = inlay.getInlay().getUserData(KEY);
                        if (note.getUuid().equals(userData.getUuid())) {
                            String newOffset = String.valueOf(inlay.getInlay().getOffset());
                            String oldOffset = note.getLineNumberOffset();

                            //System.out.println("Synchronize " +
//                            "[file] " + filePath + " offset " +
//                            "[old] = " + oldOffset + " " +
//                            "[new] = " + newOffset + " " +
//                            "[note] " + note.getNoteText().substring(0, Math.min(note.getNoteText().length(), 10)) + "  " +
//                            "[checkSumsSame] = " + fileChecksum.equals(note.getChecksum()) + " ");
                            note.setLineNumberOffset(newOffset);
                            note.setChecksum(fileChecksum);
//                    note.setLineNumber(String.valueOf(editor.offsetToVisualLine(Integer.parseInt(newOffset),true)  ));
                        }
                    }
                });
    }
}
