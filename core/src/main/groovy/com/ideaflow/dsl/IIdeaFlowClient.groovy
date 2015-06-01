package com.ideaflow.dsl

import com.ideaflow.model.IdeaFlowModel

interface IIdeaFlowClient {

    IdeaFlowModel readModel(TaskId taskId)

    void writeModel(IdeaFlowModel model)
}