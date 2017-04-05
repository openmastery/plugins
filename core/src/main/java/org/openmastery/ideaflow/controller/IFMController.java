package org.openmastery.ideaflow.controller;

import com.bancvue.rest.exception.NotFoundException;
import org.joda.time.Duration;
import org.openmastery.ideaflow.Logger;
import org.openmastery.ideaflow.activity.ActivityHandler;
import org.openmastery.ideaflow.activity.BatchPublisher;
import org.openmastery.ideaflow.activity.MessageQueue;
import org.openmastery.ideaflow.state.TaskState;
import org.openmastery.publisher.api.SharedTags;
import org.openmastery.publisher.api.event.EventType;
import org.openmastery.publisher.api.task.Task;
import org.openmastery.publisher.client.BatchClient;
import org.openmastery.publisher.client.TaskClient;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class IFMController {

	private boolean paused = true;
	private TaskClient taskClient;
	private TaskState activeTask;
	private ActivityHandler activityHandler;
	private MessageQueue messageQueue;
	private BatchPublisher batchPublisher;

	public IFMController(Logger logger) {
		File ideaFlowDir = createIdeaFlowDir();
		batchPublisher = new BatchPublisher(ideaFlowDir, logger);
		messageQueue = new MessageQueue(this, batchPublisher);
		activityHandler = new ActivityHandler(this, messageQueue);
		startPushModificationActivityTimer(30);
	}

	private File createIdeaFlowDir() {
		File ideaFlowDir = new File(System.getProperty("user.home") + File.separator + ".ideaflow");
		ideaFlowDir.mkdirs();
		return ideaFlowDir;
	}

	private void startPushModificationActivityTimer(final long intervalInSeconds) {
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				getActivityHandler().pushModificationActivity(intervalInSeconds);
			}
		};

		long intervalInMillis = intervalInSeconds * 1000;
		new Timer().scheduleAtFixedRate(timerTask, intervalInMillis, intervalInMillis);
	}

	public ActivityHandler getActivityHandler() {
		return activityHandler;
	}

	public void flushBatch() {
		messageQueue.flush();
		batchPublisher.flush();
	}

	public void initClients(String apiUrl, String apiKey) {
		// TODO: make these configurable
		taskClient = new TaskClient(apiUrl)
				.apiKey(apiKey)
				.connectTimeout(1000)
				.readTimeout(2000);
		BatchClient batchClient = new BatchClient(apiUrl)
				.apiKey(apiKey)
				.connectTimeout(5000)
				.readTimeout(30000);
		batchPublisher.setBatchClient(batchClient);
	}

	public Duration getRecentIdleDuration() {
		return getActivityHandler().getRecentIdleDuration();
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	public boolean isRecording() {
		return paused == false;
	}

	public boolean isTaskActive() {
		return (activeTask != null);
	}

	public void setActiveTask(TaskState newActiveTask) {
		this.activeTask = newActiveTask;
	}

	public TaskState createAndActivateTask(String name, String description, String project) {
		// TODO: what to do on conflict?  Do we still need to activate tasks?
		Task remoteTask = taskClient.createTask(name, description, project);
		TaskState task = TaskState.create(remoteTask);

		activateTask(task);
		return task;
	}

	public TaskState resumeAndActivateTask(String name) throws NoSuchTaskToResumeException {
		Task remoteTask;
		try {
			remoteTask = taskClient.findTaskWithName(name);
		} catch (NotFoundException ex) {
			throw new NoSuchTaskToResumeException(name);
		}

		TaskState task = TaskState.create(remoteTask);
		activateTask(task);
		return task;
	}

	public void activateTask(TaskState newActiveTask) {
		setPaused(false);
		if (this.activeTask != null) {
			messageQueue.pushEvent(activeTask.getId(), EventType.DEACTIVATE, "Task-Switch: [" + activeTask.getName() + "] to [" + newActiveTask.getName() + "]");
			messageQueue.pushEvent(newActiveTask.getId(), EventType.ACTIVATE, "Task-Switch: [" + activeTask.getName() + "] to [" + newActiveTask.getName() + "]");
		} else {
			messageQueue.pushEvent(newActiveTask.getId(), EventType.ACTIVATE, "Task-Start: [" + newActiveTask.getName() + "]");
		}

		setActiveTask(newActiveTask);
	}

	public void shutdown() {
		if (this.activeTask != null) {
			messageQueue.pushEvent(activeTask.getId(), EventType.DEACTIVATE, "IDE Shutdown");
		}
	}

	public TaskState clearActiveTask() {
		TaskState oldActiveTask = activeTask;
		activeTask = null;
		return oldActiveTask;
	}

	public boolean hasActiveTask() {
		return activeTask != null;
	}

	public TaskState getActiveTask() {
		return activeTask;
	}

	public Long getActiveTaskId() {
		final TaskState task = activeTask;
		return (task == null ? null : task.getId());
	}

	public String getActiveTaskName() {
		final TaskState task = activeTask;
		return (task == null ? null : task.getName());
	}

	public void createPain(String painMessage) {
		addUnresolvedPain(painMessage);
		createEvent(painMessage, EventType.WTF);
	}

	private void addUnresolvedPain(String painMessage) {
		activeTask.addPainfulTroubleshootingEvent(painMessage);
	}

	public void resolveWithYay(String yayMessage) {
		if (yayMessage.contains(SharedTags.RESOLVE_TROUBLESHOOTING_JOURNEY)) {
			activeTask.clearTroubleshootingEventList();
		} else {
			activeTask.addAwesomeTroubleshootingEvent(yayMessage);
		}

		createEvent(yayMessage, EventType.AWESOME);
	}

	public void createPainSnippet(String painMessage, String source, String snippet) {
		addUnresolvedPain(painMessage);
		messageQueue.pushSnippet(activeTask.getId(), EventType.WTF, painMessage, source, snippet);
	}

	public void resolveWithAwesomeSnippet(String awesomeMessage, String source, String snippet) {
		activeTask.clearTroubleshootingEventList();
		messageQueue.pushSnippet(activeTask.getId(), EventType.AWESOME, awesomeMessage, source, snippet);
	}

	public void createEvent(String message, EventType eventType) {
		messageQueue.pushEvent(activeTask.getId(), eventType, message);
	}

}
