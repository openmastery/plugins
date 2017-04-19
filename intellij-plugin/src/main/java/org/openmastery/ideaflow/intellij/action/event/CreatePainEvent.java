package org.openmastery.ideaflow.intellij.action.event;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import org.openmastery.ideaflow.controller.IFMController;
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent;
import org.openmastery.ideaflow.state.TaskState;

import javax.swing.Icon;

import static org.openmastery.ideaflow.intellij.action.ActionSupport.disableWhenNotRecording;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getActiveFilePath;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getIFMController;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getSelectedText;
import static org.openmastery.ideaflow.intellij.action.ActionSupport.getTaskManager;

public class CreatePainEvent extends AnAction {

	private Icon PAIN_ICON;
	private Icon PAIN_ICON_DOT1;
	private Icon PAIN_ICON_DOT2;
	private Icon PAIN_ICON_DOT3;


	CreatePainEvent() {
		PAIN_ICON = IdeaFlowApplicationComponent.getIcon("pain.png");
		PAIN_ICON_DOT1 = IdeaFlowApplicationComponent.getIcon("pain_1dot.png");
		PAIN_ICON_DOT2 = IdeaFlowApplicationComponent.getIcon("pain_2dot.png");
		PAIN_ICON_DOT3 = IdeaFlowApplicationComponent.getIcon("pain_3dot.png");
	}

	@Override
	public void actionPerformed(AnActionEvent e) {
		IFMController controller = getIFMController(e);
		if (controller != null) {
			String painMessage = promptForInput(controller);
			if (painMessage != null) {
				String snippet = getSelectedText(e);
				if (snippet != null) {
					String source = getActiveFilePath(e);
					controller.createPainSnippet(painMessage, source, snippet);
				} else {
					controller.createPain(painMessage);
				}

				getTaskManager().updateTask(controller.getActiveTask());
			}
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e);

		IFMController controller = IdeaFlowApplicationComponent.getIFMController();
		if (controller != null && controller.getActiveTask() != null) {
			TaskState activeTask = controller.getActiveTask();
			updateIcon(e.getPresentation(), activeTask.getUnresolvedPainCount());
		}
	}

	protected String promptForInput(IFMController controller) {
		String questionToAsk = determineQuestionToAsk(controller);

		return IdeaFlowApplicationComponent.promptForInput("WTF?!", questionToAsk);
	}

	private String determineQuestionToAsk(IFMController controller) {
		String questionToAsk;
		int painSize = 0;

		if (controller != null && controller.getActiveTask() != null) {
			TaskState activeTask = controller.getActiveTask();
			painSize = activeTask.getUnresolvedPainCount();
		}
		if (painSize == 0) {
			questionToAsk = "What are you confused about? (question)";
		} else {
			questionToAsk = "What are you still confused about? (discovery + question)";
		}
		return questionToAsk;
	}

	private void updateIcon(Presentation presentation, int unresolvedPainCount) {
		if (unresolvedPainCount < 1) {
			presentation.setIcon(PAIN_ICON);
		} else if (unresolvedPainCount == 1) {
			presentation.setIcon(PAIN_ICON_DOT1);
		} else if (unresolvedPainCount == 2) {
			presentation.setIcon(PAIN_ICON_DOT2);
		} else if (unresolvedPainCount > 2) {
			presentation.setIcon(PAIN_ICON_DOT3);
		}
	}

}
