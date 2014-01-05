package com.ideaflow.model

class IdeaFlowModel {

	List entityList = []
    String fileName

    boolean isPaused = false
    Date created

    private boolean openConflict = false

    IdeaFlowModel(String fileName, Date created) {
        this.fileName = fileName
        this.created = created
    }

	void addEvent(Conflict conflict) {
		addModelEntity(conflict) {
			openConflict = true
		}
	}

	void addEvent(Resolution resolution) {
		addModelEntity(resolution) {
			openConflict = false
		}
	}

    void addEvent(GenericEvent event) {
		addModelEntity(event)
    }

	void addInterval(Interval interval) {
		addModelEntity(interval)
    }

	private void addModelEntity(ModelEntity modelEntity, Closure action = null) {
		if (modelEntity && !isPaused) {
			entityList.add(modelEntity)
			action?.call()
		}
	}

    int size() {
        entityList.size()
    }

    boolean isOpenConflict() {
        openConflict
    }

}
