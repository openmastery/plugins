package com.ideaflow.dsl

import com.ideaflow.model.*
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class IdeaFlowReaderTest extends GroovyTestCase {

	private StringWriter stringWriter
	private IdeaFlowWriter writer

	void setUp() {
		stringWriter = new StringWriter()
		writer = new IdeaFlowWriter(stringWriter)
	}

	void testReadModel_ShouldReadContentWrittenByWriter() {
		Date createDate = new Date(NOW)
		Interval interval = createInterval(FILE, NOW)
		GenericEvent event = createNote("it's a happy note!", NOW)
		Conflict conflict = createConflict(NOW)
		Resolution resolution = createResolution(NOW)

		writer.writeInitialization(createDate)
		writer.write(interval)
		writer.write(event)
		writer.write(conflict)
		writer.write(resolution)
		IdeaFlowModel model = new IdeaFlowReader().readModel(stringWriter.toString())

		assert model.created == createDate
		assert model.itemList == [interval, event, conflict, resolution]
	}

}
