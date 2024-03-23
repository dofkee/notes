package com.example.notesplugin.action;

import com.example.notesplugin.data.DataCenter;
import com.example.notesplugin.ui.NoteDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

public class PopupAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        SelectionModel selectionModel = editor.getSelectionModel();

        DataCenter.SELECT_TEXT = selectionModel.getSelectedText();

        int lineNumberStart = editor.getDocument().getLineNumber( selectionModel.getSelectionStart());
//        int lineNumberEnd =  editor.getDocument().getLineNumber( selectionModel.getSelectionEnd());

        DataCenter.SELECT_TEXT_LINE_START = String.valueOf(lineNumberStart+1);

        VirtualFile virtualFile = e.getRequiredData(CommonDataKeys.PSI_FILE).getViewProvider().getVirtualFile();
        DataCenter.FILE_NAME = virtualFile.getName();
        DataCenter.FILE_PATH = virtualFile.getPath();

        new NoteDialog(project, editor, virtualFile).open();
    }
}
