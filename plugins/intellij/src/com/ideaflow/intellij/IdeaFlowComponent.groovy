package com.ideaflow.intellij

import com.ideaflow.controller.IDEService
import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.vcs.VcsCommitToIdeaFlowNoteAdapter
import com.intellij.openapi.application.ApplicationActivationListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.event.DocumentAdapter
import com.intellij.openapi.editor.event.DocumentEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.IdeFrame
import com.intellij.util.messages.MessageBusConnection
import org.jetbrains.annotations.NotNull

class IdeaFlowComponent implements ProjectComponent {

    private Project project
    private EventListener listener

    private MessageBusConnection appConnection
    private MessageBusConnection projectConnection
	private VcsCommitToIdeaFlowNoteAdapter vcsCommitToIdeaFlowNoteAdapter

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
        ideService = new IDEServiceImpl(project)
        controller = new IFMController(ideService)

	    listener = new EventListener()
	    vcsCommitToIdeaFlowNoteAdapter = new VcsCommitToIdeaFlowNoteAdapter(project, controller)
    }

    void disposeComponent() {}

    void projectOpened() {
        appConnection = ApplicationManager.getApplication().getMessageBus().connect()
        appConnection.subscribe(ApplicationActivationListener.TOPIC, listener)

        projectConnection = project.getMessageBus().connect()
        projectConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)

	    vcsCommitToIdeaFlowNoteAdapter.connect()
    }

	void projectClosed() {
        appConnection.disconnect()
        projectConnection.disconnect()
		vcsCommitToIdeaFlowNoteAdapter.disconnect()
    }

    private class EventListener implements FileEditorManagerListener, ApplicationActivationListener {

	    private FileModificationAdapter fileModificationAdapter = new FileModificationAdapter()

        void fileOpened(FileEditorManager source, VirtualFile file) {
            controller.startFileEvent(file.name)
        }

        void fileClosed(FileEditorManager source, VirtualFile file) {
            controller.endFileEvent(file.name)
        }

        void selectionChanged(FileEditorManagerEvent event) {
	        fileModificationAdapter.clearActiveFile()
            controller.startFileEvent(event.newFile?.name)
	        if (event.newFile) {
		        fileModificationAdapter.setActiveFile(event.newFile)
	        }
        }

        void applicationActivated(IdeFrame ideFrame) {
            controller.startFileEventForCurrentFile()
        }

        void applicationDeactivated(IdeFrame ideFrame) {
            controller.startFileEvent("[[deactivated]]")
        }

    }

	private class FileModificationAdapter extends DocumentAdapter {

		private VirtualFile activeFile;
		private Document activeDocument;

		void setActiveFile(@NotNull VirtualFile file) {
			if (activeFile) {
				clearActiveFile()
			}

			Document document = FileDocumentManager.instance.getCachedDocument(file)
			if (document) {
				activeFile = file
				activeDocument = document
				activeDocument.addDocumentListener(this)
			}
		}

		void clearActiveFile() {
			activeDocument?.removeDocumentListener(this)
			activeDocument = null
			activeFile = null
		}

		@Override
		void documentChanged(DocumentEvent event) {
			if (activeFile) {
				controller.fileModified(activeFile.name)
			}
		}
	}

}
