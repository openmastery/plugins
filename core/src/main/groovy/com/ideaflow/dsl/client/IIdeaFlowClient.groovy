package com.ideaflow.dsl.client

import com.ideaflow.model.ModelEntry

interface IIdeaFlowClient {

    void updateEntries(ArrayList<ModelEntry> modelEntries)
}