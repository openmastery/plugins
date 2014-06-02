package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test


class TestDSLTimelineSerializer {
	private DSLTimelineSerializer serializer
	File emptyFile

	@Before
	void setUp() {
		serializer = new DSLTimelineSerializer()
		emptyFile = new File('empty.txt')
		emptyFile.createNewFile()
	}

	@Test
	void seralize_ShouldNotExplode() {
		IdeaFlowModel model = new IdeaFlowModel(new File('test'), DateTime.now())
		String serializedModel = serializer.serialize(model)
		assert serializedModel != null
	}

	@Test
	void deseralize_ShouldNotExplode() {
		serializer.deserialize(emptyFile)
	}
}
