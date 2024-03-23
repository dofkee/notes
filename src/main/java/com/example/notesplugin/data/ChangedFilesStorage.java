package com.example.notesplugin.data;

import com.example.notesplugin.Debug;
import com.example.notesplugin.perf.Response;
import com.example.notesplugin.utils.PluginUtil;
import com.google.common.collect.Sets;
import com.intellij.concurrency.ConcurrentCollectionFactory;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.progress.PerformInBackgroundOption;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.progress.impl.CoreProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.rd.util.AtomicReference;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import static com.example.notesplugin.utils.Perf.perf;

public class ChangedFilesStorage {

    private Long fileLimit = 10L;
    private Long timeLimitInSeconds = 5L;

    private static ChangedFilesStorage INSTANCE = new ChangedFilesStorage();
    private AtomicReference<Date> lastSync = new AtomicReference<>(new Date());

    private Set<String> changedPaths = ConcurrentCollectionFactory.createConcurrentSet();
    private ConcurrentLinkedQueue<String> linkedQueue = new ConcurrentLinkedQueue<>();

    public static ChangedFilesStorage getInstance() {
        return INSTANCE;
    }

    public void addChangedPathIfNotExist(@NotNull String path, @NotNull Project project) {
        changedPaths.add(path);
        //System.out.println(Thread.currentThread() + " [ " +  Thread.currentThread().getStackTrace()[2].getMethodName() + " ] changedPaths add " + path + " project " + project);
        long duration = Duration.between(lastSync.get().toInstant(), new Date().toInstant()).toSeconds();
        boolean isProgressRunning = Optional.ofNullable(CoreProgressManager.getInstance().getProgressIndicator()).map(ProgressIndicator::isRunning).orElse(false);
        boolean byTime = duration > timeLimitInSeconds;
        boolean byFileLimit = changedPaths.size() > fileLimit;
        if((byTime || byFileLimit) && (!isProgressRunning || Debug.debug)) {
            Set<String> pathsToSynch = Sets.newHashSet(changedPaths);
            changedPaths.clear();

            (new Task.Backgroundable(project, "Synch Notes", true, PerformInBackgroundOption.ALWAYS_BACKGROUND) {

                @Override
                public void run(@NotNull ProgressIndicator progressIndicator) {
                    for (String path : pathsToSynch) {
                        System.out.println("Synchronize file " + path);
                        String url = PluginUtil.getFileUrl(path);

                        Document document = perf( "getDocumentFromUrl" ,
                                () -> {
                                    Document doc = PluginUtil.getDocumentFromUrl(url);
                                    return new Response<>(" ", doc);
                                }
                        );

                        if (document == null) {
                            continue;
                        }

                        List<Editor> editors = Arrays.stream(EditorFactory.getInstance().getEditors(document))
                                .filter(e -> e.getInlayModel().hasAfterLineEndElements())
                                .collect(Collectors.toList());

                        for (Editor editor : editors) {

                            VirtualFile virtualFile = editor.getVirtualFile();
                            if (virtualFile == null) {
                                continue;
                            }
                            String filePath = Optional.of(virtualFile).map(VirtualFile::getPath).orElse(null);
                            String checksum = PluginUtil.calculateMD5(virtualFile);
                            InlaysStorage.getInstance().synchronize(filePath, editor, checksum, project);
                            break;
                        }
                    }
                    lastSync.getAndSet(new Date());
                    DataCenter.refreshTable(project);
                }
            }).queue();
        }
    }
}
