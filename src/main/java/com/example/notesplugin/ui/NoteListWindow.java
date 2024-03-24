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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

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
//        tbContent.getModel().addTableModelListener(new TableModelListener() {
//            @Override
//            public void tableChanged(TableModelEvent e) {
//                TableAutoSizer.sizeColumnsToFit(tbContent, 4);
//            }
//        });
        tbContent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Point p = e.getPoint();
                int viewRowIndex = tbContent.rowAtPoint(p);
                int row =  tbContent.convertRowIndexToModel(viewRowIndex);
                int column = tbContent.columnAtPoint(p);
                if (column == DataCenter.FILE_NAME_COLUMN) {
                    NoteData noteData = DataCenter.get(row);
                    openEditor(noteData.getPath(),  noteData.getLineNumberOffsetInt());
                }
                tbContent.setRowSelectionInterval(viewRowIndex, viewRowIndex);
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

        createRowFilter(tbContent);
    }

    public JTextField createRowFilter(JTable table) {
        RowSorter<? extends TableModel> rs = table.getRowSorter();
        if (rs == null) {
            table.setAutoCreateRowSorter(true);
            rs = table.getRowSorter();
        }
        TableRowSorter<? extends TableModel> rowSorter = (TableRowSorter<? extends TableModel>) rs;

        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        rowSorter.setSortKeys(sortKeys);

        tfTopic.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {update(e);}

            @Override
            public void removeUpdate(DocumentEvent e) {update(e);}

            @Override
            public void changedUpdate(DocumentEvent e) {update(e);}

            private void update(DocumentEvent e) {
                String text = tfTopic.getText();
                rowSorter.setRowFilter(text.trim().isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
            }
        });

        return tfTopic;
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
