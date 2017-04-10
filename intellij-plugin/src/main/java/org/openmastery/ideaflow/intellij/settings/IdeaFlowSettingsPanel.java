package org.openmastery.ideaflow.intellij.settings;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class IdeaFlowSettingsPanel {

	public JPanel panel;
	public JTextField apiUrl;
	private JTextField apiKey;

	public String getApiKeyText() {
		return apiKey.getText();
	}

	public void setApiKeyText(String apiKeyText) {
		apiKey.setText(apiKeyText);
	}

	public String getApiUrlText() {
		return apiUrl.getText();
	}

	public void setApiUrlText(String apiKeyText) {
		apiUrl.setText(apiKeyText);
	}

}
