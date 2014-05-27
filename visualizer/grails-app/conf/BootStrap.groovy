import com.newiron.ideaflow.presentation.DecoratorLayerInitializer

class BootStrap {

    def init = { servletContext ->
		DecoratorLayerInitializer.init()
    }
    def destroy = {
    }
}
