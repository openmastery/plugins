package com.ideaflow.dsl.client

import com.ideaflow.model.entry.ModelEntry

interface IIdeaFlowClient {

    void updateEntries(ArrayList<ModelEntry> modelEntries)
}