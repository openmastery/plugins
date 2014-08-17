package com.ideaflow.event

import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Idle
import org.joda.time.DateTime

class EventToEditorActivityHandler {

	private Event activeEvent
	private EditorActivity lastEditorActivity
	private IdeaFlowModel model

	private static final String DONE_EVENT = "***DONE***"
	private static final int SHORTEST_ACTIVITY = 3

	EventToEditorActivityHandler(IdeaFlowModel model) {
		this.model = model
	}

	void startEvent(String eventName) {
		if (!eventName) {
			endEvent()
		}

		Event newEvent = createEvent(eventName)
		addEditorActivityForActiveEvent(newEvent)

		if (isDifferent(activeEvent, newEvent)) {
			activeEvent = newEvent
		}
	}

	void activeEventModified(String eventName) {
		if (eventName == activeEvent?.eventName) {
			activeEvent.modified = true
		}
	}

	void endEvent(String eventName) {
		if (isSameAsActiveEvent(eventName)) {
			endEvent()
		}
	}

	void endEvent() {
		Event doneEvent = createEvent(DONE_EVENT)
		addEditorActivityForActiveEvent(doneEvent)

		activeEvent = null
	}

	void endActiveEventAsIdle(String comment) {
		if (activeEvent) {
			int duration = (DateTime.now().millis - activeEvent.time.millis) / 1000
			Idle idle = new Idle(activeEvent.time, comment, duration)
			model.addModelEntity(idle)

			activeEvent = null
			lastEditorActivity = null
		}
	}

	private addEditorActivityForActiveEvent(Event newEvent) {
		if (activeEvent) {
			addEditorActivity(activeEvent, newEvent)
		}
	}

	private void addEditorActivity(Event oldEvent, Event newEvent) {
		int duration = (newEvent.time.millis - oldEvent.time.millis) / 1000
		if (shouldRecordEvent(oldEvent, newEvent, duration)) {
			EditorActivity editorActivity = createEditorActivity(oldEvent, duration)
			if (isEquivalentActivity(editorActivity, lastEditorActivity)) {
				lastEditorActivity.duration += editorActivity.duration
			} else {
				model.addModelEntity(editorActivity)
				lastEditorActivity = editorActivity
			}
		}
	}

	private boolean shouldRecordEvent(Event oldEvent, Event newEvent, int duration) {
		isDifferent(oldEvent, newEvent) && (oldEvent.modified || (duration >= SHORTEST_ACTIVITY))
	}

	private boolean isEquivalentActivity(EditorActivity activity1, EditorActivity activity2) {
		if (activity1 && activity2) {
			return (activity1.name == activity2.name) && (activity1.modified == activity2.modified)
		} else {
			return false
		}
	}

	private boolean isSameAsActiveEvent(String eventName) {
		eventName == null || (activeEvent != null && activeEvent.eventName == eventName)
	}

	private boolean isDifferent(Event lastEvent, Event newEvent) {
		lastEvent == null || newEvent == null || lastEvent.eventName != newEvent.eventName
	}

	private EditorActivity createEditorActivity(Event lastEvent, int duration) {
		new EditorActivity(lastEvent.time, lastEvent.eventName, lastEvent.modified, duration)
	}

	private Event createEvent(eventName) {
		new Event(eventName: eventName, time: new DateTime(), modified: false)
	}

	private static class Event {
		DateTime time
		String eventName
		boolean modified
	}
}
