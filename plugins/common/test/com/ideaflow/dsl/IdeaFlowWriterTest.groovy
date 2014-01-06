package com.ideaflow.dsl

import com.ideaflow.model.BandType
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

	private String readSingleDslLine() {
		List<String> lines = stringWriter.toString().readLines()
		assert lines.size() == 1
		lines[0]
	}

	void testWrite_ShouldWriteDslEditorActivity() {
		writer.write(createEditorActivity(FILE, NOW))

		String line = readSingleDslLine()

		assert line == "editorActivity (created: '${toDateString(NOW)}', name: '''${FILE}''', duration: 5, )"
	}

	void testWrite_ShouldWriteDslStateChange() {
		writer.write(createStateChange(StateChangeType.startIdeaFlowRecording, NOW))

		String line = readSingleDslLine()

		assert line == "stateChange (created: '${toDateString(NOW)}', type: 'startIdeaFlowRecording', )"
	}

	void testWrite_ShouldWriteDslNote() {
		writer.write(createNote('happy note', NOW))

		String line = readSingleDslLine()

		assert line == "note (created: '${toDateString(NOW)}', comment: '''happy note''', )"
	}

	void testWrite_ShouldWriteDslConflict() {
		writer.write(createConflict(NOW))

		String line = readSingleDslLine()

		assert line == "conflict (created: '${toDateString(NOW)}', question: '''question''', )"
	}

	void testWrite_ShouldWriteDslResolution() {
		writer.write(createResolution(NOW))

		String line = readSingleDslLine()

		assert line == "resolution (created: '${toDateString(NOW)}', answer: '''answer''', )"
	}

	void testWrite_ShouldWriteDslBandStart() {
		writer.write(createBandStart(BandType.learning, NOW))

		String line = readSingleDslLine()

		assert line == "bandStart (created: '${toDateString(NOW)}', type: 'learning', )"
	}

	void testWrite_ShouldWriteDslBandEnd() {
		writer.write(createBandEnd(BandType.learning, NOW))

		String line = readSingleDslLine()

		assert line == "bandEnd (created: '${toDateString(NOW)}', type: 'learning', )"
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
