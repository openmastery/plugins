package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

@Mixin(ActionSupport)
class ToggleLearning extends ToggleBandStart {

	private static final Icon LEARNING_ICON = IdeaFlowApplicationComponent.getIcon("learning.png")
	private static final Icon LEARNING_CONTAINER_ICON = IdeaFlowApplicationComponent.getIcon("learning_container.png")
	private static final Icon LEARNING_CONFLICT_ICON = IdeaFlowApplicationComponent.getIcon("conflict_learning.png")

	ToggleLearning() {
		super(BandType.learning, "Start Learning", "What question is in your head?", "End Learning")
	}

	@Override
	protected Icon getPresentationIcon(AnActionEvent e) {
		IFMController controller = getIFMController(e)

		if (controller) {
			BandStart activeBandStart = controller.getActiveBandStart()
			if (activeBandStart) {
				if (activeBandStart.isLinkedToPreviousBand) {
					return LEARNING_CONFLICT_ICON
				} else {
					return LEARNING_CONTAINER_ICON
				}
			} else if (controller.isOpenConflict()) {
				return LEARNING_CONFLICT_ICON
			}
		}
		return LEARNING_ICON
	}

}
