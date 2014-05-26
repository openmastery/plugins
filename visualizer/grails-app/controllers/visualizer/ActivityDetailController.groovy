package visualizer

import com.ideaflow.timeline.Timeline
import com.newiron.ideaflow.presentation.TimelineDetail

class ActivityDetailController {

	IfmService ifmService

    def list() {

		Timeline timeline = ifmService.activeTimeline
		TimelineDetail detail = new TimelineDetail(timeline)

        render(template: "list", model: [detailEntries: detail.listRows()])
    }

    def delete() {
        //remove an interval
    }

    def edit() {
        //reduce the duration of an interval
    }


}
