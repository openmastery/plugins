package org.openmastery.ideaflow.intellij

import com.intellij.execution.ExecutionAdapter
import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
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
import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.handler.ProcessExecutionHandler
import org.openmastery.ideaflow.intellij.handler.VirtualFileActivityHandler

class IdeaFlowProjectComponent implements ProjectComponent {

	private Project project
	private FileListener fileListener
	private ProcessExecutionListener processExecutionListener
	private MessageBusConnection projectConnection

	private static String NAME = "IdeaFlow.Component"

	IdeaFlowProjectComponent(Project project) {
		this.project = project
	}

	String getComponentName() {
		return NAME
	}

	void initComponent() {
		VirtualFileActivityHandler fileActivityHandler = IdeaFlowApplicationComponent.getFileActivityHandler()
		fileListener = new FileListener(fileActivityHandler)

		processExecutionListener = new ProcessExecutionListener(IdeaFlowApplicationComponent.getIFMController())
	}

	void disposeComponent() {}

	void projectOpened() {
		projectConnection = project.getMessageBus().connect()
		projectConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileListener)
		projectConnection.subscribe(ExecutionManager.EXECUTION_TOPIC, processExecutionListener);

	}

	void projectClosed() {
		projectConnection.disconnect()
	}

	private class FileListener implements FileEditorManagerListener {

		private VirtualFileActivityHandler fileActivityHandler
		private FileModificationAdapter fileModificationAdapter

		FileListener(VirtualFileActivityHandler fileActivityHandler) {
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
		private VirtualFileActivityHandler fileActivityHandler

		FileModificationAdapter(VirtualFileActivityHandler fileActivityHandler) {
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

	private class ProcessExecutionListener extends ExecutionAdapter {

		private ProcessExecutionHandler handler

		ProcessExecutionListener(IFMController controller) {
			this.handler = new ProcessExecutionHandler(controller)
		}

		@Override
		public void processStarting(String executorId, @NotNull ExecutionEnvironment env) {
			handler.processStarting(executorId, env)
		}

		public void processStarted(String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler processHandler) {
			handler.processStarted(env, processHandler)
		}

		public void processTerminated(@NotNull RunProfile runProfile, @NotNull ProcessHandler processHandler) {
			handler.processTerminated(processHandler)
		}
	}

}
