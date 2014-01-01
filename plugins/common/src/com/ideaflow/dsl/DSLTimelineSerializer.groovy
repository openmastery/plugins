package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel

class DSLTimelineSerializer {

    String serialize(IdeaFlowModel model) {
		StringWriter stringWriter = new StringWriter()
		IdeaFlowWriter writer = new IdeaFlowWriter(stringWriter)

		writer.writeInitialization(model.created)
		model.intervalAndEventList.each {
			writer.write(it)
		}

		writer.close()
		stringWriter.toString()
    }

    IdeaFlowModel deserialize(String dslContent) {
		new IdeaFlowReader().readModel(dslContent)
    }

}
