package com.ideaflow.intellij.settings

import com.ideaflow.model.Task
import com.intellij.ide.util.PropertiesComponent
import groovy.json.JsonOutput
import groovy.json.JsonSlurper

class IdeaSettingsService {

    static final String ACTIVE_TASK = "IFM.Active.Task"
    static final String OPEN_TASKS = "IFM.Open.Tasks"


    void saveActiveTask(Task task) {

        def props = PropertiesComponent.getInstance()

        props.setValue(ACTIVE_TASK, JsonOutput.toJson(task))
    }

    private Task _loadTask(Map values) {

        def data = new Task()

        data.taskId = values["taskId"]
        data.user = values["user"]
        data.project = values["project"]
        data.baseUrl = values["baseUrl"]
        data.calculatedUrl = values["calculatedUrl"]

        return data
    }

    /**
     * Always returns non-null
     * @return
     */
    Task loadActiveTask() {

        def props = PropertiesComponent.getInstance()

        try {
            def values = new JsonSlurper().parseText(props.getValue(ACTIVE_TASK) ?: '{}')

            return _loadTask(values)
        }
        catch(JsonException) {

            return new Task()
        }
    }

    void saveOpenTasks(Collection<Task> tasks) {

        def props = PropertiesComponent.getInstance()

        props.setValue(OPEN_TASKS, JsonOutput.toJson(tasks))
    }

    Collection<Task> loadOpenTasks() {

        def props = PropertiesComponent.getInstance()

        def values = []

        try {
            values = new JsonSlurper().parseText(props.getValue(OPEN_TASKS) ?: '[]')
        }
        catch(JsonException) {}

        //Skip Tasks that don't have a taskId
        return values.collect{ data -> _loadTask(values) }.findAll{ it.hasTaskId() }
    }
}
