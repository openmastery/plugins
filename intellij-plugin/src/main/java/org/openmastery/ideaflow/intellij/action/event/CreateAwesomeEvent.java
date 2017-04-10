package org.openmastery.ideaflow.intellij.action.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;

import java.util.List;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.disableWhenNotRecording;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getActiveFilePath;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getIFMController;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getSelectedText;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getTaskManager;

public class CreateAwesomeEvent extends AnAction {

	@Override
	public void actionPerformed(AnActionEvent e) {
		IFMController controller = getIFMController(e);
		if (controller != null) {
			String awesomeMessage = promptForInput(controller);

			if (awesomeMessage != null) {
				String snippet = getSelectedText(e);
				if (snippet == null) {
					controller.resolveWithYay(awesomeMessage);
				} else {
					String source = getActiveFilePath(e);
					controller.resolveWithAwesomeSnippet(awesomeMessage, source, snippet);
				}

				getTaskManager().updateTask(controller.getActiveTask());
			}
		}
	}

	private String promptForInput(IFMController controller) {
		List<String> unresolvedPainList = controller.getActiveTask().getTroubleshootingEventList();

		String wtfString = "";
		for (int i = 0; i < unresolvedPainList.size(); i++) {
			String wtfMessage = unresolvedPainList.get(i);
			wtfString += "-- $i: " + wtfMessage + "\n";
		}

		return IdeaFlowApplicationComponent.promptForInput("YAY!", "What did you figure out? (discovery)\n" + wtfString);
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e);
	}

}
