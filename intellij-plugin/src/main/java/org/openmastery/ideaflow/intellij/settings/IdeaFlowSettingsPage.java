package org.openmastery.ideaflow.intellij.settings;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;

import javax.swing.JComponent;
import java.util.Objects;

public class IdeaFlowSettingsPage implements Configurable {

	private IdeaFlowSettingsPanel panel;

	private IdeaFlowSettings getSettings() {
		return IdeaFlowSettings.getInstance();
	}

	@Override
	public String getDisplayName() {
		return "Idea Flow";
	}

	@Override
	public String getHelpTopic() {
		return null;
	}

	@Override
	public JComponent createComponent() {
		if (panel == null) {
			panel = new IdeaFlowSettingsPanel();
		}

		reset();
		return panel.panel;
	}

	@Override
	public boolean isModified() {
		return panel == null ||
				(Objects.equals(panel.getApiKeyText(), getSettings().getApiKey()) == false) ||
				(Objects.equals(panel.getApiUrlText(), getSettings().getApiUrl()) == false);
	}

	@Override
	public void apply() throws ConfigurationException {
		if (panel != null) {
			getSettings().setApiUrl(panel.getApiUrlText());
			getSettings().setApiKey(panel.getApiKeyText());

			// TODO: where does this go?  seems like at a minimum, should fire a listener which then handles
			// this elsewhere, likely IdeaFlowApplicationComponent...
			IdeaFlowApplicationComponent.getApplicationComponent().initIfmController(getSettings());
		}
	}

	@Override
	public void reset() {
		if (panel != null) {
			panel.setApiUrlText(getSettings().getApiUrl());
			panel.setApiKeyText(getSettings().getApiKey());
		}
	}

	@Override
	public void disposeUIResources() {
		panel = null;
	}
}
