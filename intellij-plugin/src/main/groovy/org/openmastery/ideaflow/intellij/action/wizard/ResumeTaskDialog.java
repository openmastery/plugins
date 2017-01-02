package org.openmastery.ideaflow.intellij.action.wizard;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class ResumeTaskDialog {

	public JPanel panel;
	public JTextField name;

	public String getNameText() {
		return name.getText();
	}

	public void clearTextFields() {
		name.setText("");
	}

	public boolean isValid() {
 		return isNotEmpty(name.getText());
	}

	private boolean isNotEmpty(String string) {
		return (string == null || string.isEmpty()) == false;
	}

}
