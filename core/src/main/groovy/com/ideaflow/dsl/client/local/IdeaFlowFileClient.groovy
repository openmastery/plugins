package com.ideaflow.dsl.client.local

import com.ideaflow.controller.IDEService
import com.ideaflow.dsl.IdeaFlowReader
import com.ideaflow.dsl.client.IIdeaFlowClient
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Task
import org.joda.time.DateTime

class IdeaFlowFileClient<T> implements IIdeaFlowClient<T> {

    private IDEService<T> ideService

    @Override
    IdeaFlowModel readModel(T context, Task task) {

        IdeaFlowModel ideaFlowModel

        def file = addExtension(new File(System.getProperty("user.home") + "/" + task.taskId))

        if (ideService.fileExists(context, file)) {

            println("Resuming existing IdeaFlow: ${file.absolutePath}")

            String xml = ideService.readFile(context, file)

            ideaFlowModel = new IdeaFlowReader().readModel(file, xml)

        } else {
            println("Creating new IdeaFlow: ${file.absolutePath}")

            ideService.createNewFile(context, file, "")

            ideaFlowModel = new IdeaFlowModel(task, new DateTime())
        }

        ideaFlowModel.task = task

        return ideaFlowModel
    }

    private File addExtension(File file) {

        File fileWithExtension = file
        if (file.name.endsWith(".ifm") == false) {
            fileWithExtension = new File(file.absolutePath + ".ifm")
        }

        return fileWithExtension
    }

}
