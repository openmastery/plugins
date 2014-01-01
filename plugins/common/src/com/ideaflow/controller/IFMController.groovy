package com.ideaflow.controller

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.dsl.IdeaFlowReader
import com.ideaflow.event.EventToIntervalHandler
import com.ideaflow.model.Event
import com.ideaflow.model.TimeService
import com.ideaflow.model.IdeaFlowModel

import com.ideaflow.model.EventType

class IFMController {


    private IdeaFlowModel ideaFlowModel
    private TimeService timeService
    private EventToIntervalHandler eventToIntervalHandler
    private IDEService ideService

    IFMController(TimeService timeService, IDEService ideService) {
        this.timeService = timeService
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

    void startConflict(comment) {
        addEvent(EventType.startConflict, comment)
    }

    void endConflict(comment) {
        addEvent(EventType.endConflict, comment)
    }

    void addNote(comment) {
        addEvent(EventType.note, comment)
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
            ideaFlowModel = new IdeaFlowModel(relativePath, new Date(timeService.time))
        }

        eventToIntervalHandler = new EventToIntervalHandler(timeService, ideaFlowModel)
        addEvent(EventType.open, "Start IdeaFlow recording")
        startFileEventForCurrentFile()
    }

    void closeIdeaFlow() {
        if (ideaFlowModel) {
            endFileEvent(null)
            addEvent(EventType.closed, "Stop IdeaFlow recording")
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

    private void addEvent(EventType type, String comment) {
        if (comment) {
            endFileEvent(null)
            ideaFlowModel?.addTimelineEvent(new Event(type, comment))
            flush()
            startFileEventForCurrentFile()
        }
    }

}
