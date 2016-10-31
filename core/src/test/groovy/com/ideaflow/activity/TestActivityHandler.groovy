package com.ideaflow.activity

import com.ideaflow.controller.IFMController
import org.joda.time.DateTimeUtils
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.ActivityClient
import spock.lang.Ignore
import spock.lang.Specification

class TestActivityHandler extends Specification {

	private static final long NOW = System.currentTimeMillis()
	private static final long PERSISTABLE_ACTIVITY_DURATION = ActivityHandler.SHORTEST_ACTIVITY + 1
	private static final long PERSISTABLE_ACTIVITY_DURATION_MILLIS = PERSISTABLE_ACTIVITY_DURATION * 1000
	private static final long DOES_NOT_PERSIST_ACTIVITY_DURATION = ActivityHandler.SHORTEST_ACTIVITY - 1
	private static final long DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS = DOES_NOT_PERSIST_ACTIVITY_DURATION * 1000

	ActivityHandler handler
	ActivityQueue activityQueue
	IFMController controller = Mock(IFMController)
	ActivityClient activityClient = Mock(ActivityClient)

	void setup() {
		DateTimeUtils.setCurrentMillisFixed(NOW)

		handler = new ActivityHandler(controller)
		handler.activityClient = activityClient
		activityQueue = handler.activityQueue
		controller.getActiveTask() >> new Task(id: 1)
	}

	void cleanup() {
		DateTimeUtils.setCurrentMillisSystem()
	}

	private void assertEditorActivityListEmpty() {
		assertEditorActivityListSize(0)
	}

	private void assertEditorActivityListSize(int expectedSize) {
		assert activityQueue.editorActivityList.size() == expectedSize
	}

	private NewEditorActivity getEditorActivity(int index) {
		assert activityQueue.editorActivityList.size() > index
		activityQueue.editorActivityList[index]
	}

	private NewModificationActivity getModificationActivity(int index) {
		assert activityQueue.modificationActivityList.size() > index
		activityQueue.modificationActivityList[index]
	}

	private NewExecutionActivity getExecutionActivity(int index) {
		assert activityQueue.executionActivityList.size() > index
		activityQueue.executionActivityList[index]
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfNoPriorEvent() {
		when:
		handler.startFileEvent("file")

		then:
		assertEditorActivityListEmpty()
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfSameEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent("file")

		then:
		assertEditorActivityListEmpty()
	}

	void testStartEvent_ShouldCreateEditorActivity_IfDifferentEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent("other")

		then:
		assert getEditorActivity(0).filePath == "file"
		assert getEditorActivity(0).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assertEditorActivityListSize(1)
	}

	void testStartEvent_ShouldNotCreateEditorActivity_IfShortDelay() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + DOES_NOT_PERSIST_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent("other")

		then:
		assertEditorActivityListEmpty()
	}

	void testStartEvent_ShouldEndCurrentEvent_IfNull() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.startFileEvent(null)

		then:
		assert getEditorActivity(0).filePath == "file"
		assertEditorActivityListSize(1)
	}

	void testEndEvent_ShouldEndCurrentEvent_IfSameEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent("file")

		then:
		assert getEditorActivity(0).filePath == "file"
		assert getEditorActivity(0).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assertEditorActivityListSize(1)
	}

	void testEndEvent_ShouldNotEndCurrentEvent_IfDifferentEvent() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent("other")

		then:
		assertEditorActivityListEmpty()
	}

	void testEndEvent_ShouldEndCurrentEvent_IfNull() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent(null)

		then:
		assertEditorActivityListSize(1)
	}

	void testEndEvent_ShouldNotCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedNotCalled() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.endFileEvent(null)

		then:
		assert getEditorActivity(0).modified == false
		assertEditorActivityListSize(1)
	}

	void testEndEvent_ShouldCreateEditorActivityWithModifiedTrue_IfActiveEventModifiedCalled() {
		when:
		handler.startFileEvent("file")
		DateTimeUtils.setCurrentMillisFixed(NOW + PERSISTABLE_ACTIVITY_DURATION_MILLIS)
		handler.fileModified("file")
		handler.endFileEvent(null)

		then:
		assert getEditorActivity(0).modified
		assertEditorActivityListSize(1)
	}

	void testPushModificationActivity_ShouldCountModifications() {
		when:
		handler.fileModified("file")
		handler.fileModified("file")
		handler.fileModified("file")
		handler.pushModificationActivity(30)
		then:
		assert getModificationActivity(0).fileModificationCount == 3
	}

	void testMarkProcessExecution_ShouldPublishActivity_AfterStartStop() {
		when:
		handler.markProcessStarting(3, "TestMyUnit", "JUnit")
		handler.markProcessEnding(3, -12)
		then:
		assert getExecutionActivity(0).processName == "TestMyUnit"
		assert getExecutionActivity(0).executionTaskType == "JUnit"
		assert getExecutionActivity(0).exitCode == -12

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
		assert getEditorActivity(0).filePath == "file1"
		assert getEditorActivity(0).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION * 2
		assertEditorActivityListSize(1)
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
		assert getEditorActivity(0).filePath == "file1"
		assert getEditorActivity(0).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assert getEditorActivity(1).filePath == "file3"
		assert getEditorActivity(1).durationInSeconds == PERSISTABLE_ACTIVITY_DURATION
		assertEditorActivityListSize(2)
	}

}
