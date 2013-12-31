package com.ideaflow.model

import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIdeaFlowModel extends GroovyTestCase {

    IdeaFlowModel model


    void setUp() {
        model = new IdeaFlowModel('test', new Date(NOW))
    }

    void testAddEvent_ShouldNotAdd_IfPaused() {

        model.isPaused = true
        model.addEvent(createEvent(EventType.startConflict, NOW))

        assert model.size() == 0
        assert model.isOpenConflict() == false
    }

    void testAddInterval_ShouldNotAdd_IfPaused() {
        model.isPaused = true
        model.addInterval(createInterval('test', NOW))

        assert model.size() == 0
    }

    void testCreateSequencedTimeline_ShouldSortEventsFirst_IfSameTime() {

        model.addInterval(createInterval(FILE, 5, TIME1))
        model.addEvent(createEvent(EventType.note, TIME1))

        Timeline timeline = model.createSequencedTimeline()

        assert timeline.timelineEnd == 5
        assert timeline.events.get(0).type == EventType.note
    }

    void testCreateSequencedTimeline_ShouldSortEvents() {
        model.addInterval(createInterval(FILE2, 3, TIME2))
        model.addInterval(createInterval(FILE3, 3, TIME3))
        model.addInterval(createInterval(FILE4, 3, TIME4))
        model.addInterval(createInterval(FILE1, 3, TIME1))

        Timeline timeline = model.createSequencedTimeline()

        assert timeline.size() == 4
        assert timeline.events.get(0).text == FILE1
        assert timeline.events.get(1).text == FILE2
        assert timeline.events.get(2).text == FILE3
        assert timeline.events.get(3).text == FILE4

    }

    void testAddEvent_ShouldToggleOpenConflict() {
        assert model.isOpenConflict() == false

        model.addEvent(createEvent(EventType.startConflict, TIME1))
        assert model.isOpenConflict() == true

        model.addEvent(createEvent(EventType.endConflict, TIME2))
        assert model.isOpenConflict() == false
    }


}
