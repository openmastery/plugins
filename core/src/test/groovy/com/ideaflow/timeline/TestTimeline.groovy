package com.ideaflow.timeline

import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestTimeline extends Specification {

	Timeline timeline = new Timeline()

	void getTimebands_ShouldContainAllBandTypes() {
        given:
		TimeBand genericBand = createGenericBand()
		TimeBand conflictBand = createConflictBand()

        when:
		timeline.addGenericBand(genericBand)
		timeline.addConflictBand(conflictBand)

        then:
		assert timeline.timeBands.contains(genericBand)
		assert timeline.timeBands.contains(conflictBand)
	}

	void getTimebands_ShouldSortBandTypesByStartTime() {
        given:
		TimeBand time1_genericBand = createGenericBand(TIME1_POSITION)
		TimeBand time2_conflictBand = createConflictBand(TIME2_POSITION)
		TimeBand time3_genericBand = createGenericBand(TIME3_POSITION)
		TimeBand time4_conflictBand = createConflictBand(TIME4_POSITION)

        when:
		timeline.addGenericBand(time3_genericBand)
		timeline.addGenericBand(time1_genericBand)

		timeline.addConflictBand(time4_conflictBand)
		timeline.addConflictBand(time2_conflictBand)

        then:
		assert timeline.timeBands[0] == time1_genericBand
		assert timeline.timeBands[1] == time2_conflictBand
		assert timeline.timeBands[2] == time3_genericBand
		assert timeline.timeBands[3] == time4_conflictBand

	}

	void getSequencedTimelineDetail_ShouldSortBandsBeforeDetail_IfStartSameTime() {
        given:
		def activityDetail = createActivityDetail(TIME1_POSITION)
		def conflictBand = createConflictBand(TIME1_POSITION)
		def activityDetail2 = createActivityDetail(TIME2_POSITION)
		def conflictBand2 = createConflictBand(TIME2_POSITION)

        when:
		timeline.addActivityDetail(activityDetail)
		timeline.addConflictBand(conflictBand)
		timeline.addConflictBand(conflictBand2)
		timeline.addActivityDetail(activityDetail2)

        then:
		assert timeline.sequencedTimelineDetail[0] == conflictBand
		assert timeline.sequencedTimelineDetail[1] == activityDetail
		assert timeline.sequencedTimelineDetail[2] == conflictBand2
		assert timeline.sequencedTimelineDetail[3] == activityDetail2
	}
}
