package org.openmastery.ideaflow.intellij;

import com.intellij.execution.ExecutionAdapter;
import com.intellij.execution.ExecutionManager;
import com.intellij.execution.configurations.RunProfile;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentAdapter;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.handler.ProcessExecutionHandler;
import org.openmastery.ideaflow.intellij.handler.VirtualFileActivityHandler;

public class IdeaFlowProjectComponent implements ProjectComponent {

	private static final String NAME = "IdeaFlow.Component";

	private Project project;
	private FileListener fileListener;
	private ProcessExecutionListener processExecutionListener;
	private MessageBusConnection projectConnection;

	public IdeaFlowProjectComponent(Project project) {
		this.project = project;
	}

	public String getComponentName() {
		return NAME;
	}

	public void initComponent() {
		VirtualFileActivityHandler fileActivityHandler = IdeaFlowApplicationComponent.getFileActivityHandler();
		fileListener = new FileListener(fileActivityHandler);

		processExecutionListener = new ProcessExecutionListener(IdeaFlowApplicationComponent.getIFMController());
	}

	public void disposeComponent() {}

	public void projectOpened() {
		projectConnection = project.getMessageBus().connect();
		projectConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, fileListener);
		projectConnection.subscribe(ExecutionManager.EXECUTION_TOPIC, processExecutionListener);

	}

	public void projectClosed() {
		projectConnection.disconnect();
	}

	private class FileListener implements FileEditorManagerListener {

		private VirtualFileActivityHandler fileActivityHandler;
		private FileModificationAdapter fileModificationAdapter;

		public FileListener(VirtualFileActivityHandler fileActivityHandler) {
			this.fileActivityHandler = fileActivityHandler;
			this.fileModificationAdapter = new FileModificationAdapter(fileActivityHandler);
		}

		public void fileOpened(FileEditorManager source, VirtualFile file) {
		}

		public void fileClosed(FileEditorManager source, VirtualFile file) {
			fileActivityHandler.endFileEvent(source.getProject(), file);
		}

		public void selectionChanged(FileEditorManagerEvent event) {
			fileActivityHandler.startFileEvent(event.getManager().getProject(), event.getNewFile());
			if (event.getNewFile() != null) {
				fileModificationAdapter.setActiveFile(event.getManager().getProject(), event.getNewFile());
			}
		}

	}

	private class FileModificationAdapter extends DocumentAdapter {

		private Project activeProject;
		private VirtualFile activeFile;
		private Document activeDocument;
		private VirtualFileActivityHandler fileActivityHandler;

		FileModificationAdapter(VirtualFileActivityHandler fileActivityHandler) {
			this.fileActivityHandler = fileActivityHandler;
		}

		public void setActiveFile(@NotNull Project project, @NotNull VirtualFile file) {
			clearActiveFile();

			Document document = FileDocumentManager.getInstance().getCachedDocument(file);
			if (document != null) {
				activeProject = project;
				activeFile = file;
				activeDocument = document;
				activeDocument.addDocumentListener(this);
			}
		}

		public void clearActiveFile() {
			if (activeDocument != null) {
				activeDocument.removeDocumentListener(this);
			}
			activeDocument = null;
			activeFile = null;
			activeProject = null;
		}

		@Override
		public void documentChanged(DocumentEvent event) {
			if (activeFile != null) {
				fileActivityHandler.fileModified(activeProject, activeFile);
			}

		}
	}

	private class ProcessExecutionListener extends ExecutionAdapter {

		private ProcessExecutionHandler handler;

		ProcessExecutionListener(IFMController controller) {
			this.handler = new ProcessExecutionHandler(controller);
		}

		@Override
		public void processStarting(String executorId, @NotNull ExecutionEnvironment env) {
			handler.processStarting(executorId, env);
		}

		public void processStarted(String executorId, @NotNull ExecutionEnvironment env, @NotNull ProcessHandler processHandler) {
			handler.processStarted(env, processHandler);
		}

		public void processTerminated(@NotNull RunProfile runProfile, @NotNull ProcessHandler processHandler) {
			handler.processTerminated(processHandler);
		}
	}

}
