package visualizer

import com.ideaflow.model.*
import com.ideaflow.timeline.*

class HighlightController {

	IdeaFlowMapService ideaFlowMapService

    def list() {

		Timeline timeline = ideaFlowMapService.activeTimeline
		List<TimeBand> timeBands = timeline.timeBands

		render(template: "list", model: [timeBands: timeBands])
    }

    def delete() {
        //delete a highlight
    }

    def edit() {
        //change the start/stop time on a highlight or change the color
    }

    def create() {
        //create a new highlight
    }

}
