package com.ideaflow.intellij.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException

import javax.swing.*

class IdeaSettings implements Configurable {

    IdeaSettingsService storage = new IdeaSettingsService()

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
        return dialog == null || dialog.isDifferent(storage.loadActiveTask())
    }

    @Override
    void apply() throws ConfigurationException {
        if (dialog != null) {
            storage.saveActiveTask(dialog.toTask())
        }
    }

    @Override
    void reset() {
        if (dialog != null) {
            dialog.load(storage.loadActiveTask())
        }
    }

    @Override
    void disposeUIResources() {
        dialog = null
    }
}
