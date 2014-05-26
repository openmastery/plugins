package com.newiron.ideaflow.presentation

import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.Event
import com.ideaflow.timeline.TimeDuration
import com.ideaflow.timeline.TimePosition


class DecoratorLayerInitializer {

	static void init() {
		TimePosition.mixin(TimePositionDecoratorMixin)
		TimeDuration.mixin(TimeDurationDecoratorMixin)
		ActivityDetail.mixin(ActiveBandDecoratorMixin)
		Event.mixin(ActiveBandDecoratorMixin)
	}
}
