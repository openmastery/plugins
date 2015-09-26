package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Task

class DSLTimelineSerializer {

	String serialize(IdeaFlowModel model) {
		StringWriter stringWriter = new StringWriter()
		IdeaFlowWriter writer = new IdeaFlowWriter(stringWriter)

		writer.writeInitialization(model.created)
		model.entryList.each {
			writer.write(it)
		}

		writer.close()
		stringWriter.toString()
	}

	IdeaFlowModel deserialize(Task task, String content) {
		new IdeaFlowReader().readModel(task, content)
	}

}
