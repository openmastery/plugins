import com.ideaflow.timeline.TimeDuration
import com.ideaflow.timeline.TimePosition
import com.newiron.ideaflow.presentation.TimeDurationDecoratorMixin
import com.newiron.ideaflow.presentation.TimePositionDecoratorMixin

class BootStrap {

    def init = { servletContext ->
		TimePosition.mixin(TimePositionDecoratorMixin)
		TimeDuration.mixin(TimeDurationDecoratorMixin)
    }
    def destroy = {
    }
}
