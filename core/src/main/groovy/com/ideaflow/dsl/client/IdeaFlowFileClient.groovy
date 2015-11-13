package com.ideaflow.dsl.client

import com.ideaflow.controller.FileService
import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Task
import org.joda.time.DateTime

class IdeaFlowFileClient {

    private FileService fileService
	private DSLTimelineSerializer serializer = new DSLTimelineSerializer()

	IdeaFlowFileClient() {
		this.fileService = new FileService()
	}

    IdeaFlowModel readModel(Task task) {
        IdeaFlowModel ideaFlowModel

        File file = getFile(task)

        if (fileService.fileExists(file)) {
            println("Resuming existing IdeaFlow: ${file.absolutePath}")

            String xml = fileService.readFile(file)

            ideaFlowModel = serializer.deserialize(task, xml)
        } else {
            println("Creating new IdeaFlow: ${file.absolutePath}")

            fileService.createNewFile(file, "")

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
        fileService.writeFile(getFile(ideaFlowModel.task), xml)
    }
}
