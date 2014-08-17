package com.ideaflow.model

import org.joda.time.DateTime

class IdeaFlowModel {

	List<ModelEntity> entityList = []
	File file
	DateTime created

	private Conflict activeConflict = null
	private BandStart activeBandStart = null

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

	void addModelEntity(BandStart bandStart) {
		addModelEntityInternal(bandStart) {
			activeBandStart = bandStart
		}
	}

	void addModelEntity(BandEnd bandEnd) {
		addModelEntityInternal(bandEnd) {
			activeBandStart = null
		}
	}

	void addModelEntity(ModelEntity modelEntity) {
		addModelEntityInternal(modelEntity, null)
	}

	private void addModelEntityInternal(ModelEntity modelEntity, Closure action) {
		if (modelEntity) {
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

	BandStart getActiveBandStart() {
		activeBandStart
	}

	boolean isOpenConflict() {
		activeConflict != null
	}

}
