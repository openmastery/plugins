package com.ideaflow.event

import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.TimeService
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestEventToEditorActivityHandler extends GroovyTestCase {

    EventToEditorActivityHandler eventHandler
    IdeaFlowModel model
    def timeService
    def time

    void setUp() {
        timeService = [getTime: { time }] as TimeService
        time = NOW

        model = new IdeaFlowModel('test', null)
        eventHandler = new EventToEditorActivityHandler(timeService, model)
    }

    void testStartEvent_ShouldNotCreateEditorActivity_IfNoPriorEvent() {
        eventHandler.startEvent(FILE)
        assert 0 == model.size()
    }

    void testStartEvent_ShouldNotCreateEditorActivity_IfSameEvent() {

        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.startEvent(FILE)

        assert 0 == model.size()
    }

    void testStartEvent_ShouldCreateEditorActivity_IfDifferentEvent() {

        eventHandler.startEvent(FILE)
        time = NOW + LONG_DELAY
        eventHandler.startEvent(OTHER_FILE)

        assert 1 == model.size()

        EditorActivity editorActivity = getEditorActivity(0)
        int seconds = LONG_DELAY / 1000

        assert FILE == editorActivity.name
        assert seconds == editorActivity.duration
    }

    void testStartEvent_ShouldNotCreateEditorActivity_IfShortDelay() {
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

        EditorActivity editorActivity = getEditorActivity(0)
        int seconds = LONG_DELAY / 1000

        assert FILE == editorActivity.name
        assert seconds == editorActivity.duration
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

    private EditorActivity getEditorActivity(int index) {
        model.entityList.get(index)
    }

}
