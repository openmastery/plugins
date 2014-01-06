package com.ideaflow.dsl

import com.ideaflow.model.*
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class IdeaFlowReaderWriterTest extends GroovyTestCase {

	private StringWriter stringWriter
	private IdeaFlowWriter writer

	void setUp() {
		stringWriter = new StringWriter()
		writer = new IdeaFlowWriter(stringWriter)
	}

	void testReadModel_ShouldReadContentWrittenByWriter() {
		Date createDate = new Date(NOW)
		EditorActivity editorActivity = createEditorActivity(FILE)
		Note note = createNote("it's a happy note!")
		StateChange event = createStateChange(StateChangeType.startIdeaFlowRecording)
		Conflict conflict = createConflict()
		Resolution resolution = createResolution()
		BandStart bandStart = createBandStart()
		BandEnd bandEnd = createBandEnd()

		writer.writeInitialization(createDate)
		writer.write(editorActivity)
		writer.write(note)
		writer.write(conflict)
		writer.write(resolution)
		writer.write(event)
		writer.write(bandStart)
		writer.write(bandEnd)
		IdeaFlowModel model = new IdeaFlowReader().readModel(stringWriter.toString())

		model.entityList.each { ModelEntity entity ->
			assert entity.id != null
			entity.id = null
		}
		assert model.created == createDate
		assert model.entityList[0] == editorActivity
		assert model.entityList[1] == note
		assert model.entityList[2] == conflict
		assert model.entityList[3] == resolution
		assert model.entityList[4] == event
		assert model.entityList[5] == bandStart
		assert model.entityList[6] == bandEnd
		assert model.size() == 7
	}

}
