package com.ideaflow.event

import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import org.joda.time.DateTimeUtils
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestEventToEditorActivityHandler extends GroovyTestCase {

	private static final int PERSISTABLE_ACTIVITY_DURATION = EventToEditorActivityHandler.SHORTEST_ACTIVITY + 1
	private static final long PERSISTABLE_ACTIVITY_DURATION_MILLIS = PERSISTABLE_ACTIVITY_DURATION * 1000
	private static final int DOES_NOT_PERSIST_ACTIVITY_DURATION = EventToEditorActivityHandler.SHORTEST_ACTIVITY - 1
	private static final long DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS = DOES_NOT_PERSIST_ACTIVITY_DURATION * 1000

	EventToEditorActivityHandler eventHandler
	IdeaFlowModel model

	void setUp() {
		DateTimeUtils.setCurrentMillisFixed(NOW)

		model = new IdeaFlowModel(new File('test'), null)
		eventHandler = new EventToEditorActivityHandler(model)
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfNoPriorEvent() {
		eventHandler.startEvent(FILE)
		assert 0 == model.size()
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfSameEvent() {

		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.startEvent(FILE)

		assert 0 == model.size()
	}

	void testStartEvent_ShouldCreateEditorActivity_IfDifferentEvent() {

		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.startEvent(OTHER_FILE)

		assert 1 == model.size()

		EditorActivity editorActivity = getEditorActivity(0)
		int seconds = LONG_DELAY / 1000

		assert FILE == editorActivity.name
		assert seconds == editorActivity.duration
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfShortDelay() {
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + SHORT_DELAY)
		eventHandler.startEvent(OTHER_FILE)

		assert 0 == model.size()
	}

	void testStartEvent_ShouldEndCurrentEvent_IfNull() {
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.startEvent(null)

		assert 1 == model.size()
	}

	void testEndEvent_ShouldEndCurrentEvent_IfSameEvent() {
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(FILE)

		assert 1 == model.size()

		EditorActivity editorActivity = getEditorActivity(0)
		int seconds = LONG_DELAY / 1000

		assert FILE == editorActivity.name
		assert seconds == editorActivity.duration
	}

	void testEndEvent_ShouldNotEndCurrentEvent_IfDifferentEvent() {
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(OTHER_FILE)

		assert 0 == model.size()
	}

	void testEndEvent_ShouldEndCurrentEvent_IfNull() {
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(null)

		assert 1 == model.size()
	}

	void testEndEvent_ShouldNotCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedNotCalled() {
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.endEvent(null)

		assert 1 == model.size()
		assert getEditorActivity(0).modified == false
	}

	void testEndEvent_ShouldCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedCalled() {
		eventHandler.startEvent(FILE)
		DateTimeUtils.setCurrentMillisFixed(NOW + LONG_DELAY)
		eventHandler.activeEventModified()
		eventHandler.endEvent(null)

		assert 1 == model.size()
		assert getEditorActivity(0).modified
	}

	void testDuplicateEvents_ShouldIncrementDurationOnExistingEditorActivityAndNotCreateNewActivity_IfShortActivityComesBetweenTwoSameActivities() {
		long currentTime = NOW

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

		assert 1 == model.size()
		assert getEditorActivity(0).duration == PERSISTABLE_ACTIVITY_DURATION * 2
	}

	void testDuplicateEvents_ShouldCreateNewEvent_IfShortActivityComesBetweenTwoActivitiesWithSameNameButDifferentInModifiedState() {
		long currentTime = NOW

		eventHandler.startEvent(FILE1)
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE2)
		currentTime += DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE1)
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.activeEventModified()
		eventHandler.endEvent()

		assert 2 == model.size()
		assert getEditorActivity(0).duration == PERSISTABLE_ACTIVITY_DURATION
		assert getEditorActivity(1).duration == PERSISTABLE_ACTIVITY_DURATION
	}

	private EditorActivity getEditorActivity(int index) {
		model.entityList.get(index)
	}

}
