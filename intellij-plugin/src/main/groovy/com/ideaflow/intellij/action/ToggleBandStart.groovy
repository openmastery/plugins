package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowApplicationComponent
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.intellij.openapi.actionSystem.AnActionEvent

@Mixin(ActionSupport)
abstract class ToggleBandStart extends IdeaFlowToggleAction {

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
	protected boolean isPresentationEnabled(AnActionEvent e) {
		boolean enabled = isIdeaFlowOpen(e)

		if (enabled) {
			BandType activeBandStartType = getActiveBandStartType(e)
			enabled = (activeBandStartType == null) || (activeBandStartType == bandType)
		}
		return enabled
	}

	@Override
	protected String getPresentationText(AnActionEvent e) {
		BandStart bandStart = getActiveBandStart(e)
		return bandStart ? endBandTitle : startBandTitle
	}

	@Override
	protected String getPresentationDescription(AnActionEvent e) {
		BandStart bandStart = getActiveBandStart(e)

		if (bandType == bandStart?.type) {
			return bandStart ? "${endBandTitle}: ${bandStart.comment}" : startBandTitle
		} else {
			return getPresentationText(e)
		}
	}

	@Override
	boolean isSelected(AnActionEvent e) {
		return isOpenBand(e) && (getActiveBandStartType(e) == bandType)
	}

	@Override
	void setSelected(AnActionEvent e, boolean state) {
		IFMController controller = IdeaFlowApplicationComponent.getIFMController()
		BandStart activeBandStart = controller.getActiveBandStart()

		if ((activeBandStart != null) && (activeBandStart.type == bandType)) {
			controller.endBand(e.project, bandType)
		} else {
			String comment = controller.promptForInput(e.project, startBandTitle, startBandMessage)
			controller.startBand(e.project, comment, bandType)
		}
	}

}
