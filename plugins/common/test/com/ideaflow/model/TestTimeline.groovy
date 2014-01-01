package com.ideaflow.model

import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestTimeline extends GroovyTestCase {

    Timeline timeline

    void setUp() {
        timeline = new Timeline()
    }

    void testAddInterval_ShouldCollapseSimilarEvents() {
        timeline.addInterval(FILE, 5)
        timeline.addInterval(FILE, 10)

        assert timeline.size() == 1
        assert timeline.timelineEnd == 15

        def event = timeline.events.get(0)
        assert event.text == FILE
        assert event.duration == 15
        assert event.eventTime == 0
    }

    void testAddInterval_ShouldNotCollapseDifferentEvents() {
        timeline.addInterval(FILE, 5)
        timeline.addInterval(OTHER_FILE, 10)

        assert timeline.size() == 2
        def event1 = timeline.events.get(0)
        def event2 = timeline.events.get(1)

        assert event1.text == FILE
        assert event1.duration == 5
        assert event1.eventTime == 0

        assert event2.text == OTHER_FILE
        assert event2.duration == 10
        assert event2.eventTime == 5
    }

    void testAddInterval_ShouldMarkConflict_IfConflictStarted() {
        timeline.addEvent('conflict', EventType.startConflict)
        timeline.addInterval(FILE, 5)
        timeline.addEvent('conflict', EventType.endConflict)
        timeline.addInterval(FILE, 10)

        assert timeline.size() == 4

        assert timeline.events.get(1).text == FILE
        assert timeline.events.get(1).conflict == true

        assert timeline.events.get(3).text == FILE
        assert timeline.events.get(3).conflict == false
    }

    void testAddEvent_ShouldNotAdd_IfNotFlowEvent() {
        timeline.addEvent('ignore', EventType.open)
        timeline.addEvent('test', EventType.note)

        assert timeline.size() == 1
    }

    void testGroupByConflict_ShouldSummarizeConflict() {

        timeline.addEvent('start', EventType.startConflict)
        timeline.addInterval(FILE, 5)
        timeline.addInterval(OTHER_FILE, 10)
        timeline.addEvent('end', EventType.endConflict)

        def conflicts = timeline.groupByConflict()

        assert conflicts.size() == 1

        Timeline.Conflict conflict = conflicts.get(0)
        assert conflict.conflictNote == 'start'
        assert conflict.resolveNote == 'end'
        assert conflict.duration == 15
    }

    void testGroupByConflict_ShouldCreateForEachConflict() {

        timeline.addEvent('start', EventType.startConflict)
        timeline.addInterval(FILE, 5)
        timeline.addEvent('end', EventType.endConflict)

        timeline.addEvent('start', EventType.startConflict)
        timeline.addInterval(FILE, 10)
        timeline.addEvent('end', EventType.endConflict)

        def conflicts = timeline.groupByConflict()

        assert conflicts.size() == 2
        assert conflicts.get(0).duration == 5
        assert conflicts.get(1).duration == 10

    }

    void testGetTotalNonConflict_ShouldSumTimeOutsideOfConflict() {

        timeline.addInterval(FILE, 1)

        timeline.addEvent('start', EventType.startConflict)
        timeline.addInterval(FILE, 5)
        timeline.addEvent('end', EventType.endConflict)

        timeline.addInterval(FILE, 50)

        assert 51 == timeline.getTotalNonConflict()
    }

    void testGroupByIntervalName_ShouldSumBySameType() {

        timeline.addInterval(FILE, 1)
        timeline.addInterval(OTHER_FILE, 7)
        timeline.addInterval(FILE, 50)
        timeline.addInterval(OTHER_FILE, 7)

       def groups = timeline.groupByIntervalName()

        assert groups.get(FILE) == 51
        assert groups.get(OTHER_FILE) == 14
    }



}
