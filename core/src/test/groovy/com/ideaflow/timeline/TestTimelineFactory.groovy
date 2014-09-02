package com.ideaflow.timeline

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Idle
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import spock.lang.Specification
import test.support.FixtureSupport
import test.support.IdeaFlowModelBuilder

@Mixin(FixtureSupport)
class TestTimelineFactory extends Specification {

	TimelineFactory timelineFactory = new TimelineFactory()

	void testCreate_ShouldCreateConflictBands() {
        given:
		Conflict conflict = createConflict(TIME1)
		Resolution resolution = createResolution(TIME2)
		IdeaFlowModel ifm = IdeaFlowModelBuilder.create().defaults()
				.addEditorActivity(10)
				.addConflict(conflict)
				.addEditorActivity(15)
				.addResolution(resolution)
				.build()

        when:
		Timeline timeline = timelineFactory.create(ifm)

        then:
		ConflictBand conflictBand = firstAndOnlyConflict(timeline)
		assert conflictBand.conflict == conflict
		assert conflictBand.resolution == resolution
		assert conflictBand.startPosition == new TimePosition(conflict.created, 10)
		assert conflictBand.endPosition == new TimePosition(resolution.created, 25)
		assert conflictBand.duration == new TimeDuration(15)
	}

	ConflictBand firstAndOnlyConflict(Timeline timeline) {
		assert timeline.conflictBands.size() == 1
		return timeline.conflictBands[0]
	}

	void testCreate_ShouldCreateGenericBands() {
        given:
		BandStart bandStart = createBandStart()
		BandEnd bandEnd = createBandEnd()
		IdeaFlowModel ifm = IdeaFlowModelBuilder.create().defaults()
				.addEditorActivity(10)
				.addBandStart(bandStart)
				.addEditorActivity(15)
				.addBandEnd(bandEnd)
				.build()

        when:
		Timeline timeline = timelineFactory.create(ifm)

        then:
		GenericBand genericBand = firstAndOnlyGenericBand(timeline)
		assert genericBand.bandStart == bandStart
		assert genericBand.bandEnd == bandEnd
		assert genericBand.startPosition == new TimePosition(bandStart.created, 10)
		assert genericBand.endPosition == new TimePosition(bandEnd.created, 25)
		assert genericBand.duration == new TimeDuration(15)
	}

	private GenericBand firstAndOnlyGenericBand(Timeline timeline) {
		assert timeline.genericBands.size() == 1
		return timeline.genericBands[0]
	}

	void testCreate_ShouldCreateEvents() {
        given:
		Note note = createNote()
		IdeaFlowModel ifm = IdeaFlowModelBuilder.create().defaults()
				.addEditorActivity(10)
				.addNote(note)
				.build()

        when:
		Timeline timeline = timelineFactory.create(ifm)

        then:
		Event event = firstAndOnlyEvent(timeline)
		assert event.note == note
		assert event.time.relativeOffset == 10
	}

	private Event firstAndOnlyEvent(Timeline timeline) {
		assert timeline.events.size() == 1
		return timeline.events[0]
	}

	void testCreate_ShouldCreateActivityDetail() {
        given:
		EditorActivity editorActivity1 = createEditorActivity(FILE1, 10, TIME1)
		EditorActivity editorActivity2 = createEditorActivity(FILE2, 15, TIME2)
		IdeaFlowModel ifm = IdeaFlowModelBuilder.create().defaults()
				.addEditorActivity(editorActivity1)
				.addEditorActivity(editorActivity2)
				.build()

        when:
		Timeline timeline = timelineFactory.create(ifm)

        then:
		ActivityDetail activityDetail1 = timeline.activityDetails[0]
		assert activityDetail1.editorActivity == editorActivity1
		assert activityDetail1.time.relativeOffset == 0
		ActivityDetail activityDetail2 = timeline.activityDetails[1]
		assert activityDetail2.editorActivity == editorActivity2
		assert activityDetail2.time.relativeOffset == 10
		assert timeline.activityDetails.size() == 2
	}

	void testCreate_ShouldCreateIdleActivity() {
		given:
		Idle idle = createIdle()
		IdeaFlowModel ifm = IdeaFlowModelBuilder.create().defaults()
			.addIdle(idle)
			.build()

		when:
		Timeline timeline = timelineFactory.create(ifm)

		then:
		IdleDetail idleDetail = timeline.idleDetails[0]
		assert idleDetail.idle == idle
	}

