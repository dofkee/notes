package com.example.notesplugin.listener;

import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VListener implements AsyncFileListener {

    @Nullable
    @Override
    public ChangeApplier prepareChange(@NotNull List<? extends @NotNull VFileEvent> list) {
        return new ChangeApplier() {
            @Override
            public void beforeVfsChange() {
                ChangeApplier.super.beforeVfsChange();


            }

            @Override
            public void afterVfsChange() {
                ChangeApplier.super.afterVfsChange();
                for (VFileEvent vFileEvent : list) {
                    vFileEvent.getFile();
                }

            }
        };
    }
}
