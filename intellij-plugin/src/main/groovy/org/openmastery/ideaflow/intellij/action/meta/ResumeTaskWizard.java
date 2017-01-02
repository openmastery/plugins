package org.openmastery.ideaflow.intellij.action.meta;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class ResumeTaskWizard extends DialogWrapper {

	private ResumeTaskDialog dialog;

	public ResumeTaskWizard(@Nullable Project project) {
		super(project);
		init();
		setTitle("Resume Task");
	}

	@Override
	protected JComponent createCenterPanel() {
		if (dialog == null) {
			dialog = new ResumeTaskDialog();
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

	public boolean shouldCreateTask() {
		return showAndGet();
	}

}
