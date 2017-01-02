package org.openmastery.ideaflow.intellij.action.ifm

import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.intellij.action.ActionSupport
import org.openmastery.ideaflow.intellij.action.IdeaFlowToggleAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.openmastery.publisher.api.event.EventType

@Mixin(ActionSupport)
class TogglePause extends IdeaFlowToggleAction {

	private static final String RECORD_TITLE = "Record IdeaFlow"
	private static final String PAUSE_TITLE = "Pause IdeaFlow"

	@Override
	protected boolean isPresentationEnabled(AnActionEvent e) {
		return isTaskActive(e)
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		return isPaused(e) ? RECORD_TITLE : PAUSE_TITLE
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		return "${getPresentationText(e)}: ${getActiveIdeaFlowName(e)}"
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		return isPaused(e)
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IFMController controller = getIFMController(e)

		//WARNING: Make sure event state is sent in unpaused state, since after activity is paused we can't write to logs
		if (state) {
			controller?.createEvent("Pause", EventType.DEACTIVATE)
			controller?.setPaused(true)
		} else {
			controller?.setPaused(false)
			controller?.createEvent("Unpause", EventType.ACTIVATE)
		}

	}


}
