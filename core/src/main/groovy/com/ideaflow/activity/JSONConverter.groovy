package com.ideaflow.activity;

import com.bancvue.rest.config.ObjectMapperContextResolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewModificationActivity;

import java.io.IOException;

public class JSONConverter {

	Map<String, Class> idToClassMap = [
		"EditorActivity" : NewEditorActivity.class,
			"ExecutionActivity" : NewExecutionActivity.class,
			"ExternalActivity" : NewExternalActivity.class,
			"ModificationActivity" : NewModificationActivity.class
			]

	Map<Class, String> classToIdMap

	ObjectMapper mapper;

	JSONConverter() {
		mapper = new ObjectMapperContextResolver().getContext(null);
		classToIdMap = idToClassMap.collectEntries { k, v -> [v, k] }
	}

	String toJSON(Object object) throws JsonProcessingException {
		String typeName = classToIdMap.get(object.class)
		if (typeName == null) {
			throw new UnsupportedObjectType("Unable to find typeName for "+object.getClass().name)
		}
		return typeName + "=" + mapper.writeValueAsString(object);
	}

	Object fromJSON(String jsonInString) throws IOException {
		String [] jsonSplit = jsonInString.split("=")
		String typeName = jsonSplit[0]
		String jsonContent = jsonSplit[1]
		Class clazz = idToClassMap.get(typeName)
		return mapper.readValue(jsonContent, clazz);
	}

	static class UnsupportedObjectType extends RuntimeException {

		UnsupportedObjectType(String message) {
			super(message)
		}
	}
}
