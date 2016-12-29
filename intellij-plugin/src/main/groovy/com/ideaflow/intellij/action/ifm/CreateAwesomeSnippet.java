package com.ideaflow.intellij.action.ifm;

import com.ideaflow.controller.IFMController;
import com.ideaflow.intellij.action.ActionSupport;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.publisher.api.event.EventType;

public class CreateAwesomeSnippet extends CreateAwesomeNote {

	private ActionSupport actionSupport = new ActionSupport();

	@Override
	public void actionPerformed(AnActionEvent e) {
		String snippet = actionSupport.getSelectedText(e);

		if (snippet != null) {
			String awesomeMessage = promptForInput();

			if (awesomeMessage != null) {
				IFMController controller = IdeaFlowApplicationComponent.getIFMController();
				controller.resolveWithAwesomeSnippet(awesomeMessage, null, snippet);
			}
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		actionSupport.disableWhenNotRecordingOrNoSelectedText(e);
	}

}