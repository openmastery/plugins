package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.client.ActivityClient

import java.util.concurrent.atomic.AtomicReference

class ActivityQueue {

	private final Object lock = new Object()
	private List<NewEditorActivity> editorActivityList = []
	private List<NewExternalActivity> externalActivityList = []
	private List<NewIdleActivity> idleActivityList = []
	private List<NewModificationActivity> modificationActivityList = []
	private List<NewExecutionActivity> executionActivityList = []
	private AtomicReference<ActivityClient> activityClientReference = new AtomicReference<>()

	void setActivityClient(ActivityClient activityClient) {
		activityClientReference.set(activityClient)
	}

	private boolean isDisabled() {
		activityClientReference.get() == null
	}

	void pushEditorActivity(Long taskId, Long durationInSeconds, String filePath, boolean isModified) {
		if (isDisabled()) {
			return
		}

		NewEditorActivity activity = NewEditorActivity.builder()
				.taskId(taskId)
				.filePath(filePath)
				.isModified(isModified)
				.durationInSeconds(durationInSeconds)
				.build();

		synchronized (lock) {
			editorActivityList << activity
		}
	}

	void pushModificationActivity(Long taskId, Long durationInSeconds, int modificationCount) {
		if (isDisabled()) {
			return
		}

		NewModificationActivity activity = NewModificationActivity.builder()
				.taskId(taskId)
				.fileModificationCount(modificationCount)
				.durationInSeconds(durationInSeconds)
				.build();

		synchronized (lock) {
			modificationActivityList << activity
		}
	}

	void pushExecutionActivity(Long taskId, Long durationInSeconds, String processName,
							   int exitCode,
							   String executionTaskType,
							   boolean isDebug) {
		if (isDisabled()) {
			return
		}

		NewExecutionActivity activity = NewExecutionActivity.builder()
				.taskId(taskId)
				.processName(processName)
				.exitCode(exitCode)
				.executionTaskType(executionTaskType)
				.durationInSeconds(durationInSeconds)
				.isDebug(isDebug)
				.build();

		synchronized (lock) {
			executionActivityList << activity
		}
	}

	void pushIdleActivity(Long taskId, Long durationInSeconds) {
		if (isDisabled()) {
			return
		}

		NewIdleActivity activity = NewIdleActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.build();

		synchronized (lock) {
			idleActivityList << activity
		}
	}

	void pushExternalActivity(Long taskId, Long durationInSeconds, String comment) {
		if (isDisabled()) {
			return
		}

		NewExternalActivity activity = NewExternalActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.comment(comment)
				.build();

		synchronized (lock) {
			externalActivityList << activity
		}
	}

	void publishActivityBatch() {
		ActivityClient activityClient = activityClientReference.get()
		if (activityClient == null) {
			return
		}

		NewActivityBatch batch = clearActivityListsAndCreateBatch()
		if (batch.isEmpty() == false) {
			withRetry(3) {
				activityClient.addActivityBatch(batch)
			}
		}
	}

	private NewActivityBatch clearActivityListsAndCreateBatch() {
		List<NewEditorActivity> editorActivityListCopy
		List<NewExternalActivity> externalActivityListCopy
		List<NewIdleActivity> idleActivityListCopy
		List<NewModificationActivity> modificationActivityListCopy
		List<NewExecutionActivity> executionActivityListCopy

		synchronized (lock) {
			editorActivityListCopy = new ArrayList<>(editorActivityList)
			externalActivityListCopy = new ArrayList<>(externalActivityList)
			idleActivityListCopy = new ArrayList<>(idleActivityList)
			modificationActivityListCopy = new ArrayList<>(modificationActivityList)
			executionActivityListCopy = new ArrayList<>(executionActivityList)

			editorActivityList.clear()
			externalActivityList.clear()
			idleActivityList.clear()
			modificationActivityList.clear()
			executionActivityList.clear()
		}

		NewActivityBatch.builder()
				.timeSent(LocalDateTime.now())
				.idleActivityList(idleActivityListCopy)
				.editorActivityList(editorActivityListCopy)
				.externalActivityList(externalActivityListCopy)
				.modificationActivityList(modificationActivityListCopy)
				.executionActivityList(executionActivityListCopy)
				.build()
	}

	private void withRetry(int maxAttempts, Closure block) {
		boolean success = false

		for (int count = 1; success == false; count++) {
			try {
				block.call()
				success = true
			} catch (Exception ex) {
				if (count >= maxAttempts) {
					throw ex
				}
			}
		}
	}

}
