package visualizer

import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.Timeline

class ConflictController {

	IfmService ifmService

    def list() {

		Timeline timeline = ifmService.activeTimeline
		List<ConflictBand> conflicts = timeline.conflictBands

        render(template: "list", model: [conflicts: conflicts])
    }

    def show() {
        //show the full details of a single conflict
    }

    def edit() {
        //change the details of a conflict
    }


}
