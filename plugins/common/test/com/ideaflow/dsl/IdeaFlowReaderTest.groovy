package com.ideaflow.dsl

import com.ideaflow.model.Event
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Interval
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
		Event event = createEvent("it's a happy note!", NOW)

		writer.writeInitialization(createDate)
		writer.writeInterval(interval)
		writer.writeTimelineEvent(event)
		IdeaFlowModel model = new IdeaFlowReader().readModel(stringWriter.toString())

		assert model.created == createDate
		assert model.eventList == [event]
		assert model.intervalList == [interval]
	}

}
