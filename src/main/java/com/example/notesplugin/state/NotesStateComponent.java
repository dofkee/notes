package com.example.notesplugin.state;

import com.example.notesplugin.data.DataCenter;
import com.example.notesplugin.perf.Perf;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import org.jetbrains.annotations.NotNull;

@State(name = "NotesState", storages = {@Storage(value = "NotesState.xml")})
public class NotesStateComponent implements ProjectComponent, PersistentStateComponent<NotesState> {

    private final Project project;
    NotesState notesState;

    public NotesStateComponent(Project project) {
        this.project = project;
    }

    public static NotesStateComponent getInstance(Project project) {
        return project.getComponent(NotesStateComponent.class);
    }

    @Override
    public @NotNull NotesState getState() {
        if (notesState == null) {
            notesState = new NotesState();
        }
        return notesState;
    }

    @Override
    public void loadState(@NotNull NotesState notesState) {
        this.notesState = notesState;
//
//        for (NoteData note : notesState.getNotes()) {
//            DataCenter.add(note, project, null);
//        }
//

        Perf.perf( "loadState", () -> {
                    StartupManager.getInstance(getProject())
                            .runAfterOpened(() -> {
                                queueLater(new Task.Backgroundable(getProject(), "Synch", false) {
                                    @Override
                                    public void run(@NotNull final ProgressIndicator indicator) {
                                        Project project = getProject();
                                        if (project == null || project.isDisposed()) {
                                            return;
                                        }

                                        Perf.perf( "loadState_runnable" , () -> notesState.getNotes().forEach(note -> DataCenter.add(note, project, null)));
                                    }
                                });
                            });
                }
        );
//

    }

    private static void queueLater(final Task task) {
        final Application app = ApplicationManager.getApplication();
        if (task.isHeadless()) {
            // for headless tasks we need to ensure async execution.
            // Otherwise, calls to AntConfiguration.getInstance() from the task will cause SOE
            app.invokeLater(task::queue);
        }
        else {
            app.invokeLater(task::queue);
        }
    }

    public Project getProject() {
        return project;
    }
}
