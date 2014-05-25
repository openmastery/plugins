import com.ideaflow.timeline.TimePosition
import com.newiron.ideaflow.presentation.TimePositionDecoratorMixin

class BootStrap {

    def init = { servletContext ->
		TimePosition.mixin(TimePositionDecoratorMixin)
    }
    def destroy = {
    }
}
