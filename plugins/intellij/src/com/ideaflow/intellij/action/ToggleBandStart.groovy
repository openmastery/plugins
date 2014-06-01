package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowComponent
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction

@Mixin(ActionSupport)
abstract class ToggleBandStart extends ToggleAction {

	private BandType bandType
	private String startBandTitle
	private String startBandMessage
	private String endBandTitle

	ToggleBandStart(BandType bandType, String startBandTitle, String startBandMessage, String endBandTitle) {
		this.bandType = bandType
		this.startBandTitle = startBandTitle
		this.startBandMessage = startBandMessage
		this.endBandTitle = endBandTitle
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		return isOpenBand(e) && (getActiveBandStartType(e) == bandType)
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IFMController controller = IdeaFlowComponent.getIFMController(e.project)
		BandStart activeBandStart = controller.getActiveBandStart()

		if ((activeBandStart != null) && (activeBandStart.type == bandType)) {
			controller.endBand(bandType)
		} else {
			String comment = controller.promptForInput(startBandTitle, startBandMessage)
			controller.startBand(comment, bandType)
		}
	}

	@Override
	public void update(AnActionEvent e) {
		super.update(e);
		disableWhenOtherBandTypeOpen(e)

		Presentation presentation = e.getPresentation()
		if (isOpenBand(e)) {
			presentation.setText(endBandTitle)
		} else {
			presentation.setText(startBandTitle)
		}

	}

	private void disableWhenOtherBandTypeOpen(AnActionEvent e) {
		boolean enabled = isIdeaFlowOpenAndNotPaused(e)

		if (enabled) {
			BandType activeBandStartType = getActiveBandStartType(e)
			enabled = (activeBandStartType == null) || (activeBandStartType == bandType)
		}

		Presentation presentation = e.getPresentation()
		presentation.setEnabled(enabled);
	}

}
