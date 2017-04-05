package org.openmastery.ideaflow.activity;

import org.openmastery.ideaflow.controller.IFMController;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.openmastery.ideaflow.state.TaskState;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityHandler {

	private static final int SHORTEST_ACTIVITY = 3;

	private IFMController controller;
	private MessageQueue messageQueue;
	private FileActivity activeFileActivity;
	private AtomicInteger modificationCount = new AtomicInteger(0);

	private Duration recentIdleDuration = null;

	private Map<Long, ProcessActivity> activeProcessMap = new HashMap<Long, ProcessActivity>();

	public ActivityHandler(IFMController controller, MessageQueue messageQueue) {
		this.controller = controller;
		this.messageQueue = messageQueue;
	}

	public Duration getRecentIdleDuration() {
		return recentIdleDuration;
	}

	private boolean isSame(String newFilePath) {
		return isDifferent(newFilePath) == false;
	}

	private boolean isDifferent(String newFilePath) {
		if (activeFileActivity == null) {
			return newFilePath != null;
		} else {
			return activeFileActivity.filePath.equals(newFilePath) == false;
		}
	}

	private boolean isOverActivityThreshold() {
		return activeFileActivity != null && activeFileActivity.getDurationInSeconds() >= SHORTEST_ACTIVITY;
	}

	private Long getActiveTaskId() {
		TaskState taskState = controller.getActiveTask();
		return taskState != null ? taskState.getId() : null;
	}

	public void markIdleTime(final Duration idleDuration) {
		markIdleOrExternal(idleDuration, new ActiveTaskBlock() {
			@Override
			public void execute(Long activeTaskId) {
				messageQueue.pushIdleActivity(activeTaskId, idleDuration.getStandardSeconds());
			}
		});
	}

	public void markExternalActivity(final Duration idleDuration, final String comment) {
		recentIdleDuration = idleDuration;
		markIdleOrExternal(idleDuration, new ActiveTaskBlock() {
			@Override
			public void execute(Long activeTaskId) {
				messageQueue.pushExternalActivity(activeTaskId, idleDuration.getStandardSeconds(), comment);
			}
		});
	}

	private void markIdleOrExternal(Duration idleDuration, ActiveTaskBlock block) {
		Long activeTaskId = getActiveTaskId();
		if (activeTaskId == null) {
			return;
		}

		if (idleDuration.getStandardSeconds() >= SHORTEST_ACTIVITY) {
			if (activeFileActivity != null) {
				long duration = activeFileActivity.getDurationInSeconds() - idleDuration.getStandardSeconds();
				if (duration > 0) {
					LocalDateTime endTime = LocalDateTime.now().minusSeconds((int) idleDuration.getStandardSeconds());
					messageQueue.pushEditorActivity(coalesce(activeFileActivity.taskId, activeTaskId),
					                                duration, endTime, activeFileActivity.filePath, activeFileActivity.modified);
				}
			}
			block.execute(activeTaskId);
			if (activeFileActivity != null) {
				activeFileActivity = createFileActivity(activeTaskId, activeFileActivity.filePath);
			}
		}
	}

	public void markProcessStarting(Long taskId, Long processId, String processName, String executionTaskType, boolean isDebug) {
		ProcessActivity processActivity = new ProcessActivity(taskId, processName, executionTaskType, LocalDateTime.now(), isDebug);
		activeProcessMap.put(processId, processActivity);
		//TODO this will leak memory if the processes started are never closed
	}

	public void markProcessEnding(Long processId, int exitCode) {
		ProcessActivity processActivity = activeProcessMap.remove(processId);
		Long activeTaskId = getActiveTaskId();
		if (processActivity != null && activeTaskId != null) {
			messageQueue.pushExecutionActivity(coalesce(processActivity.taskId, activeTaskId), processActivity.getDurationInSeconds(),
			                                   processActivity.processName, exitCode, processActivity.executionTaskType, processActivity.isDebug);
		}
	}

	public void startFileEvent(String filePath) {
		Long activeTaskId = getActiveTaskId();
		if (activeTaskId == null) {
			return;
		}

		if (isDifferent(filePath)) {
			if (isOverActivityThreshold()) {
				messageQueue.pushEditorActivity(coalesce(activeFileActivity.taskId, activeTaskId),
				                                activeFileActivity.getDurationInSeconds(),
				                                activeFileActivity.filePath, activeFileActivity.modified);
			}

			activeFileActivity = createFileActivity(activeTaskId, filePath);
		}
	}

	public void endFileEvent(String filePath) {
		if ((filePath == null) || isSame(filePath)) {
			startFileEvent(null);
		}
	}

	public void fileModified(String filePath) {
		if (activeFileActivity != null && activeFileActivity.filePath.equals(filePath)) {
			activeFileActivity.modified = true;
		}
		modificationCount.incrementAndGet();
	}

	public void pushModificationActivity(Long intervalInSeconds) {
		int modCount = modificationCount.getAndSet(0);
		if (modCount > 0) {
			messageQueue.pushModificationActivity(getActiveTaskId(), intervalInSeconds, modCount);
		}
	}

	private FileActivity createFileActivity(Long taskId, String filePath) {
		return filePath == null ? null : new FileActivity(taskId, filePath, LocalDateTime.now(), false);
	}

	private Long coalesce(Long primary, Long secondary) {
		return primary != null ? primary : secondary;
	}



	private interface ActiveTaskBlock {

		void execute(Long activeTaskId);

	}

	private static class ProcessActivity {

		private Long taskId;
		private LocalDateTime timeStarted;
		private String processName;
		private String executionTaskType;
		private boolean isDebug;

		public ProcessActivity(Long taskId, String processName, String executionTaskType, LocalDateTime timeStarted, boolean isDebug) {
			this.taskId = taskId;
			this.processName = processName;
			this.executionTaskType = executionTaskType;
			this.timeStarted = timeStarted;
			this.isDebug = isDebug;
		}

		public long getDurationInSeconds() {
			return Period.fieldDifference(timeStarted, LocalDateTime.now()).getMillis() / 1000;
		}

		public String toString() {
			return "ProcessActivity [taskId=" + taskId + ", processName=" + processName + ", executionTaskType=" +
					executionTaskType + ", " + "duration=" + getDurationInSeconds() + ", isDebug=" + isDebug + "]";
		}
	}

	private static class FileActivity {

		private Long taskId;
		private LocalDateTime time;
		private String filePath;
		private boolean modified;

		public FileActivity(Long taskId, String filePath, LocalDateTime time, boolean modified) {
			this.taskId = taskId;
			this.filePath = filePath;
			this.time = time;
			this.modified = modified;
		}

		public long getDurationInSeconds() {
			return Period.fieldDifference(time, LocalDateTime.now()).getMillis() / 1000;
		}

		public String toString() {
			return "FileActivity [taskId=" + taskId + ", path=" + filePath + ", modified=" + modified + ", duration=" + getDurationInSeconds() + "]";
		}
	}

}
