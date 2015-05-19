package com.ideaflow.event

import com.ideaflow.model.entry.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import org.joda.time.DateTimeUtils
import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestEventToEditorActivityHandler extends Specification {

	private static final int PERSISTABLE_ACTIVITY_DURATION = EventToEditorActivityHandler.SHORTEST_ACTIVITY + 1
	private static final long PERSISTABLE_ACTIVITY_DURATION_MILLIS = PERSISTABLE_ACTIVITY_DURATION * 1000
	private static final int DOES_NOT_PERSIST_ACTIVITY_DURATION = EventToEditorActivityHandler.SHORTEST_ACTIVITY - 1
	private static final long DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS = DOES_NOT_PERSIST_ACTIVITY_DURATION * 1000

	EventToEditorActivityHandler eventHandler
	IdeaFlowModel model

	void setup() {
		DateTimeUtils.setCurrentMillisFixed(NOW)

		model = new IdeaFlowModel(new File('test'), null)
		eventHandler = new EventToEditorActivityHandler(model)
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfNoPriorEvent() {
        when:
		eventHandler.startEvent(FILE)

        then:
		assert 0 == model.size()
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfSameEvent() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.startEvent(FILE)

        then:
		assert 0 == model.size()
	}

	void testStartEvent_ShouldCreateEditorActivity_IfDifferentEvent() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.startEvent(OTHER_FILE)

        then:
		assert 1 == model.size()

        when:
		EditorActivity editorActivity = getEditorActivity(0)
		int seconds = LONG_DELAY / 1000

        then:
		assert FILE == editorActivity.name
		assert seconds == editorActivity.duration
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfShortDelay() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + SHORT_DELAY)
		eventHandler.startEvent(OTHER_FILE)

        then:
		assert 0 == model.size()
	}

	void testStartEvent_ShouldEndCurrentEvent_IfNull() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.startEvent(null)

        then:
		assert 1 == model.size()
	}

	void testEndEvent_ShouldEndCurrentEvent_IfSameEvent() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(FILE)

        then:
		assert 1 == model.size()

        when:
		EditorActivity editorActivity = getEditorActivity(0)
		int seconds = LONG_DELAY / 1000

        then:
		assert FILE == editorActivity.name
		assert seconds == editorActivity.duration
	}

	void testEndEvent_ShouldNotEndCurrentEvent_IfDifferentEvent() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(OTHER_FILE)

        then:
		assert 0 == model.size()
	}

	void testEndEvent_ShouldEndCurrentEvent_IfNull() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(null)

        then:
		assert 1 == model.size()
	}

	void testEndEvent_ShouldNotCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedNotCalled() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(null)

        then:
		assert 1 == model.size()
		assert getEditorActivity(0).modified == false
	}

	void testEndEvent_ShouldCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedCalled() {
        when:
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.activeEventModified(FILE)
		eventHandler.endEvent(null)

        then:
		assert 1 == model.size()
		assert getEditorActivity(0).modified
	}

	void testDuplicateEvents_ShouldIncrementDurationOnExistingEditorActivityAndNotCreateNewActivity_IfShortActivityComesBetweenTwoSameActivities() {
        given:
		long currentTime = NOW

        when:
		eventHandler.startEvent(FILE1)
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE2)
		currentTime += DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE1)
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.endEvent()

        then:
		assert 1 == model.size()
		assert getEditorActivity(0).duration == PERSISTABLE_ACTIVITY_DURATION * 2
	}

	void testDuplicateEvents_ShouldCreateNewEvent_IfShortActivityComesBetweenTwoActivitiesWithSameNameButDifferentInModifiedState() {
        given:
		long currentTime = NOW

        when:
		eventHandler.startEvent(FILE1)
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE2)
		currentTime += DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE1)
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.activeEventModified(FILE1)
		eventHandler.endEvent()

        then:
		assert 2 == model.size()
		assert getEditorActivity(0).duration == PERSISTABLE_ACTIVITY_DURATION
		assert getEditorActivity(1).duration == PERSISTABLE_ACTIVITY_DURATION
	}

	private EditorActivity getEditorActivity(int index) {
		model.entryList.get(index)
	}

}
