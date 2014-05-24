package com.ideaflow.timeline

import org.junit.Before
import org.junit.Test
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestTimeline {

	Timeline timeline

	@Before
	void setup() {
		timeline = new Timeline()
	}

	@Test
	void getTimebands_ShouldContainAllBandTypes() {
		TimeBand genericBand = createGenericBand()
		TimeBand conflictBand = createConflictBand()

		timeline.addGenericBand(genericBand)
		timeline.addConflictBand(conflictBand)

		assert timeline.timeBands.contains(genericBand)
		assert timeline.timeBands.contains(conflictBand)
	}

	@Test
	void getTimebands_ShouldSortBandTypesByStartTime() {
		TimeBand time1_genericBand = createGenericBand(TIME1_POSITION)
		TimeBand time2_conflictBand = createConflictBand(TIME2_POSITION)
		TimeBand time3_genericBand = createGenericBand(TIME3_POSITION)
		TimeBand time4_conflictBand = createConflictBand(TIME4_POSITION)

		timeline.addGenericBand(time3_genericBand)
		timeline.addGenericBand(time1_genericBand)

		timeline.addConflictBand(time4_conflictBand)
		timeline.addConflictBand(time2_conflictBand)

		assert timeline.timeBands[0] == time1_genericBand
		assert timeline.timeBands[1] == time2_conflictBand
		assert timeline.timeBands[2] == time3_genericBand
		assert timeline.timeBands[3] == time4_conflictBand

	}
}
