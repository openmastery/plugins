package com.ideaflow.intellij.action.ifm

import com.ideaflow.intellij.action.ActionSupport
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType

@Mixin(ActionSupport)
class ToggleRework extends ToggleLearningOrRework {

	ToggleRework() {
		super(IdeaFlowStateType.REWORK, "Start Rework", "What are you reworking?", "End Rework")
	}

}
