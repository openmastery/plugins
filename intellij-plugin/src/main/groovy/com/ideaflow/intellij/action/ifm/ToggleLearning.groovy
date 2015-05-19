package com.ideaflow.intellij.action.ifm

import com.ideaflow.intellij.action.ActionSupport
import com.ideaflow.model.BandType

@Mixin(ActionSupport)
class ToggleLearning extends ToggleBandStart {

	ToggleLearning() {
		super(BandType.learning, "Start Learning", "What question is in your head?", "End Learning")
	}

}
