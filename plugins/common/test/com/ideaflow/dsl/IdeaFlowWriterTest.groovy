package com.ideaflow.dsl

import com.ideaflow.model.Conflict
import com.ideaflow.model.ModelEntity
import org.joda.time.DateTime
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class IdeaFlowWriterTest extends GroovyTestCase {

	static class DummyEvent extends ModelEntity {
	}


	private IdeaFlowWriter writer

	void setUp() {
		writer = new IdeaFlowWriter(new StringWriter())
	}

	void testWrite_ShouldErrorIfObjectContainsAdditionalProperty() {
		Conflict conflict = createConflict()
		conflict.metaClass.newProperty = "value"

		try {
			writer.write(conflict)
			fail()
		} catch (RuntimeException ex) {
			assert ex.message.contains("Object Conflict declares unknown properties=[newProperty]")
		}
	}

	void testWrite_ShouldErrorIfObjectMissingDeclaredProperty() {
		DummyEvent dummy = new DummyEvent()

		try {
			writer.writeItem('dummy', dummy, ['created', 'missingProperty'])
			fail()
		} catch (RuntimeException ex) {
			assert ex.message == "IdeaFlowWriter:write(DummyEvent) is configured to write out properties=[missingProperty] which are not declared in corresponding class."
		}
	}

}
