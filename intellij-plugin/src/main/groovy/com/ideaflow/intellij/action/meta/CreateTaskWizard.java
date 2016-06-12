package com.ideaflow.intellij.action.meta;

import com.ideaflow.controller.IFMController;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;

import javax.swing.JComponent;

public class CreateTaskWizard extends DialogWrapper {

	private CreateTaskDialog dialog;

	public CreateTaskWizard(@Nullable Project project) {
		super(project);
		init();
		setTitle("Create Task");
	}

	@Override
	protected JComponent createCenterPanel() {
		if (dialog == null) {
			dialog = new CreateTaskDialog();
		} else {
			dialog.clearTextFields();
		}
		return dialog.panel;
	}

	@Override
	protected ValidationInfo doValidate() {
		return dialog.isValid() ? null : new ValidationInfo("Please fill out the form");
	}

	@Override
	public JComponent getPreferredFocusedComponent() {
		return dialog.name;
	}

	public String getTaskName() {
		return dialog.getNameText();
	}

	public String getTaskDescription() {
		return dialog.getDescriptionText();
	}

	public void createTask() {
		if (showAndGet()) {
			IFMController<Project> controller = IdeaFlowApplicationComponent.getIFMController();
			controller.newIdeaFlow(getTaskName(), getTaskDescription());
		}
	}

}
