package org.openmastery.ideaflow.activity;

import com.bancvue.rest.exception.NotFoundException;
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
import org.openmastery.time.TimeService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BatchPublisher implements Runnable {

	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

	// if the user has multiple IDEs running, it's possible the same file could be moved and overwritten.  so, modify files
	// with a random number specific to the IDE instance which should make it statistically improbable for that to happen
	private static final String FILE_MODIFIER = "." + new Random().nextLong();
	private static final long BATCH_PUBLISH_FREQUENCY_MS = 30000;

	private AtomicBoolean closed = new AtomicBoolean(false);
	private AtomicReference<Thread> runThreadHolder = new AtomicReference<>();
	private JSONConverter jsonConverter = new JSONConverter();
	private Map<File, Integer> failedFileToLastDayRetriedMap = new LinkedHashMap<>();
	private AtomicReference<BatchClient> batchClientReference = new AtomicReference<>();
	private Logger logger;
	private File activeDir;
	private File publishDir;
	private File failedDir;
	private File retryNextSessionDir;
	private TimeService timeService;
	private PublishingLock publishingLock;

	public BatchPublisher(File baseDir, Logger logger, TimeService timeService) {
		this.logger = logger;
		this.timeService = timeService;
		this.activeDir = createDir(baseDir, "active");
		this.publishDir = createDir(baseDir, "publish");
		this.failedDir = createDir(baseDir, "failed");
		this.retryNextSessionDir = createDir(baseDir, "retryNextSession");
		this.publishingLock = new PublishingLock(logger, baseDir);

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
		File renameToFile = new File(dir, renameTo + FILE_MODIFIER);
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
				acquireLockAndPublishBatches();
			}

			try {
				Thread.sleep(BATCH_PUBLISH_FREQUENCY_MS);
			} catch (InterruptedException ex) {
			}
		}
	}

	public void commitActiveFiles() {
		final String dateTime = DATE_TIME_FORMATTER.format(timeService.now());
		File[] files = activeDir.listFiles();

		for (int i = 0; i < files.length; i++) {
			moveFileToDirAndRename(files[i], publishDir, dateTime + "_" + i);
		}
	}

	private boolean hasSomethingToPublish() {
		return getBatchesToPublish().length > 0;
	}

	public File[] getBatchesToPublish() {
		List<File> batchesToPublish = new ArrayList<>();
		int dayOfYear = timeService.now().getDayOfYear();

		for (File file : publishDir.listFiles()) {
			Integer lastDayTried = failedFileToLastDayRetriedMap.get(file);
			if (lastDayTried == null || lastDayTried != dayOfYear) {
				batchesToPublish.add(file);
			}
		}
		return batchesToPublish.toArray(new File[batchesToPublish.size()]);
	}

	private void acquireLockAndPublishBatches() {
		if (publishingLock.acquire()) {
			try {
				publishBatches();
			} finally {
				publishingLock.release();
			}
		}
	}

	private void publishBatches() {
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
			failedFileToLastDayRetriedMap.put(batchFile, timeService.now().getDayOfYear());
			logger.info("Failed to publish " + batchFile.getAbsolutePath() + ", exception=" + ex.getMessage() + ", will retry tomorrow...");
		}

	}

	private void publishBatch(NewIFMBatch batch) {
		BatchClient batchClient = batchClientReference.get();
		if (batchClient == null) {
			throw new ServerUnavailableException("BatchClient is unavailable");
		}

		if (batch.isEmpty() == false) {
			batchClient.addIFMBatch(batch);
		}
	}

	private NewIFMBatch convertBatchFileToObject(File batchFile) throws IOException {
		NewIFMBatch.NewIFMBatchBuilder builder = NewIFMBatch.builder()
				.timeSent(timeService.now());

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


	private static class PublishingLock {

		private Logger logger;
		private FileLock lock;
		private RandomAccessFile publishingLockFile;

		public PublishingLock(Logger logger, File baseDir) {
			this.logger = logger;
			this.publishingLockFile = createPublishingLockFile(baseDir);
		}

		private RandomAccessFile createPublishingLockFile(File baseDir) {
			File lockFile = new File(baseDir, "publishing.lock");
			try {
				lockFile.createNewFile();
				return new RandomAccessFile(lockFile, "rw");
			} catch (IOException ex) {
				throw new RuntimeException("Failed to create lock file", ex);
			}
		}

		public boolean acquire() {
			FileChannel channel = publishingLockFile.getChannel();
			try {
				lock = channel.tryLock();
				if (lock != null) {
					publishingLockFile.writeChars(System.currentTimeMillis() + "");
				}
				return lock != null;
			} catch (OverlappingFileLockException ex) {
				// this shouldn't be possible since the BatchPublisher is single-threaded...
				logger.error("Application error, publishing lock already acquired by current process");
				return false;
			} catch (IOException ex) {
				return false;
			}
		}

		public void release() {
			try {
				lock.release();
			} catch (Exception ex) {
				logger.error("Failed to release publishing lock", ex);
			} finally {
				lock = null;
			}
		}

	}
}
