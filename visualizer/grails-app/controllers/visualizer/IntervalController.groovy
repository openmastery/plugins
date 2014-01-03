package visualizer

import com.newiron.ideaflow.data.TimePosition

class IntervalController {

    def list() {
        //list out all the intervals for a particular time range that corresponds to the time window
        List<TimePosition> events = []
        events << new TimePosition(1, 10, 0)
        events << new TimePosition(2, 51, 0)
        events << new TimePosition(4, 35, 0)
        events << new TimePosition(5, 21, 0)
        events << new TimePosition(7, 24, 0)
        events << new TimePosition(9, 20, 0)
    }

    def delete() {
        //remove an interval
    }

    def edit() {
        //reduce the duration of an interval
    }

}
