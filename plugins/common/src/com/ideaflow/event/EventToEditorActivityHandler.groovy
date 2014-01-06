package com.ideaflow.event

import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import org.joda.time.DateTime

class EventToEditorActivityHandler {

    private Event lastEvent
    private IdeaFlowModel model

    private static final String DONE_EVENT = "***DONE***"
    private static final int SHORTEST_INTERVAL = 3

	EventToEditorActivityHandler(IdeaFlowModel model) {
        this.model = model
    }

    void startEvent(String eventName) {
        if (!eventName) {
            endEvent()
        }

        Event newEvent = createEvent(eventName)
        addEditorActivityForLastEvent(newEvent)

        if (isDifferent(lastEvent, newEvent)) {
            lastEvent = newEvent
        }
    }

    void endEvent(String eventName) {
        if (isSameAsLastEvent(eventName)) {
            endEvent()
        }
    }

    void endEvent() {
        Event doneEvent = createEvent(DONE_EVENT)
        addEditorActivityForLastEvent(doneEvent)

        lastEvent = null
    }

    private addEditorActivityForLastEvent(Event newEvent) {
        if (lastEvent) {
            addEditorActivity(lastEvent, newEvent)
        }
    }

    private void addEditorActivity(Event lastEvent, Event newEvent) {
        int duration = (newEvent.time.millis - lastEvent.time.millis) / 1000
        if (duration >= SHORTEST_INTERVAL && isDifferent(lastEvent, newEvent)) {
            model.addModelEntity(createEditorActivity(lastEvent, duration))
        }
    }

    private boolean isSameAsLastEvent(String eventName) {
        eventName == null || (lastEvent != null && lastEvent.eventName == eventName)
    }

    private boolean isDifferent(Event lastEvent, Event newEvent) {
        lastEvent == null || newEvent == null || lastEvent.eventName != newEvent.eventName
    }

    private EditorActivity createEditorActivity(Event lastEvent, int duration) {
        new EditorActivity(lastEvent.time, lastEvent.eventName, duration)
    }

    private Event createEvent(eventName) {
        new Event(eventName: eventName, time: new DateTime())
    }

    private static class Event {
        DateTime time
        String eventName
    }
}
