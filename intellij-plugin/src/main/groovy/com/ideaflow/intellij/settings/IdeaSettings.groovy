package com.ideaflow.intellij.settings

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException

import javax.swing.*

class IdeaSettings implements Configurable {

    IdeaSettingsService storage = ServiceManager.getService(IdeaSettingsService.class)
    IdeaSettingsDialog dialog

    @Override
    String getDisplayName() {
        return "IdeaFlow"
    }

    @Override
    String getHelpTopic() {
        return null
    }

    @Override
    JComponent createComponent() {
        if (dialog == null) {
            dialog = new IdeaSettingsDialog()
        }
        reset()
        return dialog.panel
    }

    @Override
    boolean isModified() {
        return dialog == null ||
                dialog.user == null || storage.user != dialog.user.text.trim() ||
                dialog.project == null || storage.project != dialog.project.text.trim() ||
                dialog.taskId == null || storage.taskId != dialog.taskId.text.trim() ||
                dialog.urlOverrideValue == null || storage.urlOverrideValue != dialog.urlOverrideValue.text.trim()
    }

    @Override
    void apply() throws ConfigurationException {
        if (dialog != null) {
            storage.taskId = dialog.taskId.text.trim()
            storage.project = dialog.project.text.trim()
            storage.user = dialog.user.text.trim()
            storage.urlValue = dialog.urlValue.text.trim()
            storage.urlOverrideValue = dialog.urlOverrideValue.text.trim()
        }
    }

    @Override
    void reset() {
        if (dialog != null) {
            dialog.taskId.text = storage.taskId
            dialog.project.text = storage.project
            dialog.user.text = storage.user
            dialog.urlValue.text = storage.urlValue
            dialog.urlOverrideValue.text = storage.urlOverrideValue
        }
    }

    @Override
    void disposeUIResources() {
        dialog = null
    }
}
