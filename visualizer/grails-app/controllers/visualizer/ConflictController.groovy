package visualizer

import com.newiron.ideaflow.data.Conflict
import com.newiron.ideaflow.data.RelativeTime
import com.newiron.ideaflow.data.TimeBand

class ConflictController {

    def list() {
        //list out all of the conflicts
        List<Conflict> conflicts = []
        conflicts <<  new Conflict("Was the data format I was using going to work in the chart?",
            "It was using the wrong chart data format for multiple series.", new RelativeTime(5, 50, 0), 10 * 60);

        Conflict conflict = new Conflict("Why am I getting an IndexOutOfBoundsException when there's no data?",
            "Code expected there to be at least one color.", new RelativeTime(5, 50, 0), 10 * 60)
        conflict.mistakeType = "Unexpected Dependency"
        conflict.cause = "BarChartDecorator, ChartData"
        conflicts << conflict

        conflicts <<  new Conflict("Why isn't the chart showing up?",
            "Forgot to add chart name to dictionary", new RelativeTime(5, 50, 0), 10 * 60);
        conflicts <<  new Conflict("Why are the bars overlapping?",
            "Need to adjust scale on axis manually", new RelativeTime(5, 50, 0), 10 * 60);

        render(template: "list", model: [conflicts: conflicts])
    }

    def show() {
        //show the full details of a single conflict
    }

    def edit() {
        //change the details of a conflict
    }

}
