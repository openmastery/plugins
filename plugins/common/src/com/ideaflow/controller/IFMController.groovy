package com.ideaflow.controller

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.dsl.IdeaFlowReader
import com.ideaflow.event.EventToEditorActivityHandler
import com.ideaflow.model.Conflict
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange
import com.ideaflow.model.StateChangeType
import org.joda.time.DateTime

class IFMController {

	private IdeaFlowModel ideaFlowModel
	private EventToEditorActivityHandler eventToIntervalHandler
	private IDEService ideService

	IFMController(IDEService ideService) {
		this.ideService = ideService
	}

	String promptForInput(String title, String message) {
		ideService.promptForInput(title, message)
	}

	boolean isIdeaFlowOpen() {
		ideaFlowModel != null
	}

	Conflict getActiveConflict() {
		return (isIdeaFlowOpen() ? ideaFlowModel.getActiveConflict() : null)
	}

	boolean isOpenConflict() {
		getActiveConflict() != null
	}

	void startConflict(String question) {
		if (question) {
			addModelEntity(new Conflict(question))
		}
	}

	void endConflict(String answer) {
		if (answer) {
			addModelEntity(new Resolution(answer))
		}
	}

	void addNote(comment) {
		if (comment) {
			addModelEntity(new Note(comment))
		}
	}

	void newIdeaFlow(File file) {
		file = addExtension(file)
		if (ideService.fileExists(file)) {
			println("Resuming existing IdeaFlow: ${file.absolutePath}")
			String xml = ideService.readFile(file)
			ideaFlowModel = new IdeaFlowReader().readModel(file, xml)
			ideaFlowModel.file = file
		} else {
			println("Creating new IdeaFlow: ${file.absolutePath}")
			ideService.createNewFile(file, "")
			ideaFlowModel = new IdeaFlowModel(file, new DateTime())
		}

		eventToIntervalHandler = new EventToEditorActivityHandler(ideaFlowModel)
		addStateChange(StateChangeType.startIdeaFlowRecording)
		startFileEventForCurrentFile()
	}

	void closeIdeaFlow() {
		if (ideaFlowModel) {
			endFileEvent(null)
			addStateChange(StateChangeType.stopIdeaFlowRecording)
			flush()

			ideaFlowModel = null
			eventToIntervalHandler = null
		}
	}

	void startFileEvent(String eventName) {
		eventToIntervalHandler?.startEvent(eventName)
		flush()
	}

	void activeEventModified() {
		eventToIntervalHandler?.activeEventModified()
	}

	void startFileEventForCurrentFile() {
		String fileName = ideService.getActiveFileSelection()
		startFileEvent(fileName)
	}

	void endFileEvent(String eventName) {
		eventToIntervalHandler?.endEvent(eventName)
	}

	void pause() {
		println("Paused")
		endFileEvent(null)
		flush()
		ideaFlowModel?.isPaused = true
	}

	void resume() {
		println("Resumed")
		ideaFlowModel?.isPaused = false
		startFileEventForCurrentFile()
	}

	boolean isPaused() {
		ideaFlowModel != null && ideaFlowModel.isPaused
	}

	private File addExtension(File file) {
		File fileWithExtension = file
		if (file.name.endsWith(".ifm") == false) {
			fileWithExtension = new File(file.absolutePath + ".ifm")
		}
		return fileWithExtension
	}

	private void flush() {
		if (ideaFlowModel) {
			String xml = new DSLTimelineSerializer().serialize(ideaFlowModel)
			ideService.writeFile(ideaFlowModel.file, xml)
		}
	}

	private void addStateChange(StateChangeType type) {
		addModelEntity(new StateChange(type))
	}

	private void addModelEntity(ModelEntity event) {
		endFileEvent(null)
		ideaFlowModel?.addModelEntity(event)
		flush()
		startFileEventForCurrentFile()
	}

}
