package visualizer

import com.ideaflow.timeline.*

class HighlightController {

	IfmService ifmService

    def list() {

		Timeline timeline = ifmService.activeTimeline
		List<TimeEntry> timeEntries = timeline.getSequencedTimeline()

		render(template: "list", model: [timeEntries: timeEntries])
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
