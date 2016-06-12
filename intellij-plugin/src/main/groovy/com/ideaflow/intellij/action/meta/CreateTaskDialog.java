package com.ideaflow.intellij.action.meta;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class CreateTaskDialog {

	public JPanel panel;
	public JTextField name;
	public JTextField description;

	public String getNameText() {
		return name.getText();
	}

	public String getDescriptionText() {
		return description.getText();
	}

	public void clearTextFields() {
		name.setText("");
		description.setText("");
	}

	public boolean isValid() {
 		return isNotEmpty(name.getText());
	}

	private boolean isNotEmpty(String foo) {
		return (foo == null || foo.isEmpty()) == false;
	}

}
