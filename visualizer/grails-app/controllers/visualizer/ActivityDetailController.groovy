package visualizer

import com.ideaflow.timeline.Timeline

class ActivityDetailController {

	IfmService ifmService

    def list() {

		Timeline timeline = ifmService.activeTimeline

        render(template: "list", model: [detailEntries: timeline.sequencedTimelineDetail])
    }

    def delete() {
        //remove an interval
    }

    def edit() {
        //reduce the duration of an interval
    }


}
