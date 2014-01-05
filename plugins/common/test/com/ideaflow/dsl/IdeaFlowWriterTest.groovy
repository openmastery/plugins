package com.ideaflow.dsl

import com.ideaflow.model.Conflict
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.StateChangeType
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class IdeaFlowWriterTest extends GroovyTestCase {

	static class DummyEvent extends ModelEntity {
	}


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
		writer.write(createEditorActivity(FILE, NOW))

		List<String> lines = readDslLines()

		assert lines.size() == 1
		assert lines[0] == "interval (created: '${toDateString(NOW)}', name: '''${FILE}''', duration: 5, )"
	}

	void testWrite_ShouldWriteDslStateChange() {
		writer.write(createStateChange(StateChangeType.startIdeaFlowRecording, NOW))

		List<String> lines = readDslLines()

		assert lines.size() == 1
		assert lines[0] == "stateChange (created: '${toDateString(NOW)}', type: 'startIdeaFlowRecording', )"
	}

	void testWrite_ShouldWriteDslNote() {
		writer.write(createNote('happy note', NOW))

		List<String> lines = readDslLines()

		assert lines.size() == 1
		assert lines[0] == "note (created: '${toDateString(NOW)}', comment: '''happy note''', )"
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

	void testWrite_ShouldErrorIfObjectContainsAdditionalProperty() {
		Conflict conflict = createConflict(NOW)
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
