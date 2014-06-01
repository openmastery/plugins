package com.ideaflow.intellij.action

import com.ideaflow.model.BandType

@Mixin(ActionSupport)
class ToggleLearning extends ToggleBandStart {

	ToggleLearning() {
		super(BandType.learning, "Start Learning", "What question is in your head?", "End Learning")
	}

}
