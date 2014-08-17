package com.ideaflow.intellij

import com.ideaflow.controller.IDEService
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.UIBundle

class IDEServiceImpl implements IDEService<Project> {

	@Override
	String getActiveFileSelection(Project project) {
		String file = null
		VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles()
		if (files.length > 0) {
			file = files[0].name
		}
		return file
	}

	@Override
	String promptForInput(Project project, String title, String message) {
		String note = Messages.showInputDialog(message,
				UIBundle.message(title), Messages.getQuestionIcon());
		return note

	}

	@Override
	void createNewFile(Project project, File file, String contents) {
		file.parentFile.mkdirs()
		file.createNewFile()
		FileHandler handler = createHandler(file)

		runWriteAction(project) {
			handler.write(contents)
		}
	}

	@Override
	void writeFile(Project project, File file, String contents) {
		FileHandler handler = createHandler(file)
		handler.validateFileExists()

		runWriteAction(project) {
			handler.write(contents)
		}
	}

	@Override
	boolean fileExists(Project project, File file) {
		findVirtualFile(file) != null
	}

	@Override
	String readFile(Project project, File file) {
		FileHandler handler = createHandler(file)
		handler.validateFileExists()
		handler.read()
	}

	private FileHandler createHandler(File file) {
		VirtualFile virtualFile = findVirtualFile(file)

		if (virtualFile == null) {
			throw new Exception("Unable to locate IDEA file, path=" + file.absolutePath)
		}
		new FileHandler(virtualFile)
	}

	private VirtualFile findVirtualFile(File file) {
		LocalFileSystem.getInstance().refreshIoFiles([file])
		LocalFileSystem.getInstance().findFileByIoFile(file)
	}

	private void runWriteAction(Project project, Closure closure) {
		WriteAction action = new WriteAction(closure)
		CommandProcessor.getInstance().executeCommand(project, action, 'cmd', null);
		if (action.exception) {
			throw action.exception
		}
	}

	private static class WriteAction implements Runnable {
		Closure closure
		Exception exception

		WriteAction(Closure closure) {
			this.closure = closure
		}

		void run() {
			ApplicationManager.getApplication().runWriteAction(new Runnable() {
				public void run() {
					try {
						closure.call()
					}
					catch (Exception e) {
						exception = e;
					}
				}
			});
		}
	}

	private static class FileHandler {

		VirtualFile file

		FileHandler(VirtualFile file) {
			this.file = file
		}

		void validateFileExists() {
			if (!fileExists()) {
				throw new Exception("Invalid file: $file.path")
			}
		}

		boolean fileExists() {
			file != null && !file.isDirectory() && file.exists()
		}

		String read() {
			VfsUtil.loadText(file)
		}

		void write(String contents) {
			VfsUtil.saveText(file, contents)
		}

	}


}
