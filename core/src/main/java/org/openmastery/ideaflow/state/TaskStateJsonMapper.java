package org.openmastery.ideaflow.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskStateJsonMapper {

	private ObjectMapper jsonMapper = new ObjectMapper();

	public String toJson(List<TaskState> taskStateList) throws JsonProcessingException {
		return jsonMapper.writeValueAsString(taskStateList);
	}

	public List<TaskState> toList(String jsonString) throws IOException {
		if (jsonString == null) {
			return new ArrayList<TaskState>();
		}

		TaskState[] taskStates = jsonMapper.readValue(jsonString, TaskState[].class);
		return Arrays.asList(taskStates);
	}

}
