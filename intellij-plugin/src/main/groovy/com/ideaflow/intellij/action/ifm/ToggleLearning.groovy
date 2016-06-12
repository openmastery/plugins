package com.ideaflow.intellij.action.ifm

import com.ideaflow.intellij.action.ActionSupport
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType

@Mixin(ActionSupport)
class ToggleLearning extends ToggleLearningOrRework {

	ToggleLearning() {
		super(IdeaFlowStateType.LEARNING, "Start Learning", "What question is in your head?", "End Learning")
	}

}
