package visualizer

import com.ideaflow.model.EditorActivity
import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.TimePosition
import com.newiron.ideaflow.presentation.TimePositionDecoratorMixin
import org.joda.time.DateTime

class ActivityDetailController {

    def list() {

		List<ActivityDetail> activities = []
		activities << createActivity(new TimePosition(1, 10, 0), 5, "main.gsp")
		activities << createActivity(new TimePosition(1, 12, 5), 20, "_list.gsp")
		activities << createActivity(new TimePosition(1, 14,34), 413, "TestDashboard")
		activities << createActivity(new TimePosition(1, 17, 51), 43, "Dashboard")
		activities << createActivity(new TimePosition(1, 22, 6), 67, "TestDashboard")
		activities << createActivity(new TimePosition(1, 25, 9), 54, "Dashboard")
		activities << createActivity(new TimePosition(1, 26, 2), 763, "TestDashboard")
		activities << createActivity(new TimePosition(1, 27, 59), 47, "ChartData")
		activities << createActivity(new TimePosition(1, 27, 59), 85, "TestChartData")
		activities << createActivity(new TimePosition(1, 27, 59), 432, "ChartData")
		activities << createActivity(new TimePosition(1, 27, 59), 54, "TestChartData")
		activities << createActivity(new TimePosition(1, 27, 59), 58, "Dashboard")
		activities << createActivity(new TimePosition(1, 27, 59), 76, "_show.gsp")

        render(template: "list", model: [activities: activities])
    }

    def delete() {
        //remove an interval
    }

    def edit() {
        //reduce the duration of an interval
    }

	private ActivityDetail createActivity(TimePosition timePosition, int duration, String activityName) {
		EditorActivity editorActivity = new EditorActivity(DateTime.now(), activityName, duration)
		ActivityDetail activityDetail = new ActivityDetail(timePosition, editorActivity)
		return activityDetail
	}

}
