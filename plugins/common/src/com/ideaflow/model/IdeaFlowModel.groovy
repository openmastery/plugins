package com.ideaflow.model

import org.joda.time.DateTime

class IdeaFlowModel {

	List<ModelEntity> entityList = []
	boolean isPaused = false
	File file
	DateTime created

	private Conflict activeConflict = null

	IdeaFlowModel(File file, DateTime created) {
		this.file = file
		this.created = created
	}

	IdeaFlowModel() {}

	void addModelEntity(Conflict conflict) {
		addModelEntityInternal(conflict) {
			activeConflict = conflict
		}
	}

	void addModelEntity(Resolution resolution) {
		addModelEntityInternal(resolution) {
			activeConflict = null
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

	Conflict getActiveConflict() {
		activeConflict
	}

	boolean isOpenConflict() {
		activeConflict != null
	}

}
