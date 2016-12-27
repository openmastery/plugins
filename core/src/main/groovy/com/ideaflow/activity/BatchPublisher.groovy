package com.ideaflow.activity

import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewBlockActivity
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.client.BatchClient

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


class BatchPublisher implements Runnable {

	static final String BATCH_FILE_PREFIX = "batch_"
	static final String FAILED_FILE_PREFIX = "failed_"

	private AtomicBoolean closed = new AtomicBoolean(false)
	private Thread runThread
	private JSONConverter jsonConverter = new JSONConverter()

	private AtomicReference<BatchClient> batchClientReference = new AtomicReference<>()
	private File messageQueueDir

	BatchPublisher(File messageQueueDir) {
		this.messageQueueDir = messageQueueDir
	}

	void setBatchClient(BatchClient activityClient) {
		batchClientReference.set(activityClient)
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
		messageFile.renameTo(batchFile)
	}

	boolean hasSomethingToPublish() {
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
				convertPublishAndDeleteBatch(batchFile)
			}
		} catch (Exception ex) {
			println "Unhandled error during batch file publishing..."
			ex.printStackTrace()
		}
	}

	private void convertPublishAndDeleteBatch(File batchFile) {
		NewIFMBatch batch
		try {
			batch = convertBatchFileToObject(batchFile)
		} catch (Exception ex) {
			// TODO: should distinguish between communication failure and server issue (i.e. task does not exist)
			File renameToFile = new File(batchFile.parentFile, FAILED_FILE_PREFIX + batchFile.name)
			batchFile.renameTo(renameToFile)
			println "Failed to convert ${batchFile.absolutePath}, exception=${ex.message}, renamingTo=${renameToFile.absolutePath}"
			return
		}

		try {
			publishBatch(batch)
			batchFile.delete()
		} catch (Exception ex) {
			println "Failed to publish ${batchFile.absolutePath}, exception=${ex.message}, will retry later..."
		}
	}

	void publishBatch(NewIFMBatch batch) {
		BatchClient batchClient = batchClientReference.get()
		if (batchClient == null) {
			throw new ServerUnavailableException("BatchClient is unavailable")
		}

		if (batch.isEmpty() == false) {
			batchClient.addIFMBatch(batch)
		}
	}


	List<File> findAllBatchesAndSort() {
		messageQueueDir.listFiles().findAll { File file ->
			file.name.matches(BATCH_FILE_PREFIX+".*")
		}.sort { File file ->
			file.name
		}
	}

	NewIFMBatch convertBatchFileToObject(File batchFile) {
		NewIFMBatch batch = createEmptyBatch()
		batchFile.eachLine { String line ->
			Object object = jsonConverter.fromJSON(line)
			addObjectToBatch(batch, object)
		}
		return batch
	}

	private NewIFMBatch createEmptyBatch() {
		NewIFMBatch batch = new NewIFMBatch()
		batch.timeSent = LocalDateTime.now()
		batch.editorActivityList = []
		batch.externalActivityList = []
		batch.idleActivityList = []
		batch.executionActivityList = []
		batch.modificationActivityList = []
		batch.blockActivityList = []
		batch.eventList =[]
		return batch
	}

	private void addObjectToBatch(NewIFMBatch batch, Object object) {
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
		} else if (object instanceof NewBlockActivity) {
			batch.blockActivityList.add(object)
		} else if (object instanceof NewBatchEvent) {
			batch.eventList.add(object)
		} else {
			throw new RuntimeException("Unrecognized batch object=${object}")
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
