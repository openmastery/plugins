package com.newiron.ideaflow.presentation

import com.ideaflow.model.*
import com.ideaflow.timeline.*
import org.junit.Before
import org.junit.Test

class TestTimelineChart {

	Timeline timeline
	TimelineChart timelineChart

	private static final TimePosition DEFAULT_TIME = new TimePosition(0,0,0)
	private static final TimePosition TIME1 = new TimePosition(0,10,0)
	private static final TimePosition TIME2 = new TimePosition(0,20,0)
	private static final TimePosition TIME3 = new TimePosition(0,30,0)


	@Before
	void setup() {
		timeline = new Timeline()
		timelineChart = new TimelineChart(timeline)
	}

	@Test
	void toJSON_ShouldReturnAllEvents() {
		timeline.addEvent(createEvent())
		timeline.addEvent(createEvent())

		String json = timelineChart.toJSON()

		println json
		assert json =~ /"events":\[\{.+\}\]/
	}

	@Test
	void toJSON_ShouldReturnAllTimebands() {
		timeline.addConflictBand(createConflictBand())
		timeline.addGenericBand(createGenericBand())

		String json = timelineChart.toJSON()

		println json
		assert json =~ /"timeBands":\[\{.+\}\]/
	}

	@Test
	void getTimebands_ShouldContainAllBandTypes() {
		timeline.addGenericBand(createGenericBand())
		timeline.addConflictBand(createConflictBand())

		timelineChart.timeBands
	}

	private Event createEvent() {
		new Event(DEFAULT_TIME, new Note("hello"))
	}

	private GenericBand createGenericBand() {
		GenericBand band = new GenericBand()
		band.bandStart = new BandStart(BandType.learning)
		band.bandEnd = new BandEnd(BandType.learning)
		band.setStartPosition(DEFAULT_TIME)
		band.setEndPosition(DEFAULT_TIME)
		return band
	}

	private ConflictBand createConflictBand() {
		ConflictBand band = new ConflictBand()
		band.conflict = new Conflict("Conflict Question")
		band.resolution = new Resolution("Resolution Answer")
		band.setStartPosition(DEFAULT_TIME)
		band.setEndPosition(DEFAULT_TIME)
		return band
	}
}
