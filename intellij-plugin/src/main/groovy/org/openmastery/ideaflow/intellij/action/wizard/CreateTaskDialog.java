package org.openmastery.ideaflow.intellij.action.wizard;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class CreateTaskDialog {

	public JPanel panel;
	public JTextField name;
	public JTextField description;
	private JTextField project;

	public String getNameText() {
		return name.getText();
	}

	public String getDescriptionText() {
		return description.getText();
	}

	public String getProjectText() { return project.getText(); }

	public void clearTextFields() {
		name.setText("");
		description.setText("");
		project.setText("");
	}

	public boolean isValid() {
 		return isNotEmpty(name.getText());
	}

	private boolean isNotEmpty(String string) {
		return (string == null || string.isEmpty()) == false;
	}

}
