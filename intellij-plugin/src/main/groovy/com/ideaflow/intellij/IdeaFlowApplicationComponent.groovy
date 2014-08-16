package com.ideaflow.intellij

import com.ideaflow.controller.IDEService
import com.ideaflow.controller.IFMController
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ApplicationComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFrame
import com.intellij.util.messages.MessageBusConnection

class IdeaFlowApplicationComponent implements ApplicationComponent {

    static String NAME = "IdeaFlow.Component"

    private IDEService<Project> ideService
    private IFMController<Project> controller
    private MessageBusConnection appConnection

    static IFMController<Project> getIFMController() {
        ApplicationManager.getApplication().getComponent(NAME).controller
    }

    @Override
    String getComponentName() {
        NAME
    }

    @Override
    void initComponent() {
        ideService = new IDEServiceImpl()
        controller = new IFMController(ideService)

        ApplicationListener applicationListener = new ApplicationListener()
        appConnection = ApplicationManager.getApplication().getMessageBus().connect()
        appConnection.subscribe(ApplicationActivationListener.TOPIC, applicationListener)
    }

    @Override
    void disposeComponent() {
        appConnection.disconnect()
    }


    private static class ApplicationListener implements ApplicationActivationListener {

        void applicationActivated(IdeFrame ideFrame) {
            if (ideFrame.project) {
                getIFMController().startFileEventForCurrentFile(ideFrame.project)
            }
        }

        void applicationDeactivated(IdeFrame ideFrame) {
            if (ideFrame.project) {
                getIFMController().startFileEvent(ideFrame.project, "[[deactivated]]")
            }
        }

    }

}
