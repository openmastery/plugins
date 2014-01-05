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

	void addModelEntity(Conflict conflict) {
		addModelEntityInternal(conflict) {
			openConflict = true
		}
	}

	void addModelEntity(Resolution resolution) {
		addModelEntityInternal(resolution) {
			openConflict = false
		}
	}

	void addModelEntity(ModelEntity modelEntity) {
		addModelEntityInternal(modelEntity, null)
	}

	private void addModelEntityInternal(ModelEntity modelEntity, Closure action) {
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
