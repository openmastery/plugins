package com.ideaflow.state;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(of = {"id", "name"})
public class TaskState {
	Long id;
	String name;
	String description;
	String project;

	boolean isBlocked;
	String blockComment;
	String blockTime;

	List<String> unresolvedWTFList = new ArrayList<String>();
}
