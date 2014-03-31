package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel

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
		IdeaFlowModel model = deserialize(dslFile.text)
		model.fileName = dslFile.absolutePath
		model
	}

	IdeaFlowModel deserialize(String dslContent) {
		new IdeaFlowReader().readModel(dslContent)
	}

}
