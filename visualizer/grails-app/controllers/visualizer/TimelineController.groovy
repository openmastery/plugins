package visualizer

import com.ideaflow.timeline.Timeline
import com.newiron.ideaflow.presentation.TimelineChart

class TimelineController {

    static defaultAction = "view"
	IdeaFlowMapService ideaFlowMapService

    def view() {
    }

    def showTimeline() {
		Timeline timeline = ideaFlowMapService.activeTimeline
		TimelineChart chart = new TimelineChart(timeline)

	    response.setContentType("application/json")
	    render chart.toJSON()
    }

}
