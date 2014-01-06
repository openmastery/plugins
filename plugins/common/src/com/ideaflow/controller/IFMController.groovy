package com.ideaflow.controller

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.dsl.IdeaFlowReader
import com.ideaflow.event.EventToEditorActivityHandler
import com.ideaflow.model.Conflict
import com.ideaflow.model.StateChange
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.IdeaFlowModel

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

    void validateFilePath(String relativePath) {
        String pathWithExtension = addExtension(relativePath)
        ideService.validateFilePath(pathWithExtension)
    }

    boolean isIdeaFlowOpen() {
        ideaFlowModel != null
    }

    boolean isOpenConflict() {
        isIdeaFlowOpen() && ideaFlowModel.isOpenConflict()
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

    void newIdeaFlow(String relativePath) {
		relativePath = addExtension(relativePath)
        if (ideService.fileExists(relativePath)) {
            println("Resuming existing IdeaFlow: $relativePath")
            String xml = ideService.readFile(relativePath)
            ideaFlowModel = new IdeaFlowReader().readModel(xml)
            ideaFlowModel.fileName = relativePath
        } else {
            println("Creating new IdeaFlow: $relativePath")
            ideService.createNewFile(relativePath, "")
            ideaFlowModel = new IdeaFlowModel(relativePath, new DateTime())
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
	
	private String addExtension(String fileName) {
		String nameWithExtension = fileName
		if (fileName != null && !fileName.endsWith(".ifm")) {
			nameWithExtension = fileName + ".ifm"
		}
		return nameWithExtension
	}

    private void flush() {
        if (ideaFlowModel) {
            String xml = new DSLTimelineSerializer().serialize(ideaFlowModel)
            ideService.writeToFile(ideaFlowModel.fileName, xml)
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
