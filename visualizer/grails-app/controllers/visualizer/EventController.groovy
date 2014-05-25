package visualizer

import com.ideaflow.model.Note
import com.ideaflow.timeline.Event
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline

class EventController {

	IdeaFlowMapService ideaFlowMapService

    def list() {

		Timeline timeline = ideaFlowMapService.activeTimeline
		List<Event> events = timeline.events

        render(template: "list", model: [events: events])
    }

    def delete() {
        //remove an event from the timeline
    }

    def edit() {
        //change the comment of an event
    }

    def create() {
        //create new event with time and comment
    }

}
