package visualizer

import com.ideaflow.timeline.Timeline
import com.newiron.ideaflow.presentation.TimelineChart

class TimelineController {

	IfmService ifmService

    def showTimeline() {
		Timeline timeline = ifmService.activeTimeline
		TimelineChart chart = new TimelineChart(timeline)

	    response.setContentType("application/json")
	    render chart.toJSON()
    }

}
