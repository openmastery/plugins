package org.openmastery.ideaflow.intellij

import com.ideaflow.controller.IFMController
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
import com.intellij.util.messages.MessageBusConnection
import org.jetbrains.annotations.NotNull
import org.openmastery.ideaflow.intellij.file.FileActivityHandler

class IdeaFlowProjectComponent implements ProjectComponent {

	private Project project
	private EventListener listener

	private MessageBusConnection projectConnection

	private static String NAME = "IdeaFlow.Component"

	IdeaFlowProjectComponent(Project project) {
		this.project = project
	}

	String getComponentName() {
		return NAME
	}

	void initComponent() {
		FileActivityHandler fileActivityHandler = IdeaFlowApplicationComponent.getFileActivityHandler()
		listener = new EventListener(fileActivityHandler)
	}

	void disposeComponent() {}

	void projectOpened() {
		projectConnection = project.getMessageBus().connect()
		projectConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)

//		if (getController().workingSetTasks.isEmpty()) {
//			IdeaFlowApplicationComponent.getIFMState().restoreActiveState(project)
//		}
	}

	void projectClosed() {
		projectConnection.disconnect()
	}

	private class EventListener implements FileEditorManagerListener {

		private FileActivityHandler fileActivityHandler
		private FileModificationAdapter fileModificationAdapter

		EventListener(FileActivityHandler fileActivityHandler) {
			this.fileActivityHandler = fileActivityHandler
			this.fileModificationAdapter = new FileModificationAdapter(fileActivityHandler)
		}

		void fileOpened(FileEditorManager source, VirtualFile file) {
		}

		void fileClosed(FileEditorManager source, VirtualFile file) {
			fileActivityHandler.endFileEvent(source.project, file)
		}

		void selectionChanged(FileEditorManagerEvent event) {
			fileActivityHandler.startFileEvent(event.manager.project, event.newFile)
			if (event.newFile) {
				fileModificationAdapter.setActiveFile(event.manager.project, event.newFile)
			}
		}

	}

	private class FileModificationAdapter extends DocumentAdapter {

		private Project activeProject
		private VirtualFile activeFile
		private Document activeDocument
		private FileActivityHandler fileActivityHandler

		FileModificationAdapter(FileActivityHandler fileActivityHandler) {
			this.fileActivityHandler = fileActivityHandler
		}

		void setActiveFile(@NotNull Project project, @NotNull VirtualFile file) {
			clearActiveFile()

			Document document = FileDocumentManager.instance.getCachedDocument(file)
			if (document) {
				activeProject = project
				activeFile = file
				activeDocument = document
				activeDocument.addDocumentListener(this)
			}
		}

		void clearActiveFile() {
			activeDocument?.removeDocumentListener(this)
			activeDocument = null
			activeFile = null
			activeProject = null
		}

		@Override
		void documentChanged(DocumentEvent event) {
			if (activeFile) {
				fileActivityHandler.fileModified(activeProject, activeFile)
			}
		}
	}

}
