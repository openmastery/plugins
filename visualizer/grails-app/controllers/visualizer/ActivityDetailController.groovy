package visualizer

import com.ideaflow.model.EditorActivity
import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.TimeBand
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline
import com.newiron.ideaflow.presentation.TimePositionDecoratorMixin
import org.joda.time.DateTime

class ActivityDetailController {

	IdeaFlowMapService ideaFlowMapService

    def list() {

		Timeline timeline = ideaFlowMapService.activeTimeline
		List<ActivityDetail> activities = timeline.activityDetails

        render(template: "list", model: [activities: activities])
    }

    def delete() {
        //remove an interval
    }

    def edit() {
        //reduce the duration of an interval
    }


}
