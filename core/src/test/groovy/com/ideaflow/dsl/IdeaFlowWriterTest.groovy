package com.ideaflow.dsl

import com.ideaflow.model.entry.Conflict
import com.ideaflow.model.entry.ModelEntry
import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class IdeaFlowWriterTest extends Specification {

	static class DummyEvent extends ModelEntry {
	}


	private IdeaFlowWriter writer = new IdeaFlowWriter(new StringWriter())

    void testWrite_ShouldErrorIfObjectContainsAdditionalProperty() {
        given:
		Conflict conflict = createConflict()
		conflict.metaClass.newProperty = "value"

        when:
        writer.write(conflict)

        then:
        RuntimeException ex = thrown()
        assert ex.message.contains("Object Conflict declares unknown properties=[newProperty]")
	}

    void testWrite_ShouldErrorIfObjectMissingDeclaredProperty() {
        given:
		DummyEvent dummy = new DummyEvent()

        when:
        writer.writeItem('dummy', dummy, ['created', 'missingProperty'])

        then:
        RuntimeException ex = thrown()
        assert ex.message == "IdeaFlowWriter:write(DummyEvent) is configured to write out properties=[missingProperty] which are not declared in corresponding class."
	}

}
