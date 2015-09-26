package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Task
import org.joda.time.DateTime
import spock.lang.Specification

class TestDSLTimelineSerializer extends Specification {

	private DSLTimelineSerializer serializer = new DSLTimelineSerializer()

    void seralize_ShouldNotExplode() {
        given:
		IdeaFlowModel model = new IdeaFlowModel(new Task(taskId: 'test'), DateTime.now())

        when:
		String serializedModel = serializer.serialize(model)

        then:
		assert serializedModel != null
	}

    void deseralize_ShouldNotExplode() {
        expect:
		serializer.deserialize(new Task(taskId: 'test'), "")
	}

}