	void testCreate_StateChangeShouldNotExplode() {
        given:
		IdeaFlowModel ifm = IdeaFlowModelBuilder.create().defaults()
				.addStateChange(createStateChange())
				.build()

        expect:
		timelineFactory.create(ifm)
	}

	def "should create time band container if generic band contains conflict"() {
		given:
		Timeline timeline = new Timeline()
		GenericBand containerBand = createGenericBand(TIME1_POSITION, TIME4_POSITION)
		ConflictBand internalConflict = createConflictBand(TIME2_POSITION, TIME3_POSITION)
		timeline.addGenericBand(containerBand)
		timeline.addConflictBand(internalConflict)

		when:
		new TimelineFactory.TimelineTimeBandContainerBuilder().addTimeBandContainersToTimeline(timeline)

		then:
		TimeBandContainer timeBandContainer = timeline.timeBandContainers[0]
		assert timeBandContainer
		assert timeBandContainer.primaryConflict == null
		assert timeBandContainer.primaryGenericBand == containerBand
		assert timeBandContainer.timeBands.contains(timeBandContainer.primaryGenericBand)
		assert timeBandContainer.timeBands.contains(internalConflict)
		assert timeBandContainer.timeBands.size() == 2
		assert timeline.timeBandContainers.size() == 1
	}

	def "should create time band container with linked conflict"() {
		given:
		Timeline timeline = new Timeline()
		ConflictBand initialConflict = createConflictBand(TIME1_POSITION)
		GenericBand containerBand = createGenericBand(TIME2_POSITION, TIME4_POSITION)
		ConflictBand internalConflict = createConflictBand(TIME3_POSITION)
		containerBand.bandStart.isLinkedToPreviousConflict = true
		timeline.addConflictBand(initialConflict)
		timeline.addGenericBand(containerBand)
		timeline.addConflictBand(internalConflict)

		when:
		new TimelineFactory.TimelineTimeBandContainerBuilder().addTimeBandContainersToTimeline(timeline)

		then:
		TimeBandContainer timeBandContainer = timeline.timeBandContainers[0]
		assert timeBandContainer
		assert timeBandContainer.primaryConflict == initialConflict
		assert timeBandContainer.primaryGenericBand == containerBand
		assert timeBandContainer.timeBands.contains(timeBandContainer.primaryGenericBand)
		assert timeBandContainer.timeBands.contains(timeBandContainer.primaryConflict)
		assert timeBandContainer.timeBands.contains(internalConflict)
		assert timeBandContainer.timeBands.size() == 3
		assert timeline.timeBandContainers.size() == 1
	}

	def "should not create time band container if generic band contains no conflicts"() {
		given:
		Timeline timeline = new Timeline()
		GenericBand containerBand = createGenericBand(TIME1_POSITION, TIME2_POSITION)
		timeline.addGenericBand(containerBand)

		when:
		new TimelineFactory.TimelineTimeBandContainerBuilder().addTimeBandContainersToTimeline(timeline)

		then:
		assert timeline.timeBandContainers.isEmpty()
	}

	def "should fail if generic band contains another generic band"() {
		given:
		Timeline timeline = new Timeline()
		GenericBand containerBand = createGenericBand(TIME1_POSITION, TIME4_POSITION)
		GenericBand internalBand = createGenericBand(TIME2_POSITION)
		timeline.addGenericBand(containerBand)
		timeline.addGenericBand(internalBand)

		when:
		new TimelineFactory.TimelineTimeBandContainerBuilder().addTimeBandContainersToTimeline(timeline)

		then:
		thrown(RuntimeException)
	}

	def "should not include conflict in container if started within container band but ended outside"() {
		given:
		Timeline timeline = new Timeline()
		GenericBand containerBand = createGenericBand(TIME1_POSITION, TIME3_POSITION)
		ConflictBand internalConflict = createConflictBand(TIME2_POSITION, TIME4_POSITION)
		timeline.addGenericBand(containerBand)
		timeline.addConflictBand(internalConflict)

		when:
		new TimelineFactory.TimelineTimeBandContainerBuilder().addTimeBandContainersToTimeline(timeline)

		then:
		timeline.timeBandContainers.isEmpty()
	}

}
