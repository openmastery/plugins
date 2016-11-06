package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.client.ActivityClient

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


class BatchSender implements Runnable {

	private AtomicBoolean closed = new AtomicBoolean(false)
	private Thread runThread
	private final Object lock = new Object()

	private AtomicReference<ActivityClient> activityClientReference = new AtomicReference<>()

	private List<NewEditorActivity> editorActivityList = []
	private List<NewExternalActivity> externalActivityList = []
	private List<NewIdleActivity> idleActivityList = []
	private List<NewModificationActivity> modificationActivityList = []
	private List<NewExecutionActivity> executionActivityList = []

	void setActivityClient(ActivityClient activityClient) {
		activityClientReference.set(activityClient)
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

	@Override
	void run() {
		runThread = Thread.currentThread()

		while (shouldPublish()) {
			try {
				Thread.sleep(30000)
			} catch (InterruptedException ex) {
			}

			if (shouldPublish()) {
				publishBatch()
			}
		}
	}


	private boolean shouldPublish() {
		closed.get() == false
	}

	void close() {
		closed.set(true)
		if (runThread != null) {
			runThread.interrupt()
		}
	}


	private void publishBatch() {
		try {
			//TODO publish some stuff
		} catch (Exception ex) {
			// TODO: what to do on failure?
			ex.printStackTrace()
		}
	}
}
