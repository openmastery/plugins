package com.ideaflow.event

import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import org.joda.time.DateTimeUtils
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestEventToEditorActivityHandler extends GroovyTestCase {

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

	void testEndEvent_ShouldIncrementDurationOnExistingEditorActivityAndNotCreateNewActivity() {
		int activityLengthLongEnoughToTriggerEvent = EventToEditorActivityHandler.SHORTEST_ACTIVITY + 1
		int activityLengthTooShortToTriggerEvent = EventToEditorActivityHandler.SHORTEST_ACTIVITY - 1
		long currentTime = NOW

		eventHandler.startEvent(FILE1)
		currentTime += activityLengthLongEnoughToTriggerEvent * 1000
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE2)
		currentTime += activityLengthTooShortToTriggerEvent * 1000
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.startEvent(FILE1)
		currentTime += activityLengthLongEnoughToTriggerEvent * 1000
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		eventHandler.endEvent()

		assert 1 == model.size()
		assert getEditorActivity(0).duration == activityLengthLongEnoughToTriggerEvent * 2
	}

	private EditorActivity getEditorActivity(int index) {
		model.entityList.get(index)
	}

}
