package com.ideaflow.intellij

import com.ideaflow.model.TimeService

import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFrame
import com.intellij.util.messages.MessageBusConnection
import com.ideaflow.controller.IFMController
import com.ideaflow.controller.IDEService

class IdeaFlowComponent implements ProjectComponent {

    private Project project
    private EventListener listener

    private MessageBusConnection appConnection;
    private MessageBusConnection projectConnection;

    private IFMController controller
    private IDEService ideService

    private static String NAME = "IdeaFlow.Component"

    IdeaFlowComponent(Project project) {
        this.project = project
    }

    static IFMController getIFMController(Project project) {
        project.getComponent(NAME).controller
    }

    static IDEService getIDEService(Project project) {
        project.getComponent(NAME).ideService
    }

    String getComponentName() {
        return NAME
    }

    void initComponent() {
        listener = new EventListener()

        def timeService = new TimeService()
        ideService = new IDEServiceImpl(project)
        controller = new IFMController(timeService, ideService)
    }

    void disposeComponent() {}

    void projectOpened() {
        appConnection = ApplicationManager.getApplication().getMessageBus().connect()
        appConnection.subscribe(ApplicationActivationListener.TOPIC, listener)

        projectConnection = project.getMessageBus().connect()
        projectConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)
    }

    void projectClosed() {
        appConnection.disconnect()
        projectConnection.disconnect()
    }

    private class EventListener implements FileEditorManagerListener, ApplicationActivationListener {

        void fileOpened(FileEditorManager source, VirtualFile file) {
            controller.startFileEvent(file.name)
        }

        void fileClosed(FileEditorManager source, VirtualFile file) {
            controller.endFileEvent(file.name)
        }

        void selectionChanged(FileEditorManagerEvent event) {
            controller.startFileEvent(event.newFile?.name)
        }

        void applicationActivated(IdeFrame ideFrame) {
            controller.startFileEventForCurrentFile()
        }

        void applicationDeactivated(IdeFrame ideFrame) {
            controller.startFileEvent("[[deactivated]]")
        }

    }
}
