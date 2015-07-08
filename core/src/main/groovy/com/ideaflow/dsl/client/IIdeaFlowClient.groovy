package com.ideaflow.dsl.client

import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Task

interface IIdeaFlowClient<T> {

    IdeaFlowModel readModel(T context, Task task)

    void saveModel(T context, IdeaFlowModel ideaFlowModel)
}