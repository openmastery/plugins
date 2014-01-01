package com.ideaflow.model

class IdeaFlowModel {

    List<Interval> intervalList = []
    List<Event> eventList = []
    String fileName

    boolean isPaused = false
    Date created

    private boolean openConflict = false

    IdeaFlowModel(String fileName, Date created) {
        this.fileName = fileName
        this.created = created
    }

    void addEvent(Event event) {
        if (event && !isPaused) {
            handleConflict(event)
            eventList.add(event)
            println(event)
        }
    }

    void addInterval(Interval interval) {
        if (interval && interval.name && !isPaused) {
            intervalList.add(interval)
            println(interval)
        }
    }

    int size() {
        intervalList.size() + eventList.size()
    }

    boolean isOpenConflict() {
        openConflict
    }

    Timeline createSequencedTimeline() {
        Timeline timeline = new Timeline()

        def sortedEvents = sortEvents()
        sortedEvents.each { item ->
            if (item instanceof Event) {
                timeline.addEvent(item.comment, item.type)
            } else if (item instanceof Interval) {
                timeline.addInterval(item.name, item.duration)
            }
        }
        return timeline
    }

    private List sortEvents() {
        def allEvents = []
        allEvents += intervalList
        allEvents += eventList
        allEvents.sort { event1, event2 ->
            int compare = event1.created <=> event2.created
            if (compare == 0) {
                compare = event1.class.name <=> event2.class.name
            }
            return compare
        }
        return allEvents
    }

    private void handleConflict(Event event) {
        if (event.type == EventType.startConflict) {
            openConflict = true
        } else if (event.type == EventType.endConflict) {
            openConflict = false
        }
    }



}
