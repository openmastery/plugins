package visualizer

import com.ideaflow.model.*
import com.ideaflow.timeline.*
import com.newiron.ideaflow.presentation.TimePositionDecoratorMixin
import com.newiron.ideaflow.presentation.TimelineChart
import grails.converters.JSON
import org.joda.time.DateTime

import static com.ideaflow.model.BandType.learning
import static com.ideaflow.model.BandType.rework


class TimelineController {

    static defaultAction = "view"

    def view() {
        //33720 seconds in Map - 800 px graphic  = 42sec per pixel

        //scale 1u = how much time
        //end time = 14:33
        //event locations = time, xunits
        //band locations = position, size, type
    }

    def showTimeline() {
		Timeline timeline = new Timeline()
		timeline.addActivityDetail(createActivity(new TimePosition(0,0,0)))
		timeline.addActivityDetail(createActivity(new TimePosition(9,22,0)))

		timeline.addEvent(createEvent(new TimePosition(1, 10, 0)))
		timeline.addEvent(createEvent(new TimePosition(2, 51, 0)))
		timeline.addEvent(createEvent(new TimePosition(4, 35, 0)))
		timeline.addEvent(createEvent(new TimePosition(5, 21, 0)))
		timeline.addEvent(createEvent(new TimePosition(7, 24, 0)))
		timeline.addEvent(createEvent(new TimePosition(9, 20, 0)))

		timeline.addGenericBand createGenericBand(learning, new TimePosition(0, 0, 0), new TimePosition(1, 10, 0))
		timeline.addGenericBand createGenericBand(learning, new TimePosition(2, 51, 0), new TimePosition(4, 30, 0))
		timeline.addGenericBand createGenericBand(rework, new TimePosition(5, 30, 0), new TimePosition(5, 50, 0))

		timeline.addConflictBand createConflict(new TimePosition(5, 50, 0), new TimePosition(6, 00, 3))
		timeline.addConflictBand createConflict(new TimePosition(7, 30, 0), new TimePosition(7, 54, 10))
		timeline.addConflictBand createConflict(new TimePosition(8, 15, 0), new TimePosition(8, 33, 24))
		timeline.addConflictBand createConflict(new TimePosition(8, 43, 0), new TimePosition(8, 54, 35))

		TimelineChart chart = new TimelineChart(timeline)

	    response.setContentType("application/json")
	    render chart.toJSON()
    }

	private ActivityDetail createActivity(TimePosition timePosition) {
		EditorActivity editorActivity = new EditorActivity(DateTime.now(), 'file.txt', 10)
		ActivityDetail activityDetail = new ActivityDetail(timePosition, editorActivity)
		return activityDetail
	}

	private Event createEvent(TimePosition timePosition) {
		Note note = new Note("note")
		Event event = new Event(timePosition, note)
		return event
	}

	private ConflictBand createConflict(TimePosition start, TimePosition end) {
		ConflictBand band = new ConflictBand()
		band.conflict = new Conflict("question")
		band.resolution = new Resolution("answer")
		band.setStartPosition(start)
		band.setEndPosition(end)
		return band
	}

	private GenericBand createGenericBand(BandType bandType, TimePosition start, TimePosition end ) {
		GenericBand band = new GenericBand()
		band.bandStart = new BandStart(bandType)
		band.bandEnd = new BandEnd(bandType)
		band.setStartPosition(start)
		band.setEndPosition(end)
		return band
	}




    def showBook() {
        Book book = new Book(id:'123', title:"Three Little Pigs")
        render book as JSON
    }

    def renderString() {
        render "this is a string"
    }

    static class Book {
        String id
        String title
    }
}
