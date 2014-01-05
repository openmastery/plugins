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

	private List<String> readDslLines() {
		stringWriter.toString().readLines()
	}

	void testWrite_ShouldWriteDslInterval() {
		writer.write(createInterval(FILE, NOW))

		List<String> lines = readDslLines()

		assert lines.size() == 1
		assert lines[0] == "interval (created: '${toDateString(NOW)}', name: '${FILE}', duration: 5, )"
	}

	void testWrite_ShouldWriteDslGenericEvent() {
		writer.write(createNote('happy note', NOW))

		List<String> lines = readDslLines()

		assert lines.size() == 1
		assert lines[0] == "event (created: '${toDateString(NOW)}', type: 'note', comment: '''happy note''', )"
	}

	void testWrite_ShouldWriteDslConflict() {
		writer.write(createConflict(NOW))

		List<String> lines = readDslLines()

		assert lines.size() == 1
		assert lines[0] == "conflict (created: '${toDateString(NOW)}', question: '''question''', )"
	}

	void testWrite_ShouldWriteDslResolution() {
		writer.write(createResolution(NOW))

		List<String> lines = readDslLines()

		assert lines.size() == 1
		assert lines[0] == "resolution (created: '${toDateString(NOW)}', answer: '''answer''', )"
	}

}
