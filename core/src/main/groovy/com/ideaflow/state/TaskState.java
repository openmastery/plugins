package com.ideaflow.state;

import lombok.Data;

@Data
public class TaskState {
	Long id;
	String name;
	String description;

	boolean isBlocked;
	String blockComment;
	String blockTime;
}
