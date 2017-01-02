package com.ideaflow.state;

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
	private Long id;
	private String name;
	private String description;
	private String project;

	private boolean isBlocked;
	private String blockComment;
	private String blockTime;

	private List<String> unresolvedPainList = new ArrayList<String>();

	@Deprecated
	public void setUnresolvedWTFList(List<String> unresolvedWTFList) {
		unresolvedPainList = unresolvedWTFList;
	}

	public static TaskState create(Task task) {
		return TaskState.builder()
				.id(task.getId())
				.name(task.getName())
				.description(task.getDescription())
				.project(task.getProject())
				.unresolvedPainList(new ArrayList<String>())
				.build();
	}

	// TODO: switch from groovy JsonOutput to jackson where it's possible to ignore properties and not have to do
	// something stupid like this
	public static String getQualifiedName(TaskState task) {
		return task.project != null ? task.project + ":" + task.name : task.name;
	}

}
