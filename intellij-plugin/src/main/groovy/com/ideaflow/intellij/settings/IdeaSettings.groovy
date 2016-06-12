package com.ideaflow.intellij.settings

import com.ideaflow.intellij.action.meta.CreateTaskDialog
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import javax.swing.JComponent

class IdeaSettings implements Configurable {

	IdeaSettingsService storage = new IdeaSettingsService()

	CreateTaskDialog dialog

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
			dialog = new CreateTaskDialog()
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
