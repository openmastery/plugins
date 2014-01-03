package visualizer

import com.newiron.ideaflow.data.Event
import com.newiron.ideaflow.data.RelativeTime

class EventController {

    def list() {
        //events also need comments from the note.  The comments can be edited and the events can be added/removed.
        List<Event> events = []
        events << new Event(1, 10, 0, "Setup initial dashboard layout to support single chart")
        events << new Event(2, 51, 0, "Figure out how to make a bar chart with jqPlot")
        events << new Event(4, 35, 0, "Wire up a bar chart with fake controller data")
        events << new Event(5, 21, 0, "Transform the backend data for the chart format")
        events << new Event(7, 24, 0, "Test the chart with different inputs")
        events << new Event(9, 20, 0, "Commit AVE-472 Commit first activity by type chart")
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
