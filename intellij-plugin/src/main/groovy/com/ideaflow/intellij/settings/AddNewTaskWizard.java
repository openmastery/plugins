package com.ideaflow.intellij.settings;

import com.ideaflow.intellij.settings.IdeaSettingsService.IdeaSettingsData;
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
            IdeaSettingsData data = storage.load();

            //Clear out task id
            data.setTaskId("");

            wizard = new IdeaSettingsDialog(data);
        }
        return wizard.panel;
    }

    public void showAndSaveSettings() {
        show();

        if (getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            storage.save(wizard.toData());
        }
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
