package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel
import org.joda.time.DateTime
import spock.lang.Specification

class TestDSLTimelineSerializer extends Specification {

	private DSLTimelineSerializer serializer = new DSLTimelineSerializer()
	private File emptyFile = File.createTempFile('empty', '.txt')

	void setup() {
		emptyFile.createNewFile()
	}

	void cleanup() {
		emptyFile.delete()
	}

    void seralize_ShouldNotExplode() {
        given:
		IdeaFlowModel model = new IdeaFlowModel(new File('test'), DateTime.now())

        when:
		String serializedModel = serializer.serialize(model)

        then:
		assert serializedModel != null
	}

    void deseralize_ShouldNotExplode() {
        expect:
		serializer.deserialize(emptyFile)
	}
}
