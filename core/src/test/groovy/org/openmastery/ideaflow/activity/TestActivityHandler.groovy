package org.openmastery.ideaflow.activity

import org.openmastery.ideaflow.controller.IFMController
import org.openmastery.ideaflow.state.TaskState
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.time.MockTimeService
import spock.lang.Ignore
import spock.lang.Specification

class TestActivityHandler extends Specification {

	private static final int  PERSISTABLE_ACTIVITY_DURATION_SECONDS = ActivityHandler.SHORTEST_ACTIVITY + 1
	private static final int DOES_NOT_PERSIST_ACTIVITY_DURATION_SECONDS = ActivityHandler.SHORTEST_ACTIVITY - 1

	ActivityHandler handler
	InMemoryMessageLogger messageLogger
	IFMController controller = Mock(IFMController)
	MockTimeService timeService = new MockTimeService()

	void setup() {
		messageLogger = new InMemoryMessageLogger()
		MessageQueue activityQueue = new MessageQueue(controller, messageLogger, timeService)
		handler = new ActivityHandler(controller, activityQueue, timeService)

		controller.getActiveTask() >> new TaskState(id: 1)
		controller.isRecording() >> true
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
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent("file")

		then:
		assertNoMessages()
	}

	void testStartEvent_ShouldCreateEditorActivity_IfDifferentEvent() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent("other")

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file"

		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION_SECONDS
		assertMessageCount(1)
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfShortDelay() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(DOES_NOT_PERSIST_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent("other")

		then:
		assertNoMessages()
	}

	void testStartEvent_ShouldEndCurrentEvent_IfNull() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file"
		assertMessageCount(1)
	}

	void testEndEvent_ShouldEndCurrentEvent_IfSameEvent() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.endFileEvent("file")

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file"
		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION_SECONDS
		assertMessageCount(1)
	}

	void testEndEvent_ShouldNotEndCurrentEvent_IfDifferentEvent() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.endFileEvent("other")

		then:
		assertNoMessages()
	}

	void testEndEvent_ShouldEndCurrentEvent_IfNull() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.endFileEvent(null)

		then:
		assertMessageCount(1)
	}

	void testEndEvent_ShouldNotCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedNotCalled() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.endFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).modified == false
		assertMessageCount(1)
	}

	void testEndEvent_ShouldCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedCalled() {
		when:
		handler.startFileEvent("file")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
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
		handler.markProcessStarting(5, 3, "TestMyUnit", "JUnit", true)
		handler.markProcessEnding(3, -12)
		then:
		assert getMessage(0, NewExecutionActivity).taskId == 5L
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
		when:
		handler.startFileEvent("file1")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent("file2")
		timeService.plusSeconds(DOES_NOT_PERSIST_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent("file3")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.endFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file1"
		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION_SECONDS * 2
		assertMessageCount(1)
	}

	void testDuplicateEvents_ShouldCreateNewEvent_IfShortActivityComesBetweenTwoActivitiesWithSameNameButDifferentInModifiedState() {
		when:
		handler.startFileEvent("file1")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent("file2")
		timeService.plusSeconds(DOES_NOT_PERSIST_ACTIVITY_DURATION_SECONDS)
		handler.startFileEvent("file3")
		timeService.plusSeconds(PERSISTABLE_ACTIVITY_DURATION_SECONDS)
		handler.fileModified("file1")
		handler.endFileEvent(null)

		then:
		assert getMessage(0, NewEditorActivity).filePath == "file1"
		assert getMessage(0, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION_SECONDS
		assert getMessage(1, NewEditorActivity).filePath == "file3"
		assert getMessage(1, NewEditorActivity).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION_SECONDS
		assertMessageCount(2)
	}


	private void assertNoMessages() {
		assert messageLogger.messages.size() == 0
	}

	private void assertMessageCount(int expectedSize) {
		assert messageLogger.messages.size() == expectedSize
	}

	private <T> T getMessage(int index, Class<T> clazz) {
		assert messageLogger.messages.size() > index
		(T)messageLogger.messages[index]
	}

}
