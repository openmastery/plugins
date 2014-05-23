package visualizer

import com.ideaflow.model.Note
import com.ideaflow.timeline.Event
import com.ideaflow.timeline.TimePosition

class EventController {

    def list() {
        //events also need comments from the note.  The comments can be edited and the events can be added/removed.
		List<Event> events = []

		events << createEvent(new TimePosition(1, 10, 0), "Setup initial dashboard layout to support single chart")
		events << createEvent(new TimePosition(2, 51, 0), "Figure out how to make a bar chart with jqPlot")
		events << createEvent(new TimePosition(4, 35, 0), "Wire up a bar chart with fake controller data")
		events << createEvent(new TimePosition(5, 21, 0), "Transform the backend data for the chart format")
		events << createEvent(new TimePosition(7, 24, 0), "Test the chart with different inputs")
		events << createEvent(new TimePosition(9, 20, 0), "Commit AVE-472 Commit first activity by type chart")

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

	private Event createEvent(TimePosition timePosition, String comment) {
		Note note = new Note(comment)
		Event event = new Event(timePosition, note)
		return event
	}
}
