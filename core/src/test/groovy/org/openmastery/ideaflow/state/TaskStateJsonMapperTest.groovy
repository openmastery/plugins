package org.openmastery.ideaflow.state;

import spock.lang.Specification
import spock.lang.Unroll;

class TaskStateJsonMapperTest extends Specification {

	TaskStateJsonMapper mapper = new TaskStateJsonMapper()

	def "should serialize TaskState to json and back"() {
		given:
		TaskState taskState = TaskState.builder()
				.id(1)
				.name("some task")
				.description("awesome description")
				.troubleshootingEventList(["event1", "event2"])
				.build()

		when:
		String json = mapper.toJson([taskState])

		then:
		assert json == """[{"id":1,"name":"some task","description":"awesome description","project":null,"troubleshootingEventList":["event1","event2"]}]"""

		when:
		List<TaskState> taskStateList = mapper.toList(json)

		then:
		assert taskStateList.size() == 1
		assert taskStateList[0] == taskState
	}

	@Unroll
	def "toList should deserialize deprecated troubleshooting list properties"() {
		given:
		String json = """[{"id":1,"name":"some task","description":"awesome description","project":null,"${deprecatedTroubleshootingListPropertyName}":["event1","event2"]}]"""

		expect:
		assert mapper.toList(json)[0].troubleshootingEventList == ["event1", "event2"]

		where:
		deprecatedTroubleshootingListPropertyName << ["unresolvedWTFList", "unresolvedPainList"]
	}

	def "toList should return empty list if null passed"() {
		expect:
		assert mapper.toList(null) == []
	}

}