package org.openmastery.ideaflow.intellij.settings

import com.intellij.ide.util.PropertiesComponent

class IdeaFlowSettingsStore {

	private static final String API_URL = "OpenMastery.IfmPublisherUrl"
	private static final String API_KEY = "OpenMastery.ApiKey";

	public static IdeaFlowSettingsStore get() {
		return new IdeaFlowSettingsStore()
	}

	private IdeaFlowSettingsStore() {}

	private PropertiesComponent getProps() {
		PropertiesComponent.getInstance()
	}

	String getApiKey() {
		props.getValue(API_KEY)
	}

	void saveApiKey(String apiKey) {
		props.setValue(API_KEY, apiKey)
	}

	String getApiUrl() {
		// TODO: set to heroku url
		props.getValue(API_URL, "http://localhost:8080")
	}

	void saveApiUrl(String apiUrl) {
		props.setValue(API_URL, apiUrl)
	}

}
