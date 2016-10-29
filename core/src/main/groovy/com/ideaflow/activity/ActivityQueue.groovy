package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.client.ActivityClient

class ActivityQueue {

	private final Object lock = new Object()
	private List<NewEditorActivity> editorActivityList = []
	private List<NewExternalActivity> externalActivityList = []
	private List<NewIdleActivity> idleActivityList = []
	private ActivityClient activityClient

	ActivityQueue(ActivityClient activityClient) {
		this.activityClient = activityClient
	}

	void pushEditorActivity(Long taskId, Long durationInSeconds, String filePath, boolean isModified) {
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
		NewIdleActivity activity = NewIdleActivity.builder()
				.taskId(taskId)
				.durationInSeconds(durationInSeconds)
				.build();

		synchronized (lock) {
			idleActivityList << activity
		}
	}

	void pushExternalActivity(Long taskId, Long durationInSeconds, String comment) {
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

		NewActivityBatch batch = NewActivityBatch.builder()
				.timeSent(LocalDateTime.now())
				.idleActivityList(idleActivityListCopy)
				.editorActivityList(editorActivityListCopy)
				.externalActivityList(externalActivityListCopy)
				.build()

		if (batch.isEmpty() == false) {
			withRetry(3) {
				activityClient.addActivityBatch(batch)
			}
		}
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
