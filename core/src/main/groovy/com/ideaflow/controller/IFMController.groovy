package com.ideaflow.controller

import com.ideaflow.activity.ActivityHandler
import com.ideaflow.activity.BatchPublisher
import com.ideaflow.activity.MessageQueue
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

	private boolean paused = true
	private IdeaFlowClient ideaFlowClient
	private EventClient eventClient
	private TaskClient taskClient
	private ActivityClient activityClient
	private Task activeTask
	private ActivityHandler activityHandler
	private MessageQueue messageQueue
	private BatchPublisher batchPublisher

	IFMController() {
		File messageQueueDir = createMessageQueueDir()
		batchPublisher = new BatchPublisher(messageQueueDir)
		messageQueue = new MessageQueue(this, batchPublisher, messageQueueDir)

		activityHandler = new ActivityHandler(this, messageQueue)

		new Thread(batchPublisher).start()
		startPushModificationActivityTimer(30)
	}

	private File createMessageQueueDir() {
		File queueDir = new File(System.getProperty("user.home") + File.separator + ".ideaflow");
		queueDir.mkdirs()
		return queueDir
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

		ideaFlowClient = new IdeaFlowClient(apiUrl)
				.apiKey(apiKey)
		eventClient = new EventClient(apiUrl)
				.apiKey(apiKey)
		taskClient = new TaskClient(apiUrl)
				.apiKey(apiKey)
		activityClient = new ActivityClient(apiUrl)
				.apiKey(apiKey)
		batchPublisher.setActivityClient(activityClient)
	}

//	private void assertValidApiUrlAndKey(String apiUrl, String apiKey) {
//		ActivityClient activityClient = new ActivityClient(apiUrl)
//				.apiKey(apiKey)
//
//		try {
//			activityClient.addActivityBatch(NewActivityBatch.builder()
//					.timeSent(LocalDateTime.now())
//					.build()
//			)
//		} catch (ConnectException ex) {
//			// TODO: what about offline?  what if the API key is invalid?
//			throw new FailedToConnectException(apiUrl)
//		} catch (WebApplicationException ex) {
//			if (ex.response.status == HttpStatus.SC_FORBIDDEN) {
//				throw new InvalidApiKeyException(apiKey)
//			}
//			throw ex
//		} catch (Exception ex) {
//			throw ex
//		}
//	}

	boolean isEnabled() {
		true
	}

	boolean isPaused() {
		paused
	}

	void setPaused(boolean paused) {
		this.paused = paused
	}

	boolean isRecording() {
		(paused == false)
	}

	boolean isTaskActive() {
		(activeTask != null)
	}

	void setActiveTask(Task activeTask) {

			this.activeTask = activeTask

	}

	Task getActiveTask() {
		activeTask
	}

	Task newTask(String name, String description) {
		Task newTask = null
		// TODO: what to do on conflict?

		newTask = taskClient.createTask(name, description);
		setActiveTask(newTask)

		newTask
	}

	String getActiveTaskName() {
		activeTask?.name
	}

	void createEvent(String message, EventType eventType) {
		if (activeTask && message != null) {
			messageQueue.pushEvent(activeTask.id, eventType, message)
		}
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
