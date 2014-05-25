package visualizer

import com.ideaflow.model.Conflict
import com.ideaflow.model.Resolution
import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline

class ConflictController {

	IdeaFlowMapService ideaFlowMapService

    def list() {

		Timeline timeline = ideaFlowMapService.activeTimeline
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
