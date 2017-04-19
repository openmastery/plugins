package org.openmastery.ideaflow.state;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskStateJsonMapper {

	private ObjectMapper jsonMapper;

	public TaskStateJsonMapper() {
		this.jsonMapper = new ObjectMapper();
		jsonMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}

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
