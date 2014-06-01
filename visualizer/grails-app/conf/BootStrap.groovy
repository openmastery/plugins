import com.newiron.ideaflow.presentation.TimelineDecorator
import com.newiron.ideaflow.presentation.TimelineDecorator
import com.newiron.ideaflow.presentation.TimelineDecorator

class BootStrap {

    def init = { servletContext ->
		TimelineDecorator.initMixins()
    }
    def destroy = {
    }
}
