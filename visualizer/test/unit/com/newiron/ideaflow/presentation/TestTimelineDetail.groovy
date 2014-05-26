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
import org.junit.Test


class TestTimelineDetail {

	Timeline timeline
	TimelineDetail timelineDetail

	private static final TimePosition TIME1 = new TimePosition(0,10,0)
	private static final TimePosition TIME2 = new TimePosition(0,20,0)
	private static final TimePosition TIME3 = new TimePosition(0,30,0)

	@Before
	void setUp() {
		this.timeline = new Timeline()
		this.timelineDetail = new TimelineDetail(timeline)
		DecoratorLayerInitializer.init()
	}

	@Test
	void initializeActiveBandTypes_ShouldAnnotateActivitiesWithinConflictBands() {
		def activity1 = createActivityDetail(TIME1)
		def activity2 = createActivityDetail(TIME2)
		def activity3 = createActivityDetail(TIME3)

		timeline.addActivityDetail(activity1)
		timeline.addConflictBand(createConflictBand(TIME2, TIME3))
		timeline.addActivityDetail(activity2)
		timeline.addActivityDetail(activity3)

		timelineDetail.initializeActiveBandTypes()

		assert activity1.activeBandType == null
		assert activity2.activeBandType == BandType.conflict
		assert activity3.activeBandType == null
	}

	@Test
	void initializeActiveBandTypes_ShouldAnnotateActivitiesWithinGenericBands() {
		def activity1 = createActivityDetail(TIME1)
		def activity2 = createActivityDetail(TIME2)
		def activity3 = createActivityDetail(TIME3)

		timeline.addActivityDetail(activity1)
		timeline.addGenericBand(createGenericBand(BandType.learning, TIME2, TIME3))
		timeline.addActivityDetail(activity2)
		timeline.addActivityDetail(activity3)

		timelineDetail.initializeActiveBandTypes()

		assert activity1.activeBandType == null
		assert activity2.activeBandType == BandType.learning
		assert activity3.activeBandType == null
	}

	@Test
	void initializeActiveBandTypes_ShouldPrioritizeConflicts_IfOverlappingBands() {
		def activity = createActivityDetail(TIME1)
		timeline.addActivityDetail(activity)
		timeline.addConflictBand(createConflictBand(TIME1, TIME2))
		timeline.addGenericBand(createGenericBand(BandType.learning, TIME1, TIME2))

		timelineDetail.initializeActiveBandTypes()

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
		band.bandStart = new BandStart(bandType)
		band.bandEnd = new BandEnd(bandType)
		band.setStartPosition(start)
		band.setEndPosition(end)
		return band
	}

	private ActivityDetail createActivityDetail(TimePosition time) {
		new ActivityDetail(time, new EditorActivity(time.actualTime, 'file', 10))
	}
}
