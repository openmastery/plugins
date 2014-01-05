package com.ideaflow.model

class IdeaFlowModel {

	List itemList = []
    String fileName

    boolean isPaused = false
    Date created

    private boolean openConflict = false

    IdeaFlowModel(String fileName, Date created) {
        this.fileName = fileName
        this.created = created
    }

	void addEvent(Conflict conflict) {
		addItem(conflict) {
			openConflict = true
		}
	}

	void addEvent(Resolution resolution) {
		addItem(resolution) {
			openConflict = false
		}
	}

    void addEvent(Event event) {
		addItem(event)
    }

	void addInterval(Interval interval) {
		addItem(interval)
    }

	private void addItem(def item, Closure action = null) {
		if (item && !isPaused) {
			itemList.add(item)

			if (action) {
				action()
			}
		}
	}

    int size() {
        itemList.size()
    }

    boolean isOpenConflict() {
        openConflict
    }

}
