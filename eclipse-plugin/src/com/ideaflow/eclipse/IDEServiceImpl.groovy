package com.ideaflow.eclipse

import com.ideaflow.controller.IDEService
import org.eclipse.core.resources.*
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

class IDEServiceImpl implements IDEService {

	@Override
	public String getActiveFileSelection() {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow()?.getActivePage()?.getActiveEditor()
		return editor?.getEditorInput()?.name
	}

	@Override
	public void createNewFile(String relativePath, String initialContent) {
		println "path = $relativePath, content = $initialContent"
		if (fileExists(relativePath)) {
			println('existing file')
			writeToFile(relativePath, initialContent)
		} else {
			println('new file')
			InputStream stream = toStream(initialContent)
			IFile file = project.getFile(relativePath)
			file.create(stream, false, null)
			stream.close()
		}
	}

	String promptForInput(String title, String message) {
		String inputValue = null
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()

		InputDialog dialog = new InputDialog(shell, title,
				message, null, createValidator())

		if (dialog.open() == InputDialog.OK) {
			inputValue = dialog.getValue()
		}
		return inputValue
	}

	private IInputValidator createValidator() { {
			newText ->
			newText? null : "Please enter a value"
		} as IInputValidator
	}

	private InputStream toStream(String content) {
		new ByteArrayInputStream(content.getBytes())
	}

	private IProject getProject() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot()
		root.getProject('ideaflow')
	}

	@Override
	public boolean fileExists(String relativePath) {
		project.refreshLocal(IResource.DEPTH_INFINITE, null)
		
		IFile file = project.getFile(relativePath)
		return file.exists()
	}
	
	private boolean folderExists(String relativePath) {
		project.refreshLocal(IResource.DEPTH_INFINITE, null)
		IFolder folder = project.getFolder(relativePath)
		return folder.exists()
	}

	@Override
	public String readFile(String relativePath) {
		IFile file = project.getFile(relativePath)
		InputStream stream = file.getContents()
		return stream.text
	}

	@Override
	public void writeToFile(String relativePath, String contents) {
		IFile file = project.getFile(relativePath)
		InputStream stream = toStream(contents)
		
		file.setContents(stream, false, false, null)
		stream.close()
	}

	@Override
	public void validateFilePath(String relativePath) {
		String subFolder = parseSubFolder(relativePath)
		if (subFolder) {
			IFile folder = project.getFile(subFolder)
			println("folder = "+folder)
			if (!folderExists(subFolder)) {				
				throw new Exception("$subFolder does not exist")
			}
		}

	}
	
	private String parseSubFolder(String relativePath) {
		String subFolder = null
		int index = relativePath.lastIndexOf('/')
		if (index >= 0) {
			subFolder = relativePath.substring(0, index)
		}
		return subFolder
	}

}
