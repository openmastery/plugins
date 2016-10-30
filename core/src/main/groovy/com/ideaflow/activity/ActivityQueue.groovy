package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.client.ActivityClient

import java.util.concurrent.atomic.AtomicReference

class ActivityQueue {

	private final Object lock = new Object()
	private List<NewEditorActivity> editorActivityList = []
	private List<NewExternalActivity> externalActivityList = []
	private List<NewIdleActivity> idleActivityList = []
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

		synchronized (lock) {
			editorActivityListCopy = new ArrayList<>(editorActivityList)
			externalActivityListCopy = new ArrayList<>(externalActivityList)
			idleActivityListCopy = new ArrayList<>(idleActivityList)

			editorActivityList.clear()
			externalActivityList.clear()
			idleActivityList.clear()
		}

		NewActivityBatch.builder()
				.timeSent(LocalDateTime.now())
				.idleActivityList(idleActivityListCopy)
				.editorActivityList(editorActivityListCopy)
				.externalActivityList(externalActivityListCopy)
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
