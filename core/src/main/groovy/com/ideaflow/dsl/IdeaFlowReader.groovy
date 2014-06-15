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
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

class IdeaFlowReader {

	IdeaFlowModel readModel(File modelFile, String dslContent) {
		IdeaFlowModelLoader loader = new IdeaFlowModelLoader(modelFile)
		String wrappedDslContent = "ideaFlowModel {${dslContent}}"
		Script dslScript = new GroovyShell().parse(wrappedDslContent)

		dslScript.metaClass = createEMC(dslScript.class, { ExpandoMetaClass emc ->
			emc."ideaFlowModel" { Closure cl ->
				cl.delegate = loader
				cl.resolveStrategy = Closure.DELEGATE_FIRST

				cl()
			}
		})
		dslScript.run()
		loader.model
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
