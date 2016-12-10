package org.openmastery.ideaflow.intellij

import com.ideaflow.activity.ActivityHandler
import com.intellij.execution.ExecutionAdapter
import com.intellij.execution.ExecutionManager
import com.intellij.execution.configurations.RunProfile
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessListener
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
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.util.messages.MessageBusConnection
import org.jetbrains.annotations.NotNull
import org.openmastery.ideaflow.intellij.file.VirtualFileActivityHandler

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

		ActivityHandler activityHandler = IdeaFlowApplicationComponent.getIFMController().getActivityHandler()
		processExecutionListener = new ProcessExecutionListener(activityHandler)

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
		ActivityHandler activityHandler
		Map<ProcessHandler, ExitCodeListener> processDecodingMap = [:]

		ProcessExecutionListener(ActivityHandler activityHandler) {
			this.activityHandler = activityHandler
		}

		@Override
		public void processStarting(String executorId, @NotNull ExecutionEnvironment env) {
			Long taskId = IdeaFlowApplicationComponent.getIFMController().getActiveTask()?.id
			String processName = env.runProfile.name
			Long processId = env.executionId
			String executionTaskType = env.getRunnerAndConfigurationSettings().getType().displayName
			boolean isDebug = executorId.equals("Debug")

			activityHandler.markProcessStarting(taskId, processId, processName, executionTaskType, isDebug)
		}

		public void processStarted(String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler processHandler) {
			ExitCodeListener exitCodeListener = new ExitCodeListener(env.executionId)
			processHandler.addProcessListener(exitCodeListener)
			processDecodingMap.put(processHandler, exitCodeListener)
		}

		public void processTerminated(@NotNull RunProfile runProfile, @NotNull ProcessHandler processHandler) {

			ExitCodeListener exitCodeListener = processDecodingMap.get(processHandler)
			if (exitCodeListener) {
				processHandler.removeProcessListener(exitCodeListener)
				activityHandler.markProcessEnding(exitCodeListener.processId, exitCodeListener.exitCode)
			} else {
				//TODO not supposed to happen, do some error handling stuff
			}

		}
	}

	private class ExitCodeListener implements ProcessListener {

		int exitCode
		Long processId

		ExitCodeListener(Long processId) {
			this.processId = processId
		}

		@Override
		void startNotified(ProcessEvent processEvent) {

		}

		@Override
		void processTerminated(ProcessEvent processEvent) {
			exitCode = processEvent.exitCode
		}

		@Override
		void processWillTerminate(ProcessEvent processEvent, boolean b) {

		}

		@Override
		void onTextAvailable(ProcessEvent processEvent, Key key) {
		}
	}


}
