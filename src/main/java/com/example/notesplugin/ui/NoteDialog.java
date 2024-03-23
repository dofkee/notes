
package com.example.notesplugin.ui;

import com.example.notesplugin.data.DataCenter;
import com.example.notesplugin.model.NoteData;
import com.example.notesplugin.utils.PluginUtil;
import com.example.notesplugin.utils.TextUtils;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.EditorTextField;
import one.util.streamex.StreamEx;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class NoteDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea taCode;
    private EditorTextField taCode2;
    private JTextArea taNote;
    private JLabel lbFileName;
    private JLabel lineNumberStart;
    private JPanel note_panel;
    private JPanel code_panel;
    private boolean update = false;
    private Integer editIndex;
    private String  noteUUID;

    private final Project project;
    private Editor editor;

    public NoteDialog(Project project, Editor editor, VirtualFile virtualFile) {
//        super(project);
        this.project = project;
        this.editor = editor;
        NoteData noteData = new NoteData(PluginUtil.generateUUID(), "Note", DataCenter.SELECT_TEXT, null, PluginUtil.calculateMD5(virtualFile));
        init(noteData, project , virtualFile);
    }

    public NoteDialog(Project project, Integer index, NoteData noteData) {
//        super(project);
        this.project = project;
        this.noteUUID = noteData.getUuid();
        init(noteData, project, null);
        this.editIndex = index;
        update = true;
    }

    private void init(NoteData noteData, Project project, @Nullable VirtualFile virtualFile) {
        DataCenter.selectNote(noteData, project);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        lbFileName.setText(noteData.getPath());
        taCode.setText(noteData.getContent());
        taNote.setText(noteData.getNoteText());
        taNote.setRows(1);
        lineNumberStart.setText(noteData.getLineNumberOffset());

        buttonOK.addActionListener(e -> onOK(virtualFile));
        buttonCancel.addActionListener(e -> onCancel());

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK(@Nullable VirtualFile virtualFile) {
        if (update) {
            updateNote(editIndex, virtualFile);
        } else {
            addNewNote(virtualFile);
        }
    }

    private void updateNote(Integer index, @Nullable VirtualFile virtualFile) {
        String noteText = taNote.getText();
        String code = taCode.getText();
        String fileType = DataCenter.FILE_NAME.substring(DataCenter.FILE_NAME.lastIndexOf(".") + 1);
        NoteData noteData = new NoteData(noteUUID, noteText, code, fileType, PluginUtil.calculateMD5(virtualFile));
        DataCenter.update(index, noteData, project, noteUUID);
        dispose();
    }

    private void addNewNote(VirtualFile virtualFile) {
        String noteText = taNote.getText();
        String code = taCode.getText();
        String fileType = DataCenter.FILE_NAME.substring(DataCenter.FILE_NAME.lastIndexOf(".") + 1);
        List<Caret> carets = editor.getCaretModel().getAllCarets();
        if (carets.isEmpty()) {
            dispose();
            return;
        }

        int offset = StreamEx.of(carets).mapToInt(Caret::getVisualLineEnd).toArray()[0];
        DataCenter.SELECT_TEXT_OFFSET = String.valueOf(offset-1);

        NoteData noteData = new NoteData(PluginUtil.generateUUID() , noteText, code, fileType, PluginUtil.calculateMD5(virtualFile));
        List<Integer> offsets = TextUtils.findOffsets(PluginUtil.getDocumentFromVF(virtualFile).getText(), noteData.getContent());
        List<Integer> lineNumbers = PluginUtil.offsetsToLines(offsets, editor);
        //System.out.println(lineNumbers);
        noteData.setLineNumber(String.valueOf(editor.offsetToVisualLine(offset, true)  ));
        noteData.setNumberOfDuplicateOffsetsFound(String.valueOf(lineNumbers));
        DataCenter.add(noteData, project, virtualFile);

//        PsiManager psiManager = PsiManager.getInstance(project);

//        PsiFile psiFile = psiManager.findFile(virtualFile);
        // Using the helper to find the PSI element at the given offset
//        PsiElement element = PsiTreeUtil.findElementOfClassAtOffset(psiFile, offset, PsiElement.class, false);
//        CustomEditorListener.Companion.getEditorManager(editor).updateCell(element, null, true);
        dispose();
    }


    private void onCancel() {
        dispose();
    }

    public void open() {
        pack();
        setTitle("Mark");
//        show();
        setMinimumSize(new Dimension(800, 200));
        setLocationRelativeTo(WindowManager.getInstance().getFrame(this.project));
        try {
            setVisible(true);
        } catch (Exception e ) {

        }
    }

//    @Override
//    protected @Nullable JComponent createCenterPanel() {
//        taCode2 = new EditorTextField("rrr");
//        taCode2.setText("ASDASD");
//        BorderLayoutPanel rootPanel = JBUI.Panels.simplePanel(UIUtil.DEFAULT_HGAP, UIUtil.DEFAULT_VGAP);
////        rootPanel.addToTop(myRootLabel);
//        rootPanel.addToCenter(taCode2.getComponent());
////        rootPanel.addToBottom(taCode2);
//        return rootPanel;
//    }
}
