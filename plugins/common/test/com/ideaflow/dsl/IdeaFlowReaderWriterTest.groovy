package com.ideaflow.dsl

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange
import com.ideaflow.model.StateChangeType
import org.joda.time.DateTime
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class IdeaFlowReaderWriterTest extends GroovyTestCase {

	private StringWriter stringWriter
	private IdeaFlowWriter writer

	void setUp() {
		stringWriter = new StringWriter()
		writer = new IdeaFlowWriter(stringWriter)
	}

	void testReadWriteSymmetryWithData() {
		DateTime createDate = new DateTime(NOW)
		EditorActivity modifiedEditorActivity = createEditorActivity(FILE)
		modifiedEditorActivity.modified = true
		EditorActivity unmodifiedEditorActivity = createEditorActivity(FILE)
		Note note = createNote("it's a happy note!")
		StateChange event = createStateChange(StateChangeType.startIdeaFlowRecording)
		Conflict conflict = createConflict()
		Resolution resolution = createResolution()
		BandStart bandStart = createBandStart()
		BandEnd bandEnd = createBandEnd()

		writer.writeInitialization(createDate)
		writer.write(modifiedEditorActivity)
		writer.write(unmodifiedEditorActivity)
		writer.write(note)
		writer.write(conflict)
		writer.write(resolution)
		writer.write(event)
		writer.write(bandStart)
		writer.write(bandEnd)

		IdeaFlowModel model = readModelAndClearIds()

		assert model.created == createDate
		assert model.entityList.remove(0) == modifiedEditorActivity
		assert model.entityList.remove(0) == unmodifiedEditorActivity
		assert model.entityList.remove(0) == note
		assert model.entityList.remove(0) == conflict
		assert model.entityList.remove(0) == resolution
		assert model.entityList.remove(0) == event
		assert model.entityList.remove(0) == bandStart
		assert model.entityList.remove(0) == bandEnd
		assert model.size() == 0
	}

	private IdeaFlowModel readModelAndClearIds() {
		IdeaFlowModel model = new IdeaFlowReader().readModel(stringWriter.toString())

		model.entityList.each { ModelEntity entity ->
			assert entity.id != null
			entity.id = null
		}
		model
	}

	void testReadWriteSymmetry_EnsureNewlyAddedModelEntitySubTypesAreSerializable() {
		List<ModelEntity> subTypeInstances = getInitializedModelEntitySubClassInstances()

		writer.writeInitialization(new DateTime(NOW))
		subTypeInstances.each { ModelEntity entity ->
			try {
				writer.write(entity)
			} catch (MissingMethodException ex) {
				throw new RuntimeException("Possible reason for failure: if a subtype of ${ModelEntity.simpleName} has just been added, " +
						"ensure ${IdeaFlowWriter.simpleName} declares method write(${entity.class.simpleName})", ex)
			}
		}
		IdeaFlowModel model
		try {
			model = new IdeaFlowReader().readModel(stringWriter.toString())
		} catch (MissingMethodException ex) {
			throw new RuntimeException("Possible reason for failure: if a subtype of ${ModelEntity.simpleName} has just been added, " +
					"ensure ${IdeaFlowReader.simpleName} declares appropriate read(<subtype>) method", ex)
		}

		model.entityList.each { ModelEntity entity ->
			assert entity.id != null
			entity.id = null
		}
		for (int i = 0; i < subTypeInstances.size(); i++) {
			assert subTypeInstances[i] == model.entityList[i]
		}
		assert subTypeInstances.size() == model.size()
	}

	private List<ModelEntity> getInitializedModelEntitySubClassInstances() {
		List<ModelEntity> subTypeInstances = getModelEntitySubClassInstances()
		DateTime createDate = new DateTime(NOW)

		subTypeInstances.each { ModelEntity entity ->
			createDate = createDate.plusSeconds(1)
			entity.created = createDate
		}
		subTypeInstances
	}

	public void testBackslashInText_ShouldNotExplode() {
		Conflict conflict = createConflict()
		conflict.question = /What's up with this regex: \s+\S+\s+/

		writer.writeInitialization(new DateTime(NOW))
		writer.write(conflict)

		IdeaFlowModel model = readModelAndClearIds()

		assert model.entityList.remove(0) == conflict
		assert model.entityList.size() == 0
	}

	public void testQuoteAtStartOrEnd_ShouldNotExplode() {
		Note singleQuoteNote = createNote("'here' is a single 'quote'")
		Note doubleQuoteNote = createNote('"here" is a double "quote"')

		writer.writeInitialization(new DateTime(NOW))
		writer.write(singleQuoteNote)
		writer.write(doubleQuoteNote)

		IdeaFlowModel model = readModelAndClearIds()

		assert model.entityList.remove(0) == singleQuoteNote
		assert model.entityList.remove(0) == doubleQuoteNote
		assert model.entityList.size() == 0
	}

	public void testTripleQuotesInString_ShouldNotExpode() {
		Note tripleSingleQuoteNote = createNote("here is a '''triple''' quote")
		Note tripleDoubleQuoteNote = createNote('here is a """double""" quote')

		writer.writeInitialization(new DateTime(NOW))
		writer.write(tripleSingleQuoteNote)
		writer.write(tripleDoubleQuoteNote)

		IdeaFlowModel model = readModelAndClearIds()

		assert model.entityList.remove(0) == tripleSingleQuoteNote
		assert model.entityList.remove(0) == tripleDoubleQuoteNote
		assert model.entityList.size() == 0
	}

}
