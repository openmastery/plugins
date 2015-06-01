package com.ideaflow.controller

import com.ideaflow.dsl.IIdeaFlowClient
import com.ideaflow.dsl.TaskId
import com.ideaflow.event.EventToEditorActivityHandler
import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange
import com.ideaflow.model.StateChangeType

class IFMController<T> {

	private IdeaFlowModel ideaFlowModel
	private EventToEditorActivityHandler eventToIntervalHandler
	private IDEService<T> ideService
	private IFMTaskList taskList
	private IIdeaFlowClient ideaFlowClient

	IFMController(IDEService<T> ideService) {
		this.ideService = ideService
		this.taskList = new IFMTaskList()
	}

	void addTaskListListener(IFMTaskListListener taskListListener) {
		taskList.addTasksListener(taskListListener)
	}

	List<TaskId> getWorkingSet() {
		taskList.getTaskList()
	}

	void setWorkingSet(List<TaskId> tasks) {
		taskList.setTaskList(tasks)
	}

	IdeaFlowModel getActiveIdeaFlowModel() {
		ideaFlowModel?.taskId?.value ? ideaFlowModel : null
	}

	String promptForInput(T context, String title, String message) {
		ideService.promptForInput(context, title, message)
	}

	String getActiveIdeaFlowName() {
		activeIdeaFlowModel?.taskId?.value
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
			addModelEntity(context, new Conflict(question, nested))
		}
	}

	void endConflict(T context, String answer) {
		if (answer) {
			addModelEntity(context, new Resolution(answer))
		}
	}

	void startBand(T context, String comment, BandType bandType, boolean isLinkedToPreviousBand) {
		if (comment) {
			BandStart activeBandStart = getActiveBandStart()
			if (activeBandStart) {
				endBand(context, activeBandStart.type)
				isLinkedToPreviousBand = true
			}

			addModelEntity(context, new BandStart(bandType, comment, isLinkedToPreviousBand))
		}
	}

	void endBand(T context, BandType bandType) {
		if (bandType) {
			addModelEntity(context, new BandEnd(bandType))
		}
	}

	void addNote(T context, String comment) {
		if (comment) {
			addModelEntity(context, new Note(comment))
		}
	}

	void newIdeaFlow(T context, TaskId taskId) {
		suspendActiveIdeaFlow(context)

		ideaFlowModel = ideaFlowClient.readModel(taskId)

		taskList.setActiveTask(taskId)

		eventToIntervalHandler = new EventToEditorActivityHandler(ideaFlowModel)
		addStateChange(context, StateChangeType.startIdeaFlowRecording)
		startFileEventForCurrentFile(context)
	}

	void closeIdeaFlow(T context) {
		if (activeIdeaFlowModel) {
			suspendActiveIdeaFlow(context)

			taskList.removeTask(ideaFlowModel.taskId)

			if (taskList.isEmpty()) {
				ideaFlowModel = null
				eventToIntervalHandler = null
			} else {
				newIdeaFlow(context, taskList.getTaskList().first())
			}
		}
	}

	private void suspendActiveIdeaFlow(T context) {
		if (activeIdeaFlowModel) {
			endFileEvent(null)
			flush()
		}
	}

    void flushActiveEvent() {
        eventToIntervalHandler?.flushActiveEvent()
    }


    void startFileEvent(T context, String eventName) {
		eventToIntervalHandler?.startEvent(eventName)
		flush()
	}

	void fileModified(String eventName) {
		eventToIntervalHandler?.activeEventModified(eventName)
	}

	void startFileEventForCurrentFile(T context) {
		String fileName = ideService.getActiveFileSelection(context)
		startFileEvent(context, fileName)
	}

	void endFileEvent(String eventName) {
		eventToIntervalHandler?.endEvent(eventName)
	}

	void markActiveFileEventAsIdle(String comment) {
		eventToIntervalHandler?.endActiveEventAsIdle(comment)
	}

	void pause(T context) {
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
			ideaFlowClient.writeModel(activeIdeaFlowModel)
		}
	}

	private void addStateChange(T context, StateChangeType type) {
		addModelEntity(context, new StateChange(type))
	}

	private void addModelEntity(T context, ModelEntity event) {
		flushActiveEvent()
		activeIdeaFlowModel?.addModelEntity(event)
		flush()
		startFileEventForCurrentFile(context)
	}

}
