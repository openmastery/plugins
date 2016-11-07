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


class BatchPublisher implements Runnable {

	static final String BATCH_FILE_PREFIX = "batch_"

	private AtomicBoolean closed = new AtomicBoolean(false)
	private Thread runThread
	private JSONConverter jsonConverter = new JSONConverter()

	private AtomicReference<ActivityClient> activityClientReference = new AtomicReference<>()
	private File messageQueueDir

	BatchPublisher(File messageQueueDir) {
		this.messageQueueDir = messageQueueDir
	}

	void setActivityClient(ActivityClient activityClient) {
		activityClientReference.set(activityClient)
	}

	@Override
	void run() {
		runThread = Thread.currentThread()

		while (isNotClosed()) {
			try {
				Thread.sleep(30000)
			} catch (InterruptedException ex) {
			}

			if (isNotClosed() && hasSomethingToPublish()) {
				publishBatches()
			}
		}
	}

	void commitBatch(File messageFile) {
		File batchFile = new File(messageQueueDir, BATCH_FILE_PREFIX + createTimestampSuffix())
		println batchFile
		messageFile.renameTo(batchFile)
	}

	boolean hasSomethingToPublish() {
		messageQueueDir.listFiles().find{ File file ->
			if (file.name.contains("batch")) {
				println file.name
			}
		}
		messageQueueDir.listFiles(new FilenameFilter() {
			@Override
			boolean accept(File filePath, String fileName) {
				return fileName.matches(BATCH_FILE_PREFIX+".*")
			}
		}).size() > 0
	}

	void publishBatches() {
		try {
			List<File> batchFiles = findAllBatchesAndSort()
			batchFiles.each { File batchFile ->
				NewActivityBatch batch = convertBatchFileToObject(batchFile)
				publishActivityBatch(batch)
				batchFile.delete()
			}
		} catch (Exception ex) {
			// Try again later... oh well
			ex.printStackTrace()
		}
	}

	void publishActivityBatch(NewActivityBatch batch) {
		ActivityClient activityClient = activityClientReference.get()
		if (activityClient == null) {
			return
		}

		if (batch.isEmpty() == false) {
			activityClient.addActivityBatch(batch)
		}
	}


	List<File> findAllBatchesAndSort() {
		messageQueueDir.listFiles().findAll { File file ->
			file.name.matches(BATCH_FILE_PREFIX+".*")
		}.sort { File file ->
			file.name
		}
	}

	NewActivityBatch convertBatchFileToObject(File batchFile) {
		NewActivityBatch batch = createEmptyBatch()
		batchFile.eachLine { String line ->
			Object object = jsonConverter.fromJSON(line)
			addObjectToBatch(batch, object)

		}
		return batch
	}

	private NewActivityBatch createEmptyBatch() {
		NewActivityBatch.builder()
				.timeSent(LocalDateTime.now())
				.editorActivityList([])
				.externalActivityList([])
				.idleActivityList([])
				.executionActivityList([])
				.modificationActivityList([])
				.build()
	}


	private void addObjectToBatch(NewActivityBatch batch, Object object) {
		if (object instanceof NewEditorActivity) {
			batch.editorActivityList.add(object)
		} else if (object instanceof NewExternalActivity) {
			batch.externalActivityList.add(object)
		} else if (object instanceof NewIdleActivity) {
			batch.idleActivityList.add(object)
		} else if (object instanceof NewExecutionActivity) {
			batch.executionActivityList.add(object)
		} else if (object instanceof NewModificationActivity) {
			batch.modificationActivityList.add(object)
		}
	}

	private boolean isNotClosed() {
		closed.get() == false
	}

	void close() {
		closed.set(true)
		if (runThread != null) {
			runThread.interrupt()
		}
	}








	String createTimestampSuffix() {
		LocalDateTime now = LocalDateTime.now()

		now.toString("yyyyMMdd_HHmmss")
	}


}
