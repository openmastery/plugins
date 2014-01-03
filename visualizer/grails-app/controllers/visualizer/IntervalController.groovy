package visualizer

import com.newiron.ideaflow.data.RelativeTime

class IntervalController {

    def list() {
        //list out all the intervals for a particular time range that corresponds to the time window
        List<RelativeTime> events = []
        events << new RelativeTime(1, 10, 0)
        events << new RelativeTime(2, 51, 0)
        events << new RelativeTime(4, 35, 0)
        events << new RelativeTime(5, 21, 0)
        events << new RelativeTime(7, 24, 0)
        events << new RelativeTime(9, 20, 0)
    }

    def delete() {
        //remove an interval
    }

    def edit() {
        //reduce the duration of an interval
    }

}
