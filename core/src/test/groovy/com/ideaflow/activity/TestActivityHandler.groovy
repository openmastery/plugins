package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.DateTimeUtils
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.task.Task
import spock.lang.Ignore
import spock.lang.Specification

class TestActivityHandler extends Specification {

	private static final long NOW = System.currentTimeMillis()
	private static final long PERSISTABLE_ACTIVITY_DURATION = ActivityHandler.SHORTEST_ACTIVITY + 1
	private static final long PERSISTABLE_ACTIVITY_DURATION_MILLIS = PERSISTABLE_ACTIVITY_DURATION * 1000
	private static final long DOES_NOT_PERSIST_ACTIVITY_DURATION = ActivityHandler.SHORTEST_ACTIVITY - 1
	private static final long DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS = DOES_NOT_PERSIST_ACTIVITY_DURATION * 1000

	ActivityHandler handler
	InMemoryMessageLogger messageLogger
	IFMController controller = Mock(IFMController)

	void setup() {
		DateTimeUtils.setCurrentMillisFixed(NOW)

		messageLogger = new InMemoryMessageLogger()
		MessageQueue activityQueue = new MessageQueue(controller, messageLogger)
		handler = new ActivityHandler(controller, activityQueue)

		controller.getActiveTask() >> new Task(id: 1)
		controller.isRecording() >> true
	}

	void cleanup() {
		DateTimeUtils.setCurrentMillisSystem()
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfNoPriorEvent() {
		when:
		handler.startFileEvent("file")

		then:
		assertNoMessages()
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfSameEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent("file")

		then:
		assertNoMessages()
	}

	void testStartEvent_ShouldCreateEditorActivity_IfDifferentEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent("other")

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file"
		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assertMessageCount(1)
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfShortDelay() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent("other")

		then:
		assertNoMessages()
	}

	void testStartEvent_ShouldEndCurrentEvent_IfNull() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file"
		assertMessageCount(1)
	}

	void testEndEvent_ShouldEndCurrentEvent_IfSameEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent("file")

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file"
		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assertMessageCount(1)
	}

	void testEndEvent_ShouldNotEndCurrentEvent_IfDifferentEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent("other")

		then:
		assertNoMessages()
	}

	void testEndEvent_ShouldEndCurrentEvent_IfNull() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent(null)

		then:
		assertMessageCount(1)
	}

	void testEndEvent_ShouldNotCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedNotCalled() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).modified == false
		assertMessageCount(1)
	}

	void testEndEvent_ShouldCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedCalled() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.fileModified("file")
		handler.endFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).modified == true
		assertMessageCount(1)
	}

	void testPushModificationActivity_ShouldCountModifications() {
		when:
		handler.fileModified("file")
		handler.fileModified("file")
		handler.fileModified("file")
		handler.pushModificationActivity(30)
		then:
		assert getMessage(0, NewModificationActivity).modificationCount == 3
	}

	void testMarkProcessExecution_ShouldPublishActivity_AfterStartStop() {
		when:
		handler.markProcessStarting(3, "TestMyUnit", "JUnit", true)
		handler.markProcessEnding(3, -12)
		then:
		assert getMessage(0, NewExecutionActivity).processName == "TestMyUnit"
		assert getMessage(0, NewExecutionActivity).executionTaskType == "JUnit"
		assert getMessage(0, NewExecutionActivity).exitCode == -12
		assert getMessage(0, NewExecutionActivity).isDebug() == true

	}


	// TODO: the previous implementation held onto the active event, which made it possible to adjust the prior event
	// this is not possible with the current implementation since the events could be published at any point... this means
	// we could be sending contiguous events of the same name to the server - probably need to account for this on the
	// server side - could also account for this in the timeline and just collapse events there
	@Ignore
	void testDuplicateEvents_ShouldIncrementDurationOnExistingEditorActivityAndNotCreateNewActivity_IfShortActivityComesBetweenTwoSameActivities() {
		given:
		long currentTime = NOW

		when:
		handler.startFileEvent("file1")
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		handler.startFileEvent("file2")
		currentTime += DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		handler.startFileEvent("file3")
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		handler.endFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file1"
		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION * 2
		assertMessageCount(1)
	}

	void testDuplicateEvents_ShouldCreateNewEvent_IfShortActivityComesBetweenTwoActivitiesWithSameNameButDifferentInModifiedState() {
		given:
		long currentTime = NOW

		when:
		handler.startFileEvent("file1")
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		handler.startFileEvent("file2")
		currentTime += DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		handler.startFileEvent("file3")
		currentTime += PERSISTABLE_ACTIVITY_DURATION_MILLIS
		DateTimeUtils.setCurrentMillisFixed(currentTime)
		handler.fileModified("file1")
		handler.endFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file1"
		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assert getMessage(1, NewEditorActivity).filePath == "file3"
		assert getMessage(1, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assertMessageCount(2)
	}


	private void assertNoMessages() {
		messageLogger.messages.size() == 0
	}

	private void assertMessageCount(int expectedSize) {
		messageLogger.messages.size() == expectedSize
	}

	private <T> T getMessage(int index, Class<T> clazz) {
		assert messageLogger.messages.size() > index
		(T)messageLogger.messages[index]
	}


}
