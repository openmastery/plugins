package org.openmastery.ideaflow.activity

import com.bancvue.rest.exception.NotFoundException
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewBlockActivity
import org.openmastery.publisher.api.activity.NewEditorActivity
import org.openmastery.publisher.api.activity.NewExecutionActivity
import org.openmastery.publisher.api.activity.NewExternalActivity
import org.openmastery.publisher.api.activity.NewIdleActivity
import org.openmastery.publisher.api.activity.NewModificationActivity
import org.openmastery.publisher.api.batch.NewBatchEvent
import org.openmastery.publisher.api.batch.NewIFMBatch
import org.openmastery.publisher.api.event.NewSnippetEvent
import org.openmastery.publisher.client.BatchClient

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference


class BatchPublisher implements Runnable {

	private AtomicBoolean closed = new AtomicBoolean(false)
	private Thread runThread
	private JSONConverter jsonConverter = new JSONConverter()

	private AtomicReference<BatchClient> batchClientReference = new AtomicReference<>()
	private File activeDir
	private File publishDir
	private File failedDir
	private File retryNextSessionDir

	BatchPublisher(File baseDir) {
		this.activeDir = createDir(baseDir, "active")
		this.publishDir = createDir(baseDir, "publish")
		this.failedDir = createDir(baseDir, "failed")
		this.retryNextSessionDir = createDir(baseDir, "retryNextSession")

		commitActiveFiles()
	}

	private File createDir(File baseDir, String name) {
		File dir = new File(baseDir, name)
		dir.mkdirs()
		dir
	}

	File createActiveFile(String name) {
		new File(activeDir, name)
	}

	void setBatchClient(BatchClient activityClient) {
		batchClientReference.set(activityClient)

		retryNextSessionDir.listFiles().each { File fileToRetry ->
			moveFileToDir(fileToRetry, publishDir)
		}
	}

	private File moveFileToDir(File file, File dir) {
		moveFileToDirAndRename(file, dir, file.name)
	}

	private File moveFileToDirAndRename(File file, File dir, String renameTo) {
		File renameToFile = new File(dir, renameTo)
		file.renameTo(renameToFile)
		renameToFile
	}

	@Override
	void run() {
		runThread = Thread.currentThread()

		while (isNotClosed()) {
			if (isNotClosed() && hasSomethingToPublish()) {
				publishBatches()
			}

			try {
				Thread.sleep(30000)
			} catch (InterruptedException ex) {
			}
		}
	}

	void commitActiveFiles() {
		String dateTime = LocalDateTime.now().toString("yyyyMMdd_HHmmss")
		File[] files = activeDir.listFiles()

		for (int i = 0; i < files.length; i++) {
			moveFileToDirAndRename(files[i], publishDir, "${dateTime}_${i}")
		}
	}

	boolean hasSomethingToPublish() {
		publishDir.listFiles().size() > 0
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

	private List<File> findAllBatchesAndSort() {
		publishDir.listFiles().sort() { File file ->
			file.name
		}
	}

	private void convertPublishAndDeleteBatch(File batchFile) {
		NewIFMBatch batch
		try {
			batch = convertBatchFileToObject(batchFile)
		} catch (Exception ex) {
			File renameToFile = moveFileToDir(batchFile, failedDir)
			println "Failed to convert ${batchFile.absolutePath}, exception=${ex.message}, renamingTo=${renameToFile.absolutePath}"
			return
		}

		try {
			publishBatch(batch)
			batchFile.delete()
		} catch (NotFoundException ex) {
			moveFileToDir(batchFile, retryNextSessionDir)
			println "Failed to publish ${batchFile.absolutePath} due to missing task, will retry in future session..."
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
		batch.snippetEventList = []
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
		} else if (object instanceof NewSnippetEvent) {
			batch.snippetEventList.add(object)
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

}
