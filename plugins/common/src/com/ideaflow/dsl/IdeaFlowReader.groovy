package com.ideaflow.dsl

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.Conflict
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange

import java.text.SimpleDateFormat

class IdeaFlowReader {

	IdeaFlowModel readModel(String dslContent) {
		IdeaFlowModelLoader loader = new IdeaFlowModelLoader()
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
		private SimpleDateFormat dateFormat

		IdeaFlowModelLoader() {
			model = new IdeaFlowModel('', null)
			entityIdCounter = 1
		}

		def initialize(Map initializeMap) {
			String dateFormatString = initializeMap['dateFormat']
			dateFormat = new SimpleDateFormat(dateFormatString)

			String createdDateString = initializeMap['created'] as String
			model.created = dateFormat.parse(createdDateString)
		}

		def editorActivity(Map editorActivityMap) {
			Map ctorMap = createConstructorMap(editorActivityMap)
			model.addModelEntity(new EditorActivity(ctorMap))
		}

		def stateChange(Map eventMap) {
			Map ctorMap = createConstructorMap(eventMap)
			model.addModelEntity(new StateChange(ctorMap))
		}

		def note(Map noteMap) {
			Map ctorMap = createConstructorMap(noteMap)
			model.addModelEntity(new Note(ctorMap))
		}

		def conflict(Map eventMap) {
			Map ctorMap = createConstructorMap(eventMap)
			model.addModelEntity(new Conflict(ctorMap))
		}

		def resolution(Map resolutionMap) {
			Map ctorMap = createConstructorMap(resolutionMap)
			model.addModelEntity(new Resolution(ctorMap))
		}

		def bandStart(Map bandStartMap) {
			Map ctorMap = createConstructorMap(bandStartMap)
			model.addModelEntity(new BandStart(ctorMap))
		}

		def bandEnd(Map bandEndMap) {
			Map ctorMap = createConstructorMap(bandEndMap)
			model.addModelEntity(new BandEnd(ctorMap))
		}

		private Map createConstructorMap(Map initialMap) {
			Map constructorMap = initialMap.clone() as Map
			constructorMap['created'] = toDate(constructorMap['created'] as String)
			constructorMap['id'] = entityIdCounter++
			constructorMap
		}

		private Date toDate(String dateString) {
			dateFormat.parse(dateString)
		}

	}
}
