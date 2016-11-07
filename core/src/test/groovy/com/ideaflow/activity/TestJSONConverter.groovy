package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewEditorActivity
import spock.lang.Specification

class TestJSONConverter extends Specification {

	JSONConverter converter = new JSONConverter()

	def "toJSON/fromJSON SHOULD serialize/deserialize API types"() {
		given:
		NewEditorActivity editorActivity = NewEditorActivity.builder()
				.taskId(1)
				.endTime(LocalDateTime.now())
				.durationInSeconds(5)
				.filePath("hello.txt")
				.isModified(true)
				.build();

		when:
		String json = converter.toJSON(editorActivity)
		NewEditorActivity deserializedActivity = (NewEditorActivity) converter.fromJSON(json)

		then:
		println json
		assert deserializedActivity != null
	}

	def "toJSON SHOULD not explode if serialization type is not in the type map"() {
		given:
		Object o = new Object()

		when:
		converter.toJSON(o)

		then:
		thrown(JSONConverter.UnsupportedObjectType.class)
	}

}
