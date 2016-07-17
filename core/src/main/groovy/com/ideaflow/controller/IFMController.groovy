package com.ideaflow.controller

import org.openmastery.publisher.api.ideaflow.IdeaFlowPartialCompositeState
import org.openmastery.publisher.api.ideaflow.IdeaFlowStateType
import org.openmastery.publisher.api.task.Task
import org.openmastery.publisher.client.ActivityClient
import org.openmastery.publisher.client.EventClient
import org.openmastery.publisher.client.IdeaFlowClient
import org.openmastery.publisher.client.TaskClient

class IFMController {
	/*
	private IdeaFlowModel ideaFlowModel
	private EventToEditorActivityHandler eventToIntervalHandler
	private IDEService<T> ideService
	private IFMWorkingSet workingSet
	private IdeaFlowFileClient client

	IFMController(IDEService<T> ideService) {
		this.ideService = ideService
		this.client = new IdeaFlowFileClient()
		this.workingSet = new IFMWorkingSet()
	}

	void addWorkingSetListener(IFMWorkingSetListener workingSetListener) {
		workingSet.addWorkingSetListener(workingSetListener)
	}

	List<Task> getWorkingSetTasks() {
		workingSet.getTasks()
	}

	void setWorkingSetTasks(List<Task> tasks) {
		workingSet.setTasks(tasks)
	}

	IdeaFlowModel getActiveIdeaFlowModel() {
		ideaFlowModel?.task ? ideaFlowModel : null
	}

	String getActiveIdeaFlowName() {
		activeIdeaFlowModel?.task?.taskId
	}

	boolean isIdeaFlowOpen() {
		activeIdeaFlowModel != null
	}

	Conflict getActiveConflict() {
		return (isIdeaFlowOpen() ? activeIdeaFlowModel.getActiveConflict() : null)
	}

	BandStart getActiveBandStart() {
		return (isIdeaFlowOpen() ? activeIdeaFlowModel.getActiveBandStart() : null)
	}

	boolean isOpenConflict() {
		getActiveConflict() != null
	}

	boolean isOpenBand() {
		getActiveBandStart() != null
	}

	void startConflict(T context, String question) {
		if (question) {
            boolean nested = isOpenBand()
			addModelEntry(context, new Conflict(question, nested))
		}
	}

	void endConflict(T context, String answer) {
		if (answer) {
			addModelEntry(context, new Resolution(answer))
		}
	}

	void startBand(T context, String comment, BandType bandType, boolean isLinkedToPreviousBand) {
		if (comment) {
			BandStart activeBandStart = getActiveBandStart()
			if (activeBandStart) {
				endBand(context, activeBandStart.type)
				isLinkedToPreviousBand = true
			}

			addModelEntry(context, new BandStart(bandType, comment, isLinkedToPreviousBand))
		}
	}

	void endBand(T context, BandType bandType) {
		if (bandType) {
			addModelEntry(context, new BandEnd(bandType))
		}
	}

	void addNote(T context, String comment) {
		if (comment) {
			addModelEntry(context, new Note(comment))
		}
	}

	void newIdeaFlow(T context, Task task) {

		suspendActiveIdeaFlow()

		ideaFlowModel = client.readModel(task)

		workingSet.setActiveTask(task)

		eventToIntervalHandler = new EventToEditorActivityHandler(ideaFlowModel)
		addStateChange(context, StateChangeType.startIdeaFlowRecording)
		startFileEventForCurrentFile(context)
	}

	void closeIdeaFlow(T context) {
		if (activeIdeaFlowModel) {
			suspendActiveIdeaFlow()

			workingSet.removeTask(ideaFlowModel.task)

			if (workingSet.isEmpty()) {
				ideaFlowModel = null
				eventToIntervalHandler = null
			} else {
				newIdeaFlow(context, workingSet.getTasks().first())
			}
		}
	}

	private void suspendActiveIdeaFlow() {
		if (activeIdeaFlowModel) {
			endFileEvent(null)
			flush()
		}
	}

    void flushActiveEvent() {
        eventToIntervalHandler?.flushActiveEvent()
    }


    void startFileEvent(String eventName) {
		eventToIntervalHandler?.startEvent(eventName)
		flush()
	}

	void fileModified(String eventName) {
		eventToIntervalHandler?.activeEventModified(eventName)
	}

	void startFileEventForCurrentFile(T context) {
		String fileName = ideService.getActiveFileSelection(context)
		startFileEvent(fileName)
	}

	void endFileEvent(String eventName) {
		eventToIntervalHandler?.endEvent(eventName)
	}

	void markActiveFileEventAsIdle(String comment) {
		eventToIntervalHandler?.endActiveEventAsIdle(comment)
	}

	void pause() {
		println("Paused")
		endFileEvent(null)
		flush()
		activeIdeaFlowModel?.isPaused = true
	}

	void resume(T context) {
		println("Resumed")
		activeIdeaFlowModel?.isPaused = false
		startFileEventForCurrentFile(context)
	}

	boolean isPaused() {
		activeIdeaFlowModel?.isPaused
	}

	private void flush() {
		if (activeIdeaFlowModel) {
			client.saveModel(activeIdeaFlowModel)
		}
	}

	private void addStateChange(T context, StateChangeType type) {
		addModelEntry(context, new StateChange(type))
	}

	private void addModelEntry(T context, ModelEntry event) {
		flushActiveEvent()
		activeIdeaFlowModel?.addModelEntry(event)
		flush()
		startFileEventForCurrentFile(context)
	}
*/

