package com.example.notesplugin.listener;

import com.example.notesplugin.data.ChangedFilesStorage;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerListener;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class VirtFileListener implements FileDocumentManagerListener {

    @Override
    public void beforeAnyDocumentSaving(@NotNull Document document, boolean explicit) {
        FileDocumentManagerListener.super.beforeAnyDocumentSaving(document, explicit);

        //System.out.println("VirtFileListener beforeAnyDocumentSaving document "  + document);
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        ChangedFilesStorage.getInstance().addChangedPathIfNotExist(file.getPath() , ProjectLocator.getInstance().guessProjectForFile(file));
    }

//    @Override
//    public void fileContentReloaded(@NotNull VirtualFile file, @NotNull Document document) {
//        FileDocumentManagerListener.super.fileContentReloaded(file, document);
//        //System.out.println("VirtFileListener fileContentReloaded document "  + document);
//    }
//
//    @Override
//    public void fileContentLoaded(@NotNull VirtualFile file, @NotNull Document document) {
//        FileDocumentManagerListener.super.fileContentLoaded(file, document);
//        //System.out.println("VirtFileListener fileContentLoaded document "  + document);
//    }
}
