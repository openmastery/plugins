package com.ideaflow.intellij.settings

import com.ideaflow.model.Task
import com.intellij.ide.util.PropertiesComponent

class IdeaSettingsService {

    static final Map<String,String> cActive = [
            "Task": "IFM.Active.Task",
            "User": "IFM.Active.User",
            "Project": "IFM.Active.Project",
            "BaseUrl": "IFM.Active.BaseUrl",
            "CalculatedUrl": "IFM.Active.CalculatedUrl"
    ]


    void save(Task data) {

        def props = PropertiesComponent.getInstance()

        props.setValue(cActive.Task, data.taskId.trim())
        props.setValue(cActive.User, data.user.trim())
        props.setValue(cActive.Project, data.project.trim())
        props.setValue(cActive.BaseUrl, data.baseUrl.trim())
        props.setValue(cActive.CalculatedUrl, data.calculatedUrl.trim())
    }

    Task load() {

        def props = PropertiesComponent.getInstance()

        def values = cActive.collectEntries{ name, path -> [path, props.getValue(path)] }

        def data = new Task()

        data.taskId = values[cActive.Task]
        data.user = values[cActive.User]
        data.project = values[cActive.Project]
        data.baseUrl = values[cActive.BaseUrl]
        data.calculatedUrl = values[cActive.CalculatedUrl]

        return data
    }
}
