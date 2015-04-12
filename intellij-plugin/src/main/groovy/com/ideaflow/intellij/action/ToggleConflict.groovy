package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import javax.swing.Icon

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
		return isIdeaFlowOpenAndNotPaused(e)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		Conflict activeConflict = getActiveConflict(e)
		return activeConflict ? END_CONFLICT_TITLE : START_CONFLICT_TITLE
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		Conflict activeConflict = getActiveConflict(e)
		return activeConflict ? "${END_CONFLICT_TITLE}: ${activeConflict.question}" : START_CONFLICT_TITLE
	}

    @Override
    protected Icon getPresentationIcon(AnActionEvent e) {
        IFMController controller = getIFMController(e)

        if (controller) {
            BandStart activeBandStart = controller.getActiveBandStart()
            if (activeBandStart?.type == BandType.learning) {
                return CONFLICT_LEARNING_ICON
            } else if (activeBandStart?.type == BandType.rework) {
                return CONFLICT_REWORK_ICON
            }
        }
        return CONFLICT_ICON
    }

    @Override
	boolean isSelected(AnActionEvent e) {
		return isOpenConflict(e)
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IFMController controller = getIFMController(e)
		Conflict activeConflict = controller.getActiveConflict()

		if (activeConflict != null) {
			endConflict(e.project, controller, activeConflict)
		} else {
			String note = controller.promptForInput(e.project, START_CONFLICT_TITLE, START_CONFLICT_MSG)
			controller.startConflict(e.project, note)
		}
	}

	static String endConflict(Project project, IFMController controller, Conflict activeConflict) {
		String note = controller.promptForInput(project, END_CONFLICT_TITLE, activeConflict.question)
		controller.endConflict(project, note)
		note
	}

}
