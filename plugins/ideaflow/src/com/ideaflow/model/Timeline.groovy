package com.ideaflow.model

class Timeline {
    List<TimelineEvent> events = []

    boolean openConflict = false

    void addInterval(String name, int duration) {
        TimelineEvent lastEvent = getLastEvent()
        if (isSame(lastEvent, name)) {
            lastEvent.duration += duration
        } else {
            def event = new TimelineEvent(eventTime: getTimelineEnd(), text: name,
                    type: EventType.interval, duration: duration, conflict: openConflict)
            events.add(event)
        }
    }

    void addEvent(String note, EventType type) {
        if (type == EventType.startConflict) {
            openConflict = true
        } else if (type == EventType.endConflict) {
            openConflict = false
        }
        if (EventType.isFlowType(type)) {
            def event = new TimelineEvent(eventTime: getTimelineEnd(), text: note, type: type, duration: 0, conflict: isConflict(type))
            events.add(event)
        }
    }

    long getTimelineEnd() {
        long end = 0
        TimelineEvent lastEvent = getLastEvent()
        if (lastEvent) {
            end = lastEvent.eventTime + lastEvent.duration
        }
        return end
    }

    int size() {
        events.size()
    }

    List<Conflict> groupByConflict() {
        List<Conflict> conflicts = []
        Conflict activeConflict = null
        events.each { event ->
            if (event.type == EventType.startConflict) {
                activeConflict = new Conflict(eventTime: event.eventTime, conflictNote: event.text)
                conflicts.add(activeConflict)
            } else if (event.type == EventType.endConflict) {
                activeConflict.resolveNote = event.text
                activeConflict = null
            } else if (event.type == EventType.interval && activeConflict) {
                activeConflict.duration += event.duration
            }
        }
        return conflicts
    }

    Map<String, Integer> groupByIntervalName() {
        Map<String, Integer> intervalsByName = [:]
        events.each { event ->
            if (event.type == EventType.interval) {
                Integer duration = intervalsByName.get(event.text)
                if (duration == null) {
                    duration = 0
                }
                intervalsByName.put(event.text, duration + event.duration)
            }
        }
        return intervalsByName
    }

    Integer getTotalNonConflict() {
        int total = 0
        events.each { event ->
            if (!event.conflict) {
                total += event.duration
            }
        }
        return total
    }

    private isConflict(EventType type) {
        type == EventType.startConflict || type == EventType.endConflict || openConflict
    }

    private boolean isSame(TimelineEvent priorEvent, String newName) {
        priorEvent?.text == newName && priorEvent?.conflict == openConflict
    }

    private TimelineEvent getLastEvent() {
        TimelineEvent event = null
        if (events.size() > 0) {
            event = events.last()
        }
        return event
    }

    static class TimelineEvent {
        long eventTime
        int duration
        EventType type
        String text
        boolean conflict
    }

    static class Conflict {
        long eventTime
        int duration
        String conflictNote
        String resolveNote
    }
}
