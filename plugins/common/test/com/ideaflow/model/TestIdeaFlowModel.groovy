package com.ideaflow.model

import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIdeaFlowModel extends GroovyTestCase {

    IdeaFlowModel model


    void setUp() {
        model = new IdeaFlowModel('test', null)
    }

    void testAddEvent_ShouldNotAdd_IfPaused() {

        model.isPaused = true
        model.addTimelineEvent(createEvent(EventType.startConflict, NOW))

        assert model.size() == 0
        assert model.isOpenConflict() == false
    }

    void testAddInterval_ShouldNotAdd_IfPaused() {
        model.isPaused = true
        model.addInterval(createInterval('test', NOW))

        assert model.size() == 0
    }

    void testAddEvent_ShouldToggleOpenConflict() {
        assert model.isOpenConflict() == false

        model.addTimelineEvent(createEvent(EventType.startConflict, TIME1))
        assert model.isOpenConflict() == true

        model.addTimelineEvent(createEvent(EventType.endConflict, TIME2))
        assert model.isOpenConflict() == false
    }


}
