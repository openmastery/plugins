package com.ideaflow.event

import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Interval
import com.ideaflow.model.TimeService
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestEventToIntervalHandler extends GroovyTestCase {

    EventToIntervalHandler eventHandler
    IdeaFlowModel model
    def timeService
    def time

    void setUp() {
        timeService = [getTime: { time }] as TimeService
        time = NOW

        model = new IdeaFlowModel('test', null)
        eventHandler = new EventToIntervalHandler(timeService, model)
    }

    void testStartEvent_ShouldNotCreateInterval_IfNoPriorEvent() {
        eventHandler.startEvent(FILE)
        assert 0 == model.size()
    }

    void testStartEvent_ShouldNotCreateInterval_IfSameEvent() {

        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.startEvent(FILE)

        assert 0 == model.size()
    }

    void testStartEvent_ShouldCreateInterval_IfDifferentEvent() {

        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.startEvent(OTHER_FILE)

        assert 1 == model.size()

        Interval interval = getInterval(0)
        int seconds = LONG_DELAY / 1000

        assert FILE == interval.name
        assert seconds == interval.duration
    }

    void testStartEvent_ShouldNotCreateInterval_IfShortDelay() {
        eventHandler.startEvent(FILE)
        time = NOW + SHORT_DELAY
        eventHandler.startEvent(OTHER_FILE)

        assert 0 == model.size()
    }

    void testStartEvent_ShouldEndCurrentEvent_IfNull() {
        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.startEvent(null)

        assert 1 == model.size()
    }

    void testEndEvent_ShouldEndCurrentEvent_IfSameEvent() {
        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.endEvent(FILE)

        assert 1 == model.size()

        Interval interval = getInterval(0)
        int seconds = LONG_DELAY / 1000

        assert FILE == interval.name
        assert seconds == interval.duration
    }

    void testEndEvent_ShouldNotEndCurrentEvent_IfDifferentEvent() {
        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.endEvent(OTHER_FILE)

        assert 0 == model.size()

    }

    void testEndEvent_ShouldEndCurrentEvent_IfNull() {
        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.endEvent(null)

        assert 1 == model.size()
    }

    private Interval getInterval(int index) {
        model.itemList.get(index)
    }

}
