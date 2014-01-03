package visualizer

import static com.newiron.ideaflow.data.BandType.*
import com.newiron.ideaflow.data.RelativeTime
import com.newiron.ideaflow.data.TimeBand
import com.newiron.ideaflow.data.Timeline
import grails.converters.JSON


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
        timeline.start = new RelativeTime(0)
        timeline.end = new RelativeTime(9, 22, 0)

        timeline.events << new RelativeTime(1, 10, 0)
        timeline.events << new RelativeTime(2, 51, 0)
        timeline.events << new RelativeTime(4, 35, 0)
        timeline.events << new RelativeTime(5, 21, 0)
        timeline.events << new RelativeTime(7, 24, 0)
        timeline.events << new RelativeTime(9, 20, 0)

        timeline.timeBands << new TimeBand(Conflict, new RelativeTime(5, 50, 0), 10 * 60)
        timeline.timeBands << new TimeBand(Conflict, new RelativeTime(7, 30, 0), 24 * 60)
        timeline.timeBands << new TimeBand(Conflict, new RelativeTime(8, 15, 0), 18 * 60)
        timeline.timeBands << new TimeBand(Conflict, new RelativeTime(8, 43, 0), 11 * 60)
        timeline.timeBands << new TimeBand(Learning, new RelativeTime(0, 0, 0), 70 * 60)
        timeline.timeBands << new TimeBand(Learning, new RelativeTime(2, 51, 0), 104 * 60)
        timeline.timeBands << new TimeBand(Rework, new RelativeTime(5, 30, 0), 20 * 60)

        render timeline as JSON
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
