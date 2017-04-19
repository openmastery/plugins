package org.openmastery.ideaflow.intellij.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;

@State(
		name = "org.openmastery.ideaflow.settings",
		storages = @Storage(id = "other", file = StoragePathMacros.APP_CONFIG + "/ideaflow.xml")
)
public class IdeaFlowSettings implements PersistentStateComponent<IdeaFlowSettings> {

	public static IdeaFlowSettings getInstance() {
		return ServiceManager.getService(IdeaFlowSettings.class);
	}

	private String apiUrl = "http://ideaflowdx.openmastery.org";
	private String apiKey;
	private int recentTaskListSize = 5;
	private String taskListJsonString;
	@Transient
	private IdeaFlowSettingsTaskManager taskManager = new IdeaFlowSettingsTaskManager(this);

	public String getTaskListJsonString() {
		return taskListJsonString;
	}

	public void setTaskListJsonString(String taskListJsonString) {
		this.taskListJsonString = taskListJsonString;
	}

	public IdeaFlowSettingsTaskManager getTaskManager() {
		return taskManager;
	}

	public String getApiUrl() {
		return apiUrl;
	}

	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public int getRecentTaskListSize() {
		return recentTaskListSize;
	}

	public void setRecentTaskListSize(int recentTaskListSize) {
		this.recentTaskListSize = recentTaskListSize;
	}

	@Override
	public IdeaFlowSettings getState() {
		return this;
	}

	@Override
	public void loadState(IdeaFlowSettings ideaFlowSettings) {
		XmlSerializerUtil.copyBean(ideaFlowSettings, this);
	}


}
