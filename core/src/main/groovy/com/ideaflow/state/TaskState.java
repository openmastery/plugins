package com.ideaflow.state;

import lombok.Data;

import java.util.List;

@Data
public class TaskState {
	Long id;
	String name;
	String description;

	boolean isBlocked;
	String blockComment;
	String blockTime;

	List<String> unresolvedWTFList;
}
