package com.ideaflow.intellij.action.ifm;

import com.ideaflow.controller.IFMController;
import com.ideaflow.intellij.action.ActionSupport;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;

public class CreatePainSnippet extends CreatePainNote {

	private ActionSupport actionSupport = new ActionSupport();

	private String getSelectedText(AnActionEvent e) {
		Editor editor = e.getData(CommonDataKeys.EDITOR);
		if (editor == null) {
			return null;
		}

		SelectionModel selectionModel = editor.getSelectionModel();
		return selectionModel.getSelectedText();
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		String snippet = getSelectedText(e);

		if (snippet != null) {
			String painMessage = promptForInput();

			if (painMessage != null) {
				IFMController controller = IdeaFlowApplicationComponent.getIFMController();
				controller.createPainSnippet(painMessage, null, snippet);
			}
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		actionSupport.disableWhenNotRecordingOrNoSelectedText(e);
	}

}