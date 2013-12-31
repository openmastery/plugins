package com.ideaflow.eclipse.handler

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IWorkspaceRoot
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

import com.ideaflow.controller.IFMController;
import com.ideaflow.eclipse.IdeaFlowActivator;

class IdeaFlowHandler extends ToggleHandler {

	void executeHandler(ExecutionEvent event) {
		if (ideaFlowProjectMissing()) {
			return
		}

		IFMController controller = IdeaFlowActivator.getController()
		if (controller.isIdeaFlowOpen()) {
			controller.closeIdeaFlow()
		} else {
			String fileName = promptForFileName('Create IdeaFlow map',
					'Name of IdeaFlow mapping file:')
			if (fileName) {
				controller.newIdeaFlow(fileName)
			}
		}
	}

	boolean isChecked() {
		IFMController controller = IdeaFlowActivator.getController()
		controller.isIdeaFlowOpen()
	}

	private String promptForFileName(String title, String message) {
		String inputValue = null
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()

		InputDialog dialog = new InputDialog(shell, title,
				message, null, new FileValidator())

		if (dialog.open() == InputDialog.OK) {
			inputValue = dialog.getValue()
		}
		return inputValue
	}

	private boolean ideaFlowProjectMissing() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot()
		IProject project = root.getProject('ideaflow')

		if (!project.exists()) {
			MessageDialog.openWarning(shell, "No 'ideaflow' project found.",
					"Please first create an 'ideaflow' project")
		}
		return !project.exists()
	}

	private Shell getShell() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
	}

	private static class FileValidator implements IInputValidator {

		@Override
		public String isValid(String newText) {
			String errorText = null
			if (!newText) {
				errorText = "Please enter a file name"
			} else {
				Exception ex = tryToValidateFile(newText)
				if (ex) {
					errorText = ex.message
				}
			}
			return errorText;
		}

		private Exception tryToValidateFile(String newFileName) {
			IFMController controller = IdeaFlowActivator.getController()
			Exception failReason = null
			try {
				controller.validateFilePath(newFileName)
			} catch (Exception ex) {
				failReason = ex
			}
			return failReason
		}
	}
}
