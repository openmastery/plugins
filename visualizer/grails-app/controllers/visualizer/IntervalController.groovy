package visualizer

import com.newiron.ideaflow.data.BandType
import com.newiron.ideaflow.data.TimeBand
import com.newiron.ideaflow.data.TimePosition

class IntervalController {

    def list() {
        //list out all the intervals for a particular time range that corresponds to the time window
        List<TimeBand> intervals = []
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 10, 0), 5, "main.gsp")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 12, 5), 20, "_list.gsp")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 14, 34), 413, "TestDashboard")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 17, 51), 43, "Dashboard")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 22, 6), 67, "TestDashboard")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 25, 9), 54, "Dashboard")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 26, 2), 763, "TestDashboard")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 27, 59), 47, "ChartData")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 31, 4), 85, "TestChartData")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 36, 54), 432, "ChartData")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 39, 31), 54, "TestChartData")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 40, 21), 58, "_show.gsp")
        intervals << new TimeBand(BandType.Interval, new TimePosition(1, 41, 8), 76, "Dashboard")

        render(template: "list", model: [intervals: intervals])
    }

    def delete() {
        //remove an interval
    }

    def edit() {
        //reduce the duration of an interval
    }

}
