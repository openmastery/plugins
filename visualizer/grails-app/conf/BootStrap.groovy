import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.TimeDuration
import com.ideaflow.timeline.TimePosition
import com.newiron.ideaflow.presentation.ActivityDetailDecoratorMixin
import com.newiron.ideaflow.presentation.DecoratorLayerInitializer
import com.newiron.ideaflow.presentation.TimeDurationDecoratorMixin
import com.newiron.ideaflow.presentation.TimePositionDecoratorMixin

class BootStrap {

    def init = { servletContext ->
		DecoratorLayerInitializer.init()
    }
    def destroy = {
    }
}
