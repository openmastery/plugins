package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel

/**
 * @deprecated
 */
class DSLTimelineSerializer {

	String serialize(IdeaFlowModel model) {
		StringWriter stringWriter = new StringWriter()
		IdeaFlowWriter writer = new IdeaFlowWriter(stringWriter)

		writer.writeInitialization(model.created)
		model.entityList.each {
			writer.write(it)
		}

		writer.close()
		stringWriter.toString()
	}

	IdeaFlowModel deserialize(File dslFile) {
		new IdeaFlowReader().readModel(dslFile, dslFile.text)
	}


}
