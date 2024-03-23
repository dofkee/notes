package com.example.notesplugin.listener;

import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import org.jetbrains.annotations.NotNull;

public class DocumentChangedListener implements DocumentListener {

    @Override
    public void documentChanged(@NotNull DocumentEvent event) {
//        if(event.getOldLength() == event.getNewLength() ) {
//            return;
//        }
//
//        Document document = event.getDocument();
//        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
//        if (file != null) {
//            System.out.println("Document changed for " + file.getCanonicalPath() ); // TODO DA> this is wrong as it returns only file name. not full path
//            ChangedFilesStorage.getInstance().addChangedPathIfNotExist(file.getCanonicalPath(), ProjectLocator.getInstance().guessProjectForFile(file));
//        }
    }


}
