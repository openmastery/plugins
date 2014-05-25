package visualizer

import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.Timeline

class ActivityDetailController {

	IfmService ifmService

    def list() {

		Timeline timeline = ifmService.activeTimeline
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
