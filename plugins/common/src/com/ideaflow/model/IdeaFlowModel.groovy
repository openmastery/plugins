package com.ideaflow.model

class IdeaFlowModel {

    List<Interval> intervalList = []
    List<Event> eventList = []
	List intervalAndEventList = []
    String fileName

    boolean isPaused = false
    Date created

    private boolean openConflict = false

    IdeaFlowModel(String fileName, Date created) {
        this.fileName = fileName
        this.created = created
    }

    void addTimelineEvent(Event event) {
        if (event && !isPaused) {
            handleConflict(event)
            eventList.add(event)
			intervalAndEventList.add(event)
        }
    }

    void addInterval(Interval interval) {
        if (interval && interval.name && !isPaused) {
            intervalList.add(interval)
			intervalAndEventList.add(interval)
        }
    }

    int size() {
        intervalList.size() + eventList.size()
    }

    boolean isOpenConflict() {
        openConflict
    }

    private void handleConflict(Event event) {
        if (event.type == EventType.startConflict) {
            openConflict = true
        } else if (event.type == EventType.endConflict) {
            openConflict = false
        }
    }

}
