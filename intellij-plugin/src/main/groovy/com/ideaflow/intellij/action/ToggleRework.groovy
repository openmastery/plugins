package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.intellij.openapi.actionSystem.AnActionEvent
import javax.swing.Icon

@Mixin(ActionSupport)
class ToggleRework extends ToggleBandStart {

	private static final Icon REWORK_ICON = IdeaFlowApplicationComponent.getIcon("rework.png")
	private static final Icon REWORK_CONTAINER_ICON = IdeaFlowApplicationComponent.getIcon("rework_container.png")
	private static final Icon REWORK_CONFLICT_ICON = IdeaFlowApplicationComponent.getIcon("conflict_rework.png")

	ToggleRework() {
		super(BandType.rework, "Start Rework", "What are you reworking?", "End Rework")
	}

	@Override
	protected Icon getPresentationIcon(AnActionEvent e) {
		IFMController controller = getIFMController(e)

		if (controller) {
			BandStart activeBandStart = controller.getActiveBandStart()
			if (activeBandStart) {
				if (activeBandStart.isLinkedToPreviousBand) {
					return REWORK_CONFLICT_ICON
				} else {
					return REWORK_CONTAINER_ICON
				}
			} else if (controller.isOpenConflict()) {
				return REWORK_CONFLICT_ICON
			}
		}
		return REWORK_ICON
	}

}
