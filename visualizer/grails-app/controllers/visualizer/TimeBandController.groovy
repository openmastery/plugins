package visualizer

import static com.newiron.ideaflow.data.BandType.*
import com.newiron.ideaflow.data.TimePosition
import com.newiron.ideaflow.data.TimeBand

class TimeBandController {

    def list() {
        List<TimePosition> timeBands = []

        timeBands << new TimeBand(Learning, new TimePosition(0, 0, 0), 70 * 60)
        timeBands << new TimeBand(Learning, new TimePosition(2, 51, 0), 104 * 60)
        timeBands << new TimeBand(Rework,   new TimePosition(5, 30, 0), 20 * 60)
        timeBands << new TimeBand(Conflict, new TimePosition(5, 50, 0), 10 * 60,
                "Was the data format I was using going to work in the chart?")
        timeBands << new TimeBand(Conflict, new TimePosition(7, 30, 0), 24 * 60,
                "Why am I getting an IndexOutOfBoundsException when there's no data?")
        timeBands << new TimeBand(Conflict, new TimePosition(8, 15, 0), 18 * 60, "Why isn't the chart showing up?")
        timeBands << new TimeBand(Conflict, new TimePosition(8, 43, 0), 11 * 60, "Why are the bars overlapping?")


        render(template: "list", model: [timeBands: timeBands])
    }

    def delete() {
        //delete a highlight
    }

    def edit() {
        //change the start/stop time on a highlight or change the color
    }

    def create() {
        //create a new highlight
    }
}
