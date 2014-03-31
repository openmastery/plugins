package com.ideaflow.timeline

import com.ideaflow.model.IdeaFlowModel
import org.junit.Before
import org.junit.Test
import test.support.IdeaFlowModelBuilder

class TestTimelineFactory {

	TimelineFactory timelineFactory

	@Before
	void setUp() {
		timelineFactory = new TimelineFactory()
	}

	@Test
	void testCreate_ShouldCreateConflictBands() {
		IdeaFlowModel ifm = IdeaFlowModelBuilder.create().defaults()
				.addConflict()
				.addResolution()
				.build()

		Timeline timeline = timelineFactory.create(ifm)
		ConflictBand conflict = firstConflict(timeline)

		//TODO this needs asserts!
	}

	ConflictBand firstConflict(Timeline timeline) {
		assert timeline.conflictBands.size() == 1
		return timeline.conflictBands[0]
	}

}
