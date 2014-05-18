package visualizer

import com.newiron.ideaflow.data.TimeBand
import com.newiron.ideaflow.data.TimePosition
import com.newiron.ideaflow.data.Timeline
import grails.converters.JSON
import groovy.json.JsonBuilder

import static com.newiron.ideaflow.data.BandType.Conflict
import static com.newiron.ideaflow.data.BandType.Learning
import static com.newiron.ideaflow.data.BandType.Rework


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
        timeline.start = new TimePosition(0)
        timeline.end = new TimePosition(9, 22, 0)

        timeline.events << new TimePosition(1, 10, 0)
        timeline.events << new TimePosition(2, 51, 0)
        timeline.events << new TimePosition(4, 35, 0)
        timeline.events << new TimePosition(5, 21, 0)
        timeline.events << new TimePosition(7, 24, 0)
        timeline.events << new TimePosition(9, 20, 0)

        timeline.timeBands << new TimeBand(Learning, new TimePosition(0, 0, 0), 70 * 60)
        timeline.timeBands << new TimeBand(Learning, new TimePosition(2, 51, 0), 104 * 60)
        timeline.timeBands << new TimeBand(Rework, new TimePosition(5, 30, 0), 20 * 60)
        timeline.timeBands << new TimeBand(Conflict, new TimePosition(5, 50, 0), 10 * 60)
        timeline.timeBands << new TimeBand(Conflict, new TimePosition(7, 30, 0), 24 * 60)
        timeline.timeBands << new TimeBand(Conflict, new TimePosition(8, 15, 0), 18 * 60)
        timeline.timeBands << new TimeBand(Conflict, new TimePosition(8, 43, 0), 11 * 60)

	    JsonBuilder jsonBuilder = new JsonBuilder()
        jsonBuilder(timeline)
	    String json = jsonBuilder.toString()
	    json = json.substring(1, json.length() - 1)

	    response.setContentType("application/json")
	    render json
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
