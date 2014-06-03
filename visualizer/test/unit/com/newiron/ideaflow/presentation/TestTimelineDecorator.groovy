package com.newiron.ideaflow.presentation

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.Resolution
import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.GenericBand
import com.ideaflow.timeline.TimePosition
import com.ideaflow.timeline.Timeline
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test


class TestTimelineDecorator {

	private static final TimePosition TIME1 = new TimePosition(0,10,0)
	private static final TimePosition TIME2 = new TimePosition(0,20,0)
	private static final TimePosition TIME3 = new TimePosition(0,30,0)

	Timeline timeline
	TimelineDecorator decorator

	@BeforeClass
	static void setupClass() {
		TimelineDecorator.initMixins()
	}

	@Before
	void setup() {
		decorator = new TimelineDecorator()
		timeline = new Timeline()
	}

	@Test
	void decorate_ShouldConfigurePercentOfMax() {
		timeline.addConflictBand createConflictWithDuration(500)
		timeline.addConflictBand createConflictWithDuration(50)
		timeline.addConflictBand createConflictWithDuration(1000)

		decorator.decorate(timeline)

		assert timeline.conflictBands[0].percent == 50
		assert timeline.conflictBands[1].percent == 5
		assert timeline.conflictBands[2].percent == 100
	}


	@Test
	void decorate_ShouldAnnotateActivitiesWithinConflictBands() {
		def activity1 = createActivityDetail(TIME1)
		def activity2 = createActivityDetail(TIME2)
		def activity3 = createActivityDetail(TIME3)

		timeline.addActivityDetail(activity1)
		timeline.addConflictBand(createConflictBand(TIME2, TIME3))
		timeline.addActivityDetail(activity2)
		timeline.addActivityDetail(activity3)

		decorator.decorate(timeline)

		assert activity1.activeBandType == null
		assert activity2.activeBandType == BandType.conflict
		assert activity3.activeBandType == null
	}

	@Test
	void decorate_ShouldAnnotateActivitiesWithinGenericBands() {
		def activity1 = createActivityDetail(TIME1)
		def activity2 = createActivityDetail(TIME2)
		def activity3 = createActivityDetail(TIME3)

		timeline.addActivityDetail(activity1)
		timeline.addGenericBand(createGenericBand(BandType.learning, TIME2, TIME3))
		timeline.addActivityDetail(activity2)
		timeline.addActivityDetail(activity3)

		decorator.decorate(timeline)

		assert activity1.activeBandType == null
		assert activity2.activeBandType == BandType.learning
		assert activity3.activeBandType == null
	}

	@Test
	void decorate_ShouldPrioritizeConflicts_IfOverlappingBands() {
		def activity = createActivityDetail(TIME1)
		timeline.addActivityDetail(activity)
		timeline.addConflictBand(createConflictBand(TIME1, TIME2))
		timeline.addGenericBand(createGenericBand(BandType.learning, TIME1, TIME2))

		decorator.decorate(timeline)

		assert activity.activeBandType == BandType.conflict
	}

	private ConflictBand createConflictBand(TimePosition start, TimePosition end) {
		ConflictBand band = new ConflictBand()
		band.conflict = new Conflict("Conflict Question")
		band.resolution = new Resolution("Resolution Answer")
		band.setStartPosition(start)
		band.setEndPosition(end)
		return band
	}

	private GenericBand createGenericBand(BandType bandType, TimePosition start, TimePosition end) {
		GenericBand band = new GenericBand()
		band.bandStart = new BandStart(bandType, 'test')
		band.bandEnd = new BandEnd(bandType)
		band.setStartPosition(start)
		band.setEndPosition(end)
		return band
	}

	private ActivityDetail createActivityDetail(TimePosition time) {
		new ActivityDetail(time, new EditorActivity(time.actualTime, 'file', true, 10))
	}


	private ConflictBand createConflictWithDuration(int duration) {
		ConflictBand band = new ConflictBand()
		band.conflict = new Conflict('question')
		band.resolution = new Resolution('answer')
		band.setStartPosition(new TimePosition(0, 0, 0))
		band.setEndPosition(new TimePosition(0, 0, duration))
		return band
	}
}
