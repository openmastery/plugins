package com.ideaflow.controller

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.dsl.IdeaFlowReader
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
import org.joda.time.DateTime

class IFMController<T> implements GroovyInterceptable {

	private IdeaFlowModel ideaFlowModel
	private EventToEditorActivityHandler eventToIntervalHandler
	private IDEService<T> ideService

	IFMController(IDEService<T> ideService) {
		this.ideService = ideService
	}

	def invokeMethod(String name, args) {
		if (ideaFlowModel && !ideaFlowModel.file.exists()) {
			ideaFlowModel = null
		}

		metaClass.getMetaMethod(name, args).invoke(this, args)
	}

	String promptForInput(T context, String title, String message) {
		ideService.promptForInput(context, title, message)
	}

	String getActiveIdeaFlowName() {
		ideaFlowModel?.file?.name
	}

	boolean isIdeaFlowOpen() {
		ideaFlowModel != null
	}

	Conflict getActiveConflict() {
		return (isIdeaFlowOpen() ? ideaFlowModel.getActiveConflict() : null)
	}

	BandStart getActiveBandStart() {
		return (isIdeaFlowOpen() ? ideaFlowModel.getActiveBandStart() : null)
	}

	boolean isOpenConflict() {
		getActiveConflict() != null
	}

	boolean isOpenBand() {
		getActiveBandStart() != null
	}

	void startConflict(T context, String question) {
		if (question) {
			addModelEntity(context, new Conflict(question))
		}
	}

	void endConflict(T context, String answer) {
		if (answer) {
			addModelEntity(context, new Resolution(answer))
		}
	}

	void startBand(T context, String comment, BandType bandType) {
		if (comment) {
			addModelEntity(context, new BandStart(bandType, comment))
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

	void newIdeaFlow(T context, File file) {
		file = addExtension(file)
		if (ideService.fileExists(context, file)) {
			println("Resuming existing IdeaFlow: ${file.absolutePath}")
			String xml = ideService.readFile(context, file)
			ideaFlowModel = new IdeaFlowReader().readModel(file, xml)
			ideaFlowModel.file = file
		} else {
			println("Creating new IdeaFlow: ${file.absolutePath}")
			ideService.createNewFile(context, file, "")
			ideaFlowModel = new IdeaFlowModel(file, new DateTime())
		}

		eventToIntervalHandler = new EventToEditorActivityHandler(ideaFlowModel)
		addStateChange(context, StateChangeType.startIdeaFlowRecording)
		startFileEventForCurrentFile(context)
	}

	void closeIdeaFlow(T context) {
		if (ideaFlowModel) {
			endFileEvent(null)
			addStateChange(context, StateChangeType.stopIdeaFlowRecording)
			flush(context)

			ideaFlowModel = null
			eventToIntervalHandler = null
		}
	}

	void startFileEvent(T context, String eventName) {
		eventToIntervalHandler?.startEvent(eventName)
		flush(context)
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

	void pause(T context) {
		println("Paused")
		endFileEvent(null)
		flush(context)
		ideaFlowModel?.isPaused = true
	}

	void resume(T context) {
		println("Resumed")
		ideaFlowModel?.isPaused = false
		startFileEventForCurrentFile(context)
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

	private void flush(T context) {
		if (ideaFlowModel) {
			String xml = new DSLTimelineSerializer().serialize(ideaFlowModel)
			ideService.writeFile(context, ideaFlowModel.file, xml)
		}
	}

	private void addStateChange(T context, StateChangeType type) {
		addModelEntity(context, new StateChange(type))
	}

	private void addModelEntity(T context, ModelEntity event) {
		endFileEvent(null)
		ideaFlowModel?.addModelEntity(event)
		flush(context)
		startFileEventForCurrentFile(context)
	}

}
