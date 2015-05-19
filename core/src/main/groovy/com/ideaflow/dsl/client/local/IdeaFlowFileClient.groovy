package com.ideaflow.dsl.client.local

import com.ideaflow.controller.IDEService
import com.ideaflow.dsl.DSLTimelineSerializer
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

        def file = _getFile(task)

        if (ideService.fileExists(context, file)) {

            println("Resuming existing IdeaFlow: ${file.absolutePath}")

            String xml = ideService.readFile(context, file)

            ideaFlowModel = new IdeaFlowReader().readModel(task, xml)

        } else {
            println("Creating new IdeaFlow: ${file.absolutePath}")

            ideService.createNewFile(context, file, "")

            ideaFlowModel = new IdeaFlowModel(task, new DateTime())
        }

        ideaFlowModel.task = task

        return ideaFlowModel
    }

    private File _getFile(Task task) {

        def file = new File(System.getProperty("user.home") + "/.ifm/" + task.taskId)

        return file.name.endsWith(".ifm") == false ?
                new File(file.absolutePath + ".ifm") :
                file
    }

    @Override
    void saveModel(T context, IdeaFlowModel ideaFlowModel) {

        String xml = new DSLTimelineSerializer().serialize(ideaFlowModel)
        ideService.writeFile(context, _getFile(ideaFlowModel.task), xml)
    }
}
