package com.ideaflow.intellij.action

import com.ideaflow.model.BandType

@Mixin(ActionSupport)
class ToggleRework extends ToggleBandStart {

	ToggleRework() {
		super(BandType.rework, "Start Rework", "What are you reworking?", "End Rework")
	}

}
