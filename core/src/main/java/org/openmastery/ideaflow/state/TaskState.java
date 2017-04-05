package org.openmastery.ideaflow.state;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.openmastery.publisher.api.task.Task;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id", "name"})
public class TaskState {

	private static final String PAIN_PREFIX = "WTF?: ";
	private static final String AWESOME_PREFIX = "YAY!: ";

	private Long id;
	private String name;
	private String description;
	private String project;

	private boolean isBlocked;
	private String blockComment;
	private String blockTime;

	private List<String> troubleshootingEventList = new ArrayList<String>();

	public int getUnresolvedPainCount() {
		int unresolvedPainCount = 0;
		for (String troubleshootingEvent : troubleshootingEventList) {
			if (troubleshootingEvent.startsWith(PAIN_PREFIX)) {
				unresolvedPainCount++;
			}
		}
		return unresolvedPainCount;
	}

	public void clearTroubleshootingEventList() {
		troubleshootingEventList.clear();
	}

	public void addPainfulTroubleshootingEvent(String event) {
		addTroubleshootingEvent(PAIN_PREFIX + event);
	}

	public void addAwesomeTroubleshootingEvent(String event) {
		addTroubleshootingEvent(AWESOME_PREFIX + event);
	}

	private void addTroubleshootingEvent(String event) {
		if (troubleshootingEventList.size() > 20) {
			troubleshootingEventList.remove(0);
		}
		troubleshootingEventList.add(event);
	}


	public static TaskState create(Task task) {
		return TaskState.builder()
				.id(task.getId())
				.name(task.getName())
				.description(task.getDescription())
				.project(task.getProject())
				.troubleshootingEventList(new ArrayList<String>())
				.build();
	}



	// TODO: see org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
	// need a better way of handling serialization; currently, this is handled by IdeaFlowSettingsTaskManager, should
	// probably be moved here... also, the following methods are deprecated b/c the property has been renamed

	@Deprecated
	public void setUnresolvedWTFList(List<String> unresolvedWTFList) {
		troubleshootingEventList = unresolvedWTFList;
	}

	@Deprecated
	public void setUnresolvedPainList(List<String> unresolvedWTFList) {
		troubleshootingEventList = unresolvedWTFList;
	}

	@Deprecated
	public void setUnresolvedPainCount(int unresolvedPainCount) {}


	// TODO: switch from groovy JsonOutput to jackson where it's possible to ignore properties and not have to do
	// something stupid like this
	public static String getQualifiedName(TaskState task) {
		return task.project != null ? task.project + ":" + task.name : task.name;
	}

}
