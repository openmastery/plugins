package org.openmastery.ideaflow.intellij.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent

import javax.swing.JComponent

class IdeaFlowSettings implements Configurable {

	private IdeaFlowSettingsStore storage = new IdeaFlowSettingsStore()
	private IdeaFlowSettingsPanel panel

	@Override
	String getDisplayName() {
		return "Idea Flow"
	}

	@Override
	String getHelpTopic() {
		return null
	}

	@Override
	JComponent createComponent() {
		if (panel == null) {
			panel = new IdeaFlowSettingsPanel()
		}

		reset()
		return panel.panel
	}

	@Override
	boolean isModified() {
		return panel == null ||
				panel.apiKeyText != storage.apiKey ||
				panel.apiUrlText != storage.apiUrl
	}

	@Override
	void apply() throws ConfigurationException {
		if (panel != null) {
			storage.saveApiUrl(panel.apiUrlText)
			storage.saveApiKey(panel.apiKeyText)

			if (isModified()) {
				// TODO: where does this go?  seems like at a minimum, should fire a listener which then handles
				// this elsewhere, likely IdeaFlowApplicationComponent...
				IdeaFlowApplicationComponent.getApplicationComponent().initIfmController(storage)
			}
		}
	}

	@Override
	void reset() {
		if (panel != null) {
			panel.apiKeyText = storage.apiKey
			panel.apiUrlText = storage.apiUrl
		}
	}

	@Override
	void disposeUIResources() {
		panel = null
	}
}
