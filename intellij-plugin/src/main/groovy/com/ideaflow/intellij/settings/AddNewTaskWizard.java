package com.ideaflow.intellij.settings;

import com.ideaflow.model.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class AddNewTaskWizard extends DialogWrapper {

    private IdeaSettingsService storage = new IdeaSettingsService();
    private IdeaSettingsDialog wizard;

    protected AddNewTaskWizard(@Nullable Project project) {
        super(project);
        init();
    }

    @Override
    protected JComponent createCenterPanel() {

        if (wizard == null) {
            Task data = storage.loadActiveTask();

            //Clear out task id
            data.setTaskId("");

            wizard = new IdeaSettingsDialog(data);
        }
        return wizard.panel;
    }

    public boolean showAndSaveSettings() {

        show();

        if (getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            storage.saveActiveTask(wizard.toTask());
        }

        return getExitCode() == DialogWrapper.OK_EXIT_CODE;
    }

    public Task getTask() {

        return storage.loadActiveTask();
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return wizard.taskId;
    }

    @Override
    protected ValidationInfo doValidate() {
        return wizard.isValid() ? null : new ValidationInfo("Please fill out the form");
    }
}
