package com.example.notesplugin.utils;

import com.example.notesplugin.Debug;
import com.example.notesplugin.data.DataCenter;
import com.example.notesplugin.data.InlaysStorage;
import com.example.notesplugin.model.NoteData;
import com.example.notesplugin.model.NoteToInlay;
import com.example.notesplugin.perf.Perf;
import com.example.notesplugin.perf.Response;
import com.intellij.codeInsight.hints.presentation.InlayPresentation;
import com.intellij.codeInsight.hints.presentation.MenuOnClickPresentation;
import com.intellij.codeInsight.hints.presentation.PresentationFactory;
import com.intellij.codeInsight.hints.presentation.PresentationRenderer;
import com.intellij.icons.AllIcons;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.Inlay;
import com.intellij.openapi.editor.InlayModel;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class InlayUtils {

    public static Inlay<?> createInlay(int offset, @NotNull NoteData noteData, @NotNull Editor editor, Project project) {
        InlayModel model = editor.getInlayModel();
        AtomicReference<Inlay<?>> ref = new AtomicReference<>();
        Inlay<?> inlay = model.addAfterLineEndElement(offset -1, false, InlayUtils.createOnClickMenuRenderer(noteData, editor, project, ref));
        Objects.requireNonNull(inlay);
        inlay.putUserData(InlaysStorage.KEY, noteData);
        ref.set(inlay);
        return inlay;
    }

    public static Integer findPotentialOffsetForNote(@NotNull NoteData note, @NotNull Editor editor, @NotNull Document document) {
        String data = document.getCharsSequence().toString();
        if (StringUtils.isEmpty(data)) {
            Debug.log("File is empty.");
            return -1;
        }
        return Perf.perf( null,  () -> {
            List<Integer> offsets = TextUtils.findOffsets(data, note.getContent());
            List<Integer> lineNumbers = PluginUtil.offsetsToLines(offsets, editor);
            Integer offset = TextUtils.findClosestOffset(offsets, note.getLineNumberOffsetInt());
            note.setNumberOfDuplicateOffsetsFound(String.valueOf(lineNumbers));
            return new Response<>("findPotentialOffsetForNote for note [" + note.getNoteText() + "] = [" + offset + "] ", offset);
        });
    }

    private static PresentationRenderer createOnClickMenuRenderer(NoteData note, Editor editor, Project project, AtomicReference<Inlay<?>> ref) {
        return new PresentationRenderer(createOnClickMenu(note, editor, project, ref));
    }

    private static MenuOnClickPresentation createOnClickMenu(NoteData note, Editor editor, Project project, AtomicReference<Inlay<?>> ref) {
        InlayPresentation text = new PresentationFactory(editor).text("      " + note.getNoteText());

        return new MenuOnClickPresentation( text, project,
                () -> Collections.singletonList(new AnAction("Delete", "Delete note", AllIcons.Actions.Cancel) {
                    @Override
                    public void actionPerformed(@NotNull AnActionEvent e1) {
                        Inlay<?> inlay = ref.get();
                        VirtualFile virtualFile = Optional.ofNullable(e1.getData(CommonDataKeys.EDITOR)).map(Editor::getVirtualFile).orElse(null);
                        if (inlay != null && virtualFile != null) {
                            String path = virtualFile.getPath();
                            NoteToInlay inlayToRemove = InlaysStorage.getInstance().findBy(path, inlay);
                            if (inlayToRemove != null) {
                                InlaysStorage.getInstance().removeBy(path, inlayToRemove);
                                DataCenter.delete(inlayToRemove.getNoteData(), project);
                            }

                            Disposer.dispose(inlay);
                        }
                    }
                }));
    }

    public static void createInlays(@NotNull NoteData noteData, @NotNull Project project, @NotNull VirtualFile file) {
        Document document = FileDocumentManager.getInstance().getDocument(file);
        Editor[] editors = Optional.ofNullable(document).map(d -> EditorFactory.getInstance().getEditors(d)).orElse( null);

        if (editors != null && editors.length > 0) {
            Inlay<?> inlay = InlayUtils.createInlay( noteData.getLineNumberOffsetInt(), noteData, editors[0], project);
            InlaysStorage.getInstance().addInlay(noteData.getPath(), new NoteToInlay(noteData, inlay));
        }
    }
}
