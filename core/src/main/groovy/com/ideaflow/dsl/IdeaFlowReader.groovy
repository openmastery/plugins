package com.ideaflow.dsl

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Idle
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class IdeaFlowReader {

	/**
	 * If the dsl content is too large, groovy will fail to compile the script due to jvm method
	 * length restrictions (java.lang.ClassFormatError: Invalid method Code length xxx) so chunk
	 * the content by lines and load it one block at a time.
	 */
	private static final int DEFAULT_CHUNK_SIZE = 500
	private static final String LINE_SEPARATOR = System.getProperty("line.separator")

	private int chunkSize

	IdeaFlowReader() {
		this(DEFAULT_CHUNK_SIZE)
	}

	IdeaFlowReader(int chunkSize) {
		this.chunkSize = chunkSize
	}

	IdeaFlowModel readModel(File modelFile, String dslContent) {
		IdeaFlowModelLoader loader = new IdeaFlowModelLoader(modelFile)

		for (List<String> dslContentChunk : dslContent.readLines().collate(chunkSize)) {
			String partialDslContent = dslContentChunk.join(LINE_SEPARATOR)
			readPartialModel(loader, partialDslContent)
		}
		loader.model
	}

	private void readPartialModel(IdeaFlowModelLoader loader, String partialDslContent) {
		String wrappedDslContent = "ideaFlowModel {${partialDslContent}}"
		Script dslScript = new GroovyShell().parse(wrappedDslContent)

		dslScript.metaClass = createEMC(dslScript.class, { ExpandoMetaClass emc ->
			emc."ideaFlowModel" { Closure cl ->
				cl.delegate = loader
				cl.resolveStrategy = Closure.DELEGATE_FIRST

				cl()
			}
		})
		dslScript.run()
	}

	private ExpandoMetaClass createEMC(Class clazz, Closure cl) {
		ExpandoMetaClass emc = new ExpandoMetaClass(clazz, false)

		cl(emc)

		emc.initialize()
		return emc
	}

	private static class IdeaFlowModelLoader {

		IdeaFlowModel model
		private int entityIdCounter
		private DateTimeFormatter dateFormat

		IdeaFlowModelLoader(File modelFile) {
			model = new IdeaFlowModel(modelFile, new DateTime())
			entityIdCounter = 1
		}

		def initialize(Map initializeMap) {
			String dateFormatString = initializeMap['dateFormat']
			dateFormat = DateTimeFormat.forPattern(dateFormatString)

			String createdDateString = initializeMap['created'] as String
			model.created = dateFormat.parseDateTime(createdDateString)
		}

		def editorActivity(Map editorActivityMap) {
			if (editorActivityMap.containsKey("modified")) {
				String modifiedString = editorActivityMap["modified"]
				editorActivityMap["modified"] = Boolean.valueOf(modifiedString)
			}
			addModelEntity(EditorActivity, editorActivityMap)
		}

		def stateChange(Map eventMap) {
			addModelEntity(StateChange, eventMap)
		}

		def note(Map noteMap) {
			addModelEntity(Note, noteMap)
		}

		def conflict(Map eventMap) {
			addModelEntity(Conflict, eventMap)
		}

		def resolution(Map resolutionMap) {
			addModelEntity(Resolution, resolutionMap)
		}

		def bandStart(Map bandStartMap) {
			addModelEntity(BandStart, bandStartMap)
		}

		def bandEnd(Map bandEndMap) {
			addModelEntity(BandEnd, bandEndMap)
		}

		def idle(Map idleMap) {
			addModelEntity(Idle, idleMap)
		}

		private void addModelEntity(Class type, Map initialMap) {
			Map constructorMap = createConstructorMap(initialMap)
			ModelEntity entity = type.newInstance(constructorMap)
			model.addModelEntity(entity)
		}

		private Map createConstructorMap(Map initialMap) {
			Map constructorMap = initialMap.clone() as Map
			constructorMap['created'] = toDate(constructorMap['created'] as String)
			constructorMap['id'] = entityIdCounter++
			constructorMap
		}

		private DateTime toDate(String dateString) {
			if (dateFormat == null) {
				throw new RuntimeException("Invalid IdeaFlowMap, initialize should be the first entry in the map")
			}

			dateFormat.parseDateTime(dateString)
		}

	}
}
