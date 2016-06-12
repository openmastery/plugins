package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.intellij.action.IdeaFlowToggleAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import org.openmastery.publisher.api.ideaflow.IdeaFlowPartialCompositeState
import org.openmastery.publisher.api.ideaflow.IdeaFlowState
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType

import javax.swing.Icon
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent

@Mixin(ActionSupport)
class ToggleConflict extends IdeaFlowToggleAction {

	private static final String START_CONFLICT_TITLE = "Start Conflict"
	private static final String START_CONFLICT_MSG = "What conflict question is in your head?"
	private static final String END_CONFLICT_TITLE = "End Conflict"
	private static final Icon CONFLICT_ICON = IdeaFlowApplicationComponent.getIcon("conflict.png")
	private static final Icon CONFLICT_REWORK_ICON = IdeaFlowApplicationComponent.getIcon("conflict_rework.png")
	private static final Icon CONFLICT_LEARNING_ICON = IdeaFlowApplicationComponent.getIcon("conflict_learning.png")

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		isTaskActiveAndRecording(e)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		IdeaFlowState activeConflict = getActiveConflict(e)
		return activeConflict ? END_CONFLICT_TITLE : START_CONFLICT_TITLE
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		IdeaFlowState activeConflict = getActiveConflict(e)
		return activeConflict ? "${END_CONFLICT_TITLE}: ${activeConflict.startingComment}" : START_CONFLICT_TITLE
	}

	@Override
	protected Icon getPresentationIcon(AnActionEvent e) {
		IdeaFlowPartialCompositeState compositeState = getActiveTaskState(e)

		if (compositeState) {
			if (compositeState.isInState(IdeaFlowStateType.LEARNING)) {
				return CONFLICT_LEARNING_ICON
			} else if (compositeState.isInState(IdeaFlowStateType.REWORK)) {
				return CONFLICT_REWORK_ICON
			}
		}
		return CONFLICT_ICON
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		return getActiveConflict(e) != null
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IFMController controller = getIFMController(e)
		IdeaFlowState activeConflict = getActiveConflict(e)

		if (activeConflict != null) {
			endConflict(controller, activeConflict)
		} else {
			String note = IdeaFlowApplicationComponent.promptForInput(START_CONFLICT_TITLE, START_CONFLICT_MSG)
			controller.startConflict(note)
		}
	}

	static String endConflict(IFMController controller, IdeaFlowState activeConflict) {
		String answer = IdeaFlowApplicationComponent.promptForInput(END_CONFLICT_TITLE, activeConflict.startingComment)
		controller.endConflict(answer)
		answer
	}

}
