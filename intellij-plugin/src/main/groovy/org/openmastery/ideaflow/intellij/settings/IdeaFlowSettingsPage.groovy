package org.openmastery.ideaflow.intellij.settings

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.ConfigurationException
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent

import javax.swing.JComponent

class IdeaFlowSettingsPage implements Configurable {

	private IdeaFlowSettingsPanel panel

	private IdeaFlowSettings getSettings() {
		IdeaFlowSettings.instance
	}

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
				panel.apiKeyText != settings.apiKey ||
				panel.apiUrlText != settings.apiUrl
	}

	@Override
	void apply() throws ConfigurationException {
		if (panel != null) {
			settings.setApiUrl(panel.apiUrlText)
			settings.setApiKey(panel.apiKeyText)

			// TODO: where does this go?  seems like at a minimum, should fire a listener which then handles
			// this elsewhere, likely IdeaFlowApplicationComponent...
			IdeaFlowApplicationComponent.getApplicationComponent().initIfmController(settings)
		}
	}

	@Override
	void reset() {
		if (panel != null) {
			panel.apiUrlText = settings.apiUrl
			panel.apiKeyText = settings.apiKey
		}
	}

	@Override
	void disposeUIResources() {
		panel = null
	}
}
