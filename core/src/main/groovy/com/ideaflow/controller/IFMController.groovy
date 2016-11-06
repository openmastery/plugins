package com.ideaflow.controller

import com.ideaflow.activity.ActivityHandler
import com.ideaflow.activity.BatchSender
import com.ideaflow.activity.IFMLogger
import org.apache.http.HttpStatus
import org.joda.time.LocalDateTime
import org.openmastery.publisher.api.activity.NewActivityBatch
import org.openmastery.publisher.api.event.EventType
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.ActivityClient
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.client.TaskClient

import javax.ws.rs.WebApplicationException

class IFMController {

	private boolean enabled = false
	private boolean paused = true
	private IdeaFlowClient ideaFlowClient
	private EventClient eventClient
	private TaskClient taskClient
	private ActivityClient activityClient
	private Task activeTask
	private ActivityHandler activityHandler
	private BatchSender batchSender

	IFMController() {
		activityHandler = new ActivityHandler(this)
		batchSender = new BatchSender()

		new Thread(batchSender).start()
		startPushModificationActivityTimer(30)
	}

	private void startPushModificationActivityTimer(final long intervalInSeconds) {
		TimerTask timerTask = new TimerTask() {
			@Override
			void run() {
				activityHandler.pushModificationActivity(intervalInSeconds)
			}
		}

		long intervalInMillis = intervalInSeconds * 1000
		new Timer().scheduleAtFixedRate(timerTask, intervalInMillis, intervalInMillis)
	}

	ActivityHandler getActivityHandler() {
		activityHandler
	}

	void initClients(String apiUrl, String apiKey) {
		try {
			assertValidApiUrlAndKey(apiUrl, apiKey)
		} catch (Exception ex) {
			enabled = false
			throw ex
		}

		ideaFlowClient = new IdeaFlowClient(apiUrl)
				.apiKey(apiKey)
		eventClient = new EventClient(apiUrl)
				.apiKey(apiKey)
		taskClient = new TaskClient(apiUrl)
				.apiKey(apiKey)
		activityClient = new ActivityClient(apiUrl)
				.apiKey(apiKey)
		batchSender.setActivityClient(activityClient)
		enabled = true
	}

	private void assertValidApiUrlAndKey(String apiUrl, String apiKey) {
		ActivityClient activityClient = new ActivityClient(apiUrl)
				.apiKey(apiKey)

		try {
			activityClient.addActivityBatch(NewActivityBatch.builder()
					.timeSent(LocalDateTime.now())
					.build()
			)
		} catch (ConnectException ex) {
			// TODO: what about offline?  what if the API key is invalid?
			throw new FailedToConnectException(apiUrl)
		} catch (WebApplicationException ex) {
			if (ex.response.status == HttpStatus.SC_FORBIDDEN) {
				throw new InvalidApiKeyException(apiKey)
			}
			throw ex
		} catch (Exception ex) {
			throw ex
		}
	}

	boolean isEnabled() {
		enabled
	}

	boolean isPaused() {
		paused
	}

	void setPaused(boolean paused) {
		this.paused = paused
	}

	boolean isRecording() {
		enabled && (paused == false)
	}

	boolean isTaskActive() {
		enabled && (activeTask != null)
	}

	void setActiveTask(Task activeTask) {
		if (enabled) {
			this.activeTask = activeTask
		}
	}

	Task getActiveTask() {
		activeTask
	}

	Task newTask(String name, String description) {
		Task newTask = null
		if (enabled) {
			// TODO: what to do on conflict?
			newTask = taskClient.createTask(name, description);
			setActiveTask(newTask)
		}
		newTask
	}

	String getActiveTaskName() {
		activeTask?.name
	}

	void createEvent(String message, EventType eventType) {
		activityHandler.createEvent(message, eventType)
	}


	private static class InvalidApiKeyException extends RuntimeException {
		InvalidApiKeyException(String apiKey) {
			super("The server did not recognize the provided API Key '${apiKey}'")
		}
	}

	private static class FailedToConnectException extends RuntimeException {
		FailedToConnectException(String apiUrl) {
			super("Failed to connect to server, url=${apiUrl}")
		}
	}


}