	private IdeaFlowClient ideaFlowClient
	private EventClient eventClient
	private TaskClient taskClient
	private ActivityClient activityClient
	private Task activeTask
	private IdeaFlowPartialCompositeState activeTaskState

	// TODO: remove this, url
	IFMController() {
		this("http://localhost:8080")
	}

	IFMController(String ifmUri) {
		ideaFlowClient = new IdeaFlowClient(ifmUri)
		eventClient = new EventClient(ifmUri)
		taskClient = new TaskClient(ifmUri)
		activityClient = new ActivityClient(ifmUri)
	}

	boolean isTaskActive() {
		activeTask != null
	}

	void setActiveTask(Task activeTask) {
		this.activeTask = activeTask
		this.activeTaskState = activeTask != null ? ideaFlowClient.getActiveState(activeTask.id) : null
	}

	Task getActiveTask() {
		activeTask
	}

	IdeaFlowPartialCompositeState getActiveTaskState() {
		activeTaskState
	}

	void newIdeaFlow(String name, String description) {
		// TODO: what to do on conflict?
		Task newTask = taskClient.createTask(name, description);
		setActiveTask(newTask)
	}

	String getActiveTaskName() {
		activeTask?.name
	}

	// TODO: this method shoudl go away - we should be returning the active state on each band transition
	private void setActiveTaskState() {
		activeTaskState = activeTask != null ? ideaFlowClient.getActiveState(activeTask.id) : null
	}

	void startConflict(String question) {
		if (activeTask) {
			ideaFlowClient.startConflict(activeTask.id, question)
			setActiveTaskState()
		}
	}

	void endConflict(String answer) {
		if (activeTask) {
			ideaFlowClient.endConflict(activeTask.id, answer)
			setActiveTaskState()
		}
	}

	void startBand(String comment, IdeaFlowStateType type) {
		if (activeTask) {
			ideaFlowClient.startBand(activeTask.id, comment, type)
			setActiveTaskState()
		}
	}

	void endBand(String comment, IdeaFlowStateType type) {
		if (activeTask) {
			ideaFlowClient.endBand(activeTask.id, comment, type)
			setActiveTaskState()
		}
	}

	void addNote(String message) {
		if (activeTask && message) {
			eventClient.addUserNote(activeTask.id, message)
			setActiveTaskState()
		}
	}

	List<Task> getRecentTasks() {
		taskClient.findRecentTasks(5)
	}

//	IdeaFlowPartialCompositeState getActiveTaskState() {
//		activeTaskState
////		activeTask ? ideaFlowClient.getActiveState(activeTask.id) : null
//	}

    void startFileEvent(String eventName) {
	    println "start file ${eventName}"
//		eventToIntervalHandler?.startEvent(eventName)
//		flush()
	}

	void fileModified(String eventName) {
		println "file modified ${eventName}"
//		eventToIntervalHandler?.activeEventModified(eventName)
	}

//	void startFileEventForCurrentFile(T context) {
//		String fileName = ideService.getActiveFileSelection(context)
//		startFileEvent(fileName)
//	}

	void endFileEvent(String eventName) {
		println "end file ${eventName}"
//		eventToIntervalHandler?.endEvent(eventName)
	}

	void markActiveFileEventAsIdle(String comment) {
		println "mark active file as idle ${comment}"
//		eventToIntervalHandler?.endActiveEventAsIdle(comment)
	}

//	void pause() {
////		println("Paused")
////		endFileEvent(null)
////		flush()
////		activeIdeaFlowModel?.isPaused = true
//	}
//
//	void resume(T context) {
////		println("Resumed")
////		activeIdeaFlowModel?.isPaused = false
////		startFileEventForCurrentFile(context)
//	}
//
//	boolean isPaused() {
////		activeIdeaFlowModel?.isPaused
//		false
//	}

}
