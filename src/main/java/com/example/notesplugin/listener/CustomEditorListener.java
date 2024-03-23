package com.example.notesplugin.listener;

import com.example.notesplugin.data.DataCenter;
import com.example.notesplugin.data.InlaysStorage;
import com.example.notesplugin.model.FileToInlayModel;
import com.example.notesplugin.model.NoteData;
import com.example.notesplugin.model.NoteToInlay;
import com.example.notesplugin.state.NotesStateComponent;
import com.example.notesplugin.utils.InlayUtils;
import com.example.notesplugin.utils.Perf;
import com.example.notesplugin.utils.PluginUtil;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;


public class CustomEditorListener implements EditorFactoryListener {

    //TODO -- add aproximal location search in case if file was changed remotely or by someone else.
    // In state - store hash of file
    // If not possible to locate in some fixed range - mark note as stale
    // If line contains - code part - do nothing.
    // check only first line from note



    /**
     * This method invoked :
     * - when editor is opened.
     *
     * FileToInlayModel stores existing file inlays
     * @param event
     */
    @Override
    public void editorCreated(@NotNull EditorFactoryEvent event) {
        Editor editor = event.getEditor();
        Project project = editor.getProject();
        if (project == null) return;

        VirtualFile virtualFile = editor.getVirtualFile();

        if (virtualFile == null) {
            return;
        }
        String path = virtualFile.getPath();
        System.out.println("Editor created " + path);
        FileToInlayModel fileInlayModel = InlaysStorage.getInstance().getModel(path);
        //When editor reopened
        if (fileInlayModel != null) {

            Perf.perf( "fileInlayModel.getInlays()" , () -> {
                fileInlayModel.getInlays().forEach(inlay -> {
                    InlayUtils.createInlay(inlay.getNoteData().getLineNumberOffsetInt(), inlay.getNoteData(), editor, project);
                });
            });


            return;
        }
        final String[] virtualFileChecksum = {null};
        Document document = PluginUtil.getDocumentFromVF(virtualFile);
        List<NoteData> notesFromState = NotesStateComponent.getInstance(project).getState().getNotes();
        notesFromState
                .stream()
                .filter(note -> virtualFile.getPath().contains(note.getPath()))
                .forEach(note -> {
                    virtualFileChecksum[0] = virtualFileChecksum[0] == null ? PluginUtil.calculateMD5(virtualFile) : virtualFileChecksum[0];

                    Integer offset = note.getChecksum().equals(virtualFileChecksum[0]) ?
                            note.getLineNumberOffsetInt() :
                            InlayUtils.findPotentialOffsetForNote(note, editor, document);
                    if (offset > -1) {
                        Inlay<?> inlay = InlayUtils.createInlay(offset, note, editor, project);
                        note.setLineNumberOffset(String.valueOf(offset));
                        note.setLineNumber(String.valueOf(editor.offsetToVisualLine(offset, true)));
                        InlaysStorage.getInstance().addInlay(path, new NoteToInlay(note, inlay));
                    }
                });

        DataCenter.refreshTable(project);

    }

}