package com.example.notesplugin.ui;

import com.example.notesplugin.data.DataCenter;
import com.example.notesplugin.model.NoteData;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NoteListWindow implements Disposable {
    private JTextField tfTopic;
    private JTable tbContent;
    private JButton btnClear;
    private JButton btnClose;
    private JPanel contentPanel;
    private JPopupMenu popupMenu;

    private Project project;

    private void init() {
        tbContent.setModel(DataCenter.TABLE_MODEL);
        tbContent.setEnabled(false);
    }

    public NoteListWindow(Project project, ToolWindow toolWindow) {
        this.project = project;
        init();

        btnClear.addActionListener(e -> DataCenter.reset(project));
        btnClose.addActionListener(e -> toolWindow.hide(null));
        tbContent.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableAutoSizer.sizeColumnsToFit(tbContent, 4);
            }
        });
        tbContent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point p = e.getPoint();
                int row = tbContent.rowAtPoint(p);
                int column = tbContent.columnAtPoint(p);
                if (column == 2) {
                    NoteData noteData = DataCenter.get(row);
                    openEditor(noteData.getPath(),  noteData.getLineNumberOffsetInt());
                }
                tbContent.setRowSelectionInterval(row, row);
                if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                    createPopupMenu(row, project);
                    popupMenu.show(tbContent, e.getX(), e.getY());
                    return;
                }
                if (e.getClickCount() == 2) {
                    NoteData noteData = DataCenter.get(row);
                    NoteDialog dialog = new NoteDialog(project,row, noteData);
                    dialog.open();

                }
            }
        });
    }

    public Editor openEditor(String path, int offset) {
        VirtualFile virtualFile = null;
        String systemIndependentPath = FileUtil.toSystemIndependentName(path);
        String protocol = "file";
        if (systemIndependentPath.indexOf(".zip!") > 0 || systemIndependentPath.indexOf(".jar!") > 0) {
            protocol = JarFileSystem.PROTOCOL;
        }

        VirtualFilePointer myFilePointer = VirtualFilePointerManager.getInstance().create(VirtualFileManager.constructUrl(protocol, (systemIndependentPath)), this, null);
        virtualFile = myFilePointer.getFile();

        if (project.isDisposed()) {
            return null;
        }
        if (virtualFile == null || !virtualFile.isValid()) {
            return null;
        }

        return FileEditorManager.getInstance(project).openTextEditor(new OpenFileDescriptor(project, virtualFile, offset), true);
    }

    private void createPopupMenu(int row, Project project) {
        popupMenu = new JPopupMenu();
        JMenuItem delMenItem = new JMenuItem();
        delMenItem.setText("delete");
        delMenItem.addActionListener(evt -> DataCenter.delete(row, project));
        popupMenu.add(delMenItem);
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    @Override
    public void dispose() {

    }
}