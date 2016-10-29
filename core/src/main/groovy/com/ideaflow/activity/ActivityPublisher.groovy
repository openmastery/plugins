package com.ideaflow.activity

import java.util.concurrent.atomic.AtomicBoolean


class ActivityPublisher implements Runnable {

	private AtomicBoolean closed = new AtomicBoolean(false)
	private Thread runThread
	private ActivityQueue activityQueue

	ActivityPublisher(ActivityQueue activityQueue) {
		this.activityQueue = activityQueue
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

	private void publishBatch() {
		try {
			activityQueue.publishActivityBatch()
		} catch (Exception ex) {
			ex.printStackTrace()
		}
	}

}
