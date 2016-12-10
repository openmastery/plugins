package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.intellij.action.IdeaFlowToggleAction
import com.ideaflow.state.TaskState
import com.ideaflow.state.TimeConverter
import com.intellij.openapi.actionSystem.AnActionEvent
import org.joda.time.LocalDateTime
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettings
import org.openmastery.ideaflow.intellij.settings.IdeaFlowSettingsTaskManager
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class ToggleBlock extends IdeaFlowToggleAction {

	private static final String RESOLVE_TASK = "Resolve the block"
	private static final String BLOCK_TASK = "Block the task"

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		return isTaskActive(e)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		return  isTaskBlocked(e) ? RESOLVE_TASK : BLOCK_TASK
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		return "${getPresentationText(e)}: ${getActiveIdeaFlowName(e)}"
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		return isTaskBlocked(e)
	}

	@Override
	void setSelected(AnActionEvent e, boolean blocked) {
		IFMController ifmController = getIFMController(e)
		if (ifmController == null) return;

		if (blocked) {
			String comment = IdeaFlowApplicationComponent.promptForInput("What's blocked?!", "What block are you waiting on?")
			ifmController.blockTask(comment)
		} else {
			ifmController.resolveBlock()
		}

		getTaskManager().updateTask(ifmController.getActiveTask())
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenNotRecording(e)
	}

	private boolean isTaskBlocked(AnActionEvent e) {
		boolean isTaskBlocked = false;
		IFMController ifmController = getIFMController(e)
		if (ifmController) {
			isTaskBlocked = ifmController.isTaskBlocked()
		}
		return isTaskBlocked
	}

	private static IdeaFlowSettingsTaskManager getTaskManager() {
		IdeaFlowSettings.instance.taskManager
	}




}
