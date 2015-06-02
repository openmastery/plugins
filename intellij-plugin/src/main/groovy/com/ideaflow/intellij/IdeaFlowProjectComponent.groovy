package com.ideaflow.intellij

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.vcs.VcsCommitToIdeaFlowNoteAdapter
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

class IdeaFlowProjectComponent implements ProjectComponent {

	private Project project
	private EventListener listener

	private MessageBusConnection projectConnection
	private VcsCommitToIdeaFlowNoteAdapter vcsCommitToIdeaFlowNoteAdapter

	private static String NAME = "IdeaFlow.Component"

	IdeaFlowProjectComponent(Project project) {
		this.project = project
	}

	String getComponentName() {
		return NAME
	}

	private IFMController<Project> getController() {
		IdeaFlowApplicationComponent.getIFMController()
	}

	void initComponent() {
		listener = new EventListener()
		vcsCommitToIdeaFlowNoteAdapter = new VcsCommitToIdeaFlowNoteAdapter(project, getController())
	}

	void disposeComponent() {}

	void projectOpened() {
		projectConnection = project.getMessageBus().connect()
		projectConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, listener)

		vcsCommitToIdeaFlowNoteAdapter.connect()

		if (getController().workingSetFiles.isEmpty()) {
			IdeaFlowApplicationComponent.getIFMState().restoreActiveState(project)
		}
	}

	void projectClosed() {
		projectConnection.disconnect()
		vcsCommitToIdeaFlowNoteAdapter.disconnect()
	}

	private class EventListener implements FileEditorManagerListener {

		private FileModificationAdapter fileModificationAdapter = new FileModificationAdapter()

		void fileOpened(FileEditorManager source, VirtualFile file) {
			getController().startFileEvent(source.getProject(), file.name)
		}

		void fileClosed(FileEditorManager source, VirtualFile file) {
			getController().endFileEvent(file.name)
		}

		void selectionChanged(FileEditorManagerEvent event) {
			fileModificationAdapter.clearActiveFile()
			getController().startFileEvent(event.manager.getProject(), event.newFile?.name)
			if (event.newFile) {
				fileModificationAdapter.setActiveFile(event.newFile)
			}
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
				getController().fileModified(activeFile.name)
			}
		}
	}

}
