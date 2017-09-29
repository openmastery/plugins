package org.openmastery.ideaflow.intellij.action.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;

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
			Project project = e.getProject();
			Editor editor = e.getData(CommonDataKeys.EDITOR);
			VirtualFile file = e.getData(LangDataKeys.VIRTUAL_FILE);

			String awesomeMessage = promptForInput(controller);

			if (awesomeMessage != null) {
				String snippet = getSelectedText(editor);
				if (snippet == null) {
					controller.resolveWithYay(awesomeMessage);
				} else {
					String source = getActiveFilePath(project, file);
					controller.resolveWithAwesomeSnippet(awesomeMessage, source, snippet);
				}

				getTaskManager().updateTask(controller.getActiveTask());
			}
		}
	}

	private String promptForInput(IFMController controller) {
//		List<String> unresolvedPainList = controller.getActiveTask().getTroubleshootingEventList();
//
//		String wtfString = "";
//		for (int i = 0; i < unresolvedPainList.size(); i++) {
//			String wtfMessage = unresolvedPainList.get(i);
//			wtfString += "-- $i: " + wtfMessage + "\n";
//		}

		return IdeaFlowApplicationComponent.promptForInput("YAY!", "What did you figure out? (#done to resolve)");
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e);
	}

}
