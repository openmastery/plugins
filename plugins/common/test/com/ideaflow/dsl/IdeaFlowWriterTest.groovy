package com.ideaflow.dsl

import test.support.FixtureSupport

@Mixin(FixtureSupport)
class IdeaFlowWriterTest extends GroovyTestCase {

	private StringWriter stringWriter
	private IdeaFlowWriter writer

	void setUp() {
		stringWriter = new StringWriter()
		writer = new IdeaFlowWriter(stringWriter)
	}

	private String toDateString(long millis) {
		Date date = new Date(millis)
		writer.dateFormat.format(date)
	}

	void testWriteInterval_ShouldWriteDslInterval() {
		writer.writeInterval(createInterval(FILE, NOW))

		List<String> lines = stringWriter.toString().readLines()

		assert lines.size() == 1
		assert lines[0] == "interval (created: '${toDateString(NOW)}', name: '${FILE}', duration: 5, )"
	}

	void testWriteTimelineEvent_ShouldWriteDslTimelineEvents() {
		writer.writeTimelineEvent(createEvent('happy note', NOW))

		List<String> lines = stringWriter.toString().readLines()

		assert lines.size() == 1
		assert lines[0] == "event (created: '${toDateString(NOW)}', type: 'note', comment: '''happy note''', )"
	}

}
