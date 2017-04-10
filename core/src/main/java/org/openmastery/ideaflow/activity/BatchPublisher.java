package org.openmastery.ideaflow.activity;

import com.bancvue.rest.exception.NotFoundException;
import org.joda.time.LocalDateTime;
import org.openmastery.ideaflow.Logger;
import org.openmastery.publisher.api.activity.NewBlockActivity;
import org.openmastery.publisher.api.activity.NewEditorActivity;
import org.openmastery.publisher.api.activity.NewExecutionActivity;
import org.openmastery.publisher.api.activity.NewExternalActivity;
import org.openmastery.publisher.api.activity.NewIdleActivity;
import org.openmastery.publisher.api.activity.NewModificationActivity;
import org.openmastery.publisher.api.batch.NewBatchEvent;
import org.openmastery.publisher.api.batch.NewIFMBatch;
import org.openmastery.publisher.api.event.NewSnippetEvent;
import org.openmastery.publisher.client.BatchClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BatchPublisher implements Runnable {

	private AtomicBoolean closed = new AtomicBoolean(false);
	private AtomicReference<Thread> runThreadHolder = new AtomicReference<Thread>();
	private JSONConverter jsonConverter = new JSONConverter();
	private Map<File, Integer> failedFileToLastDayRetriedMap = new LinkedHashMap<File, Integer>();
	private AtomicReference<BatchClient> batchClientReference = new AtomicReference<BatchClient>();
	private Logger logger;
	private File activeDir;
	private File publishDir;
	private File failedDir;
	private File retryNextSessionDir;

	public BatchPublisher(File baseDir, Logger logger) {
		this.logger = logger;
		this.activeDir = createDir(baseDir, "active");
		this.publishDir = createDir(baseDir, "publish");
		this.failedDir = createDir(baseDir, "failed");
		this.retryNextSessionDir = createDir(baseDir, "retryNextSession");

		commitActiveFiles();
	}

	private File createDir(File baseDir, String name) {
		File dir = new File(baseDir, name);
		dir.mkdirs();
		return dir;
	}

	public File createActiveFile(String name) {
		return new File(activeDir, name);
	}

	public void flush() {
		Thread thread = runThreadHolder.get();
		if (thread != null) {
			thread.interrupt();
		}
	}

	public void setBatchClient(BatchClient activityClient) {
		batchClientReference.set(activityClient);

		for (File fileToRetry : retryNextSessionDir.listFiles()) {
			moveFileToDir(fileToRetry, publishDir);
		}

		if (isRunning() == false) {
			new Thread(this).start();
		}
	}

	private File moveFileToDir(File file, File dir) {
		return moveFileToDirAndRename(file, dir, file.getName());
	}

	private File moveFileToDirAndRename(File file, File dir, String renameTo) {
		File renameToFile = new File(dir, renameTo);
		file.renameTo(renameToFile);
		return renameToFile;
	}

	@Override
	public void run() {
		if (runThreadHolder.compareAndSet(null, Thread.currentThread()) == false) {
			return;
		}

		while (isNotClosed()) {
			if (isNotClosed() && hasSomethingToPublish()) {
				publishBatches();
			}

			try {
				Thread.sleep(30000);
			} catch (InterruptedException ex) {
			}
		}
	}

	public void commitActiveFiles() {
		final String dateTime = LocalDateTime.now().toString("yyyyMMdd_HHmmss");
		File[] files = activeDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			moveFileToDirAndRename(files[i], publishDir, dateTime + "_" + i);
		}
	}

	public boolean hasSomethingToPublish() {
		return getBatchesToPublish().length > 0;
	}

	public File[] getBatchesToPublish() {
		List<File> batchesToPublish = new ArrayList<File>();
		int dayOfYear = LocalDateTime.now().getDayOfYear();

		for (File file : publishDir.listFiles()) {
			Integer lastDayTried = failedFileToLastDayRetriedMap.get(file);
			if (lastDayTried == null || lastDayTried != dayOfYear) {
				batchesToPublish.add(file);
			}
		}
		return batchesToPublish.toArray(new File[batchesToPublish.size()]);
	}

	public void publishBatches() {
		File[] batchesToPublish = getBatchesToPublish();
		Arrays.sort(batchesToPublish);

		try {
			for (File batchToPublish : batchesToPublish) {
				convertPublishAndDeleteBatch(batchToPublish);
			}
		} catch (Exception ex) {
			logger.error("Unhandled error during batch file publishing...", ex);
		}
	}

	private void convertPublishAndDeleteBatch(final File batchFile) {
		NewIFMBatch batch;
		try {
			batch = convertBatchFileToObject(batchFile);
		} catch (Exception ex) {
			final File renameToFile = moveFileToDir(batchFile, failedDir);
			logger.info("Failed to convert " + batchFile.getAbsolutePath() + ", exception=" + ex.getMessage() + ", renamingTo=" + renameToFile.getAbsolutePath());
			return;
		}

		try {
			publishBatch(batch);
			batchFile.delete();
		} catch (NotFoundException ex) {
			moveFileToDir(batchFile, retryNextSessionDir);
			logger.info("Failed to publish " + batchFile.getAbsolutePath() + " due to missing task, will retry in future session...");
		} catch (Exception ex) {
			failedFileToLastDayRetriedMap.put(batchFile, LocalDateTime.now().getDayOfYear());
			logger.info("Failed to publish " + batchFile.getAbsolutePath() + ", exception=" + ex.getMessage() + ", will retry tomorrow...");
		}

	}

	public void publishBatch(NewIFMBatch batch) {
		BatchClient batchClient = batchClientReference.get();
		if (batchClient == null) {
			throw new ServerUnavailableException("BatchClient is unavailable");
		}

		if (batch.isEmpty() == false) {
			batchClient.addIFMBatch(batch);
		}
	}

	public NewIFMBatch convertBatchFileToObject(File batchFile) throws IOException {
		NewIFMBatch.NewIFMBatchBuilder builder = NewIFMBatch.builder()
				.timeSent(LocalDateTime.now());

		BufferedReader reader = new BufferedReader(new FileReader(batchFile));
		for (String line = reader.readLine(); line != null; line = reader.readLine()) {
			Object object = jsonConverter.fromJSON(line);
			addObjectToBatch(builder, object);
		}
		return builder.build();
	}

	private void addObjectToBatch(NewIFMBatch.NewIFMBatchBuilder builder, final Object object) {
		if (object instanceof NewEditorActivity) {
			builder.editorActivity((NewEditorActivity) object);
		} else if (object instanceof NewExternalActivity) {
			builder.externalActivity((NewExternalActivity) object);
		} else if (object instanceof NewIdleActivity) {
			builder.idleActivity((NewIdleActivity) object);
		} else if (object instanceof NewExecutionActivity) {
			builder.executionActivity((NewExecutionActivity) object);
		} else if (object instanceof NewModificationActivity) {
			builder.modificationActivity((NewModificationActivity) object);
		} else if (object instanceof NewBlockActivity) {
			builder.blockActivity((NewBlockActivity) object);
		} else if (object instanceof NewBatchEvent) {
			builder.event((NewBatchEvent) object);
		} else if (object instanceof NewSnippetEvent) {
			builder.snippetEvent((NewSnippetEvent) object);
		} else {
			throw new RuntimeException("Unrecognized batch object=" + String.valueOf(object));
		}
	}

	private boolean isRunning() {
		return runThreadHolder.get() != null;
	}

	private boolean isNotClosed() {
		return closed.get() == false;
	}

	public void close() {
		closed.set(true);

		Thread runThread = runThreadHolder.get();
		if (runThread != null) {
			runThread.interrupt();
			runThreadHolder.compareAndSet(runThread, null);
		}
	}

}
