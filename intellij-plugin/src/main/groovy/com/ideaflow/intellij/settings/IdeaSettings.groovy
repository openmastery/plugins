package com.ideaflow.intellij.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException

import javax.swing.JComponent

class IdeaSettings implements Configurable {
    @Override
    String getDisplayName() {
        return "Idea"
    }

    @Override
    String getHelpTopic() {
        return null
    }

    @Override
    JComponent createComponent() {
        return new IdeaSettingsPanel().panel1;
    }

    @Override
    boolean isModified() {
        return false
    }

    @Override
    void apply() throws ConfigurationException {

    }

    @Override
    void reset() {

    }

    @Override
    void disposeUIResources() {

    }
}
