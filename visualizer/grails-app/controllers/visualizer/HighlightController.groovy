package visualizer

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.Resolution
import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.GenericBand
import com.ideaflow.timeline.TimeBand
import com.ideaflow.timeline.TimePosition

import static com.ideaflow.model.BandType.*
class HighlightController {

    def list() {
		List<TimeBand> timeBands = [];

		timeBands << createGenericBand(learning, new TimePosition(0, 0, 0), new TimePosition(1, 10, 0))
		timeBands << createGenericBand(learning, new TimePosition(2, 51, 0), new TimePosition(4, 30, 0))
		timeBands << createGenericBand(rework, new TimePosition(5, 30, 0), new TimePosition(5, 50, 0))

		timeBands << createConflict(
			"Was the data format I was using going to work in the chart?",
			"It was using the wrong chart data format for multiple series.",
			new TimePosition(5, 50, 0), new TimePosition(6, 00, 3))

		timeBands << createConflict(
			"Why am I getting an IndexOutOfBoundsException when there's no data?",
			"Code expected there to be at least one color.",
			new TimePosition(7, 30, 0), new TimePosition(7, 54, 10))

		timeBands << createConflict(
			"Why isn't the chart showing up?",
			"Forgot to add chart name to dictionary",
			new TimePosition(8, 15, 0), new TimePosition(8, 33, 24))

		timeBands << createConflict(
			"Why are the bars overlapping?",
			"Need to adjust scale on axis manually",
			new TimePosition(8, 43, 0), new TimePosition(8, 54, 35))


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

	private ConflictBand createConflict(String question, String answer, TimePosition start, TimePosition end) {
		ConflictBand band = new ConflictBand()
		band.conflict = new Conflict(question)
		band.resolution = new Resolution(answer)
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
}
