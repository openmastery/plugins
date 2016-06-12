package com.ideaflow.intellij.action.ifm

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.intellij.action.IdeaFlowToggleAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.ideaflow.intellij.IdeaFlowApplicationComponent
import org.openmastery.publisher.api.ideaflow.IdeaFlowPartialCompositeState
import org.openmastery.publisher.api.ideaflow.IdeaFlowState
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType

@Mixin(ActionSupport)
abstract class ToggleLearningOrRework extends IdeaFlowToggleAction {

	private IdeaFlowStateType stateType
	private String startBandTitle
	private String startBandMessage
	private String endBandTitle

	ToggleLearningOrRework(IdeaFlowStateType stateType, String startBandTitle, String startBandMessage, String endBandTitle) {
		this.stateType = stateType
		this.startBandTitle = startBandTitle
		this.startBandMessage = startBandMessage
		this.endBandTitle = endBandTitle
	}

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		IdeaFlowPartialCompositeState compositeState = getActiveTaskState(e)
		return compositeState?.isBandStartAllowed(stateType)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		IdeaFlowPartialCompositeState compositeState = getActiveTaskState(e)
		compositeState?.isInState(stateType) ? endBandTitle : startBandTitle
	}

	private IdeaFlowState getActiveLearningOrRework(AnActionEvent e) {
		IdeaFlowPartialCompositeState compositeState = getActiveTaskState(e)
		compositeState?.getActiveLearningOrRework()
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		IdeaFlowState activeLearningOrRework = getActiveLearningOrRework(e)

		if (activeLearningOrRework?.isOfType(stateType)) {
			return "${endBandTitle}: ${activeLearningOrRework.startingComment}"
		} else {
			return getPresentationText(e)
		}
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		IdeaFlowState activeLearningOrRework = getActiveLearningOrRework(e)
		activeLearningOrRework?.isOfType(stateType)
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		IdeaFlowState activeLearningOrRework = getActiveLearningOrRework(e)

		if (activeLearningOrRework?.isOfType(stateType)) {
			// TODO: prompt for ending comment?
			controller.endBand("", stateType)
		} else {
			IdeaFlowState activeConflict = getActiveConflict(e)
			if (activeConflict != null) {
				String resolution = ToggleConflict.endConflict(controller, activeConflict)
				controller.startBand(resolution, stateType)
			} else {
				String comment = IdeaFlowApplicationComponent.promptForInput(startBandTitle, startBandMessage)
				controller.startBand(comment, stateType)
			}
		}
	}

}
