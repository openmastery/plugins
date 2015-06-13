package com.ideaflow.intellij.action.ifm

import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.model.BandType

@Mixin(ActionSupport)
class ToggleRework extends ToggleBandStart {

	ToggleRework() {
		super(BandType.rework, "Start Rework", "What are you reworking?", "End Rework")
	}

}
