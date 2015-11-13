package com.ideaflow.controller

import com.ideaflow.dsl.client.IdeaFlowFileClient
import com.ideaflow.event.EventToEditorActivityHandler
import com.ideaflow.model.BandType
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.StateChangeType
import com.ideaflow.model.Task
import com.ideaflow.model.entry.BandEnd
import com.ideaflow.model.entry.BandStart
import com.ideaflow.model.entry.Conflict
import com.ideaflow.model.entry.ModelEntry
import com.ideaflow.model.entry.Note
import com.ideaflow.model.entry.Resolution
import com.ideaflow.model.entry.StateChange

class IFMController<T> {

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

	String promptForInput(T context, String title, String message) {
		ideService.promptForInput(context, title, message)
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

		suspendActiveIdeaFlow(context)

		ideaFlowModel = client.readModel(task)

		workingSet.setActiveTask(task)

		eventToIntervalHandler = new EventToEditorActivityHandler(ideaFlowModel)
		addStateChange(context, StateChangeType.startIdeaFlowRecording)
		startFileEventForCurrentFile(context)
	}

	void closeIdeaFlow(T context) {
		if (activeIdeaFlowModel) {
			suspendActiveIdeaFlow(context)

			workingSet.removeTask(ideaFlowModel.task)

			if (workingSet.isEmpty()) {
				ideaFlowModel = null
				eventToIntervalHandler = null
			} else {
				newIdeaFlow(context, workingSet.getTasks().first())
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

}
