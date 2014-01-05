package com.ideaflow.event

import com.ideaflow.model.Interval
import com.ideaflow.model.TimeService
import com.ideaflow.model.IdeaFlowModel

class EventToIntervalHandler {

    private Event lastEvent
    private IdeaFlowModel model
    private TimeService timeService

    private static final String DONE_EVENT = "***DONE***"
    private static final int SHORTEST_INTERVAL = 3

    EventToIntervalHandler(TimeService timeService, IdeaFlowModel model) {
        this.model = model
        this.timeService = timeService
    }

    void startEvent(String eventName) {
        if (!eventName) {
            endEvent()
        }

        Event newEvent = createEvent(eventName)
        addIntervalForLastEvent(newEvent)

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
        addIntervalForLastEvent(doneEvent)

        lastEvent = null
    }

    private addIntervalForLastEvent(Event newEvent) {
        if (lastEvent) {
            addInterval(lastEvent, newEvent)
        }
    }

    private void addInterval(Event lastEvent, Event newEvent) {
        int duration = (newEvent.time - lastEvent.time) / 1000
        if (duration >= SHORTEST_INTERVAL && isDifferent(lastEvent, newEvent)) {
            model.addInterval(createInterval(lastEvent, duration))
        }
    }

    private boolean isSameAsLastEvent(String eventName) {
        eventName == null || (lastEvent != null && lastEvent.eventName == eventName)
    }

    private boolean isDifferent(Event lastEvent, Event newEvent) {
        lastEvent == null || newEvent == null || lastEvent.eventName != newEvent.eventName
    }

    private Interval createInterval(Event lastEvent, int duration) {
        new Interval(new Date(lastEvent.time), lastEvent.eventName, duration)
    }

    private Event createEvent(eventName) {
        new Event(eventName: eventName, time: timeService.getTime())
    }

    private static class Event {
        long time
        String eventName
    }
}