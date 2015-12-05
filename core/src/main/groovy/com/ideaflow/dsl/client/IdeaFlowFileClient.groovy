package com.ideaflow.dsl.client

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Task
import org.joda.time.DateTime

class IdeaFlowFileClient {

	private DSLTimelineSerializer serializer = new DSLTimelineSerializer()

	IdeaFlowModel readModel(Task task) {
		IdeaFlowModel ideaFlowModel

		File file = getFile(task)

		if (file.exists()) {
			println("Resuming existing IdeaFlow: ${file.absolutePath}")

			ideaFlowModel = serializer.deserialize(task, file.text)
		} else {
			println("Creating new IdeaFlow: ${file.absolutePath}")

			file.parentFile.mkdirs()
			file.text = ""
			ideaFlowModel = new IdeaFlowModel(task, new DateTime())
		}

		ideaFlowModel.task = task

		return ideaFlowModel
	}

	private File getFile(Task task) {
		def file = new File(System.getProperty("user.home") + "/.ifm/" + task.taskId)

		return file.name.endsWith(".ifm") == false ?
				new File(file.absolutePath + ".ifm") :
				file
	}

	void saveModel(IdeaFlowModel ideaFlowModel) {
		String xml = serializer.serialize(ideaFlowModel)
		File file = getFile(ideaFlowModel.task)
		if (!file.exists()) {
			throw new Exception("Invalid file: $file.path")
		}
		file.text = xml
	}
}
