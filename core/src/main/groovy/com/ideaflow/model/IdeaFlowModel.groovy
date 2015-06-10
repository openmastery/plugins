package com.ideaflow.model

import org.joda.time.DateTime

class IdeaFlowModel {

	List<ModelEntry> entityList = []
	boolean isPaused = false
	File file
	DateTime created

	private Conflict activeConflict = null
	private BandStart activeBandStart = null



	IdeaFlowModel(File file, DateTime created) {
		this.file = file
		this.created = created
	}

	IdeaFlowModel() {}

	void addModelEntry(Conflict conflict) {
		addModelEntityInternal(conflict) {
			activeConflict = conflict
		}
	}

	void addModelEntry(Resolution resolution) {
		addModelEntityInternal(resolution) {
			activeConflict = null
		}
	}

	void addModelEntry(BandStart bandStart) {
		addModelEntityInternal(bandStart) {
			activeBandStart = bandStart
		}
	}

	void addModelEntry(BandEnd bandEnd) {
		addModelEntityInternal(bandEnd) {
			activeBandStart = null
		}
	}

	void addModelEntry(ModelEntry modelEntity) {
		addModelEntityInternal(modelEntity, null)
	}

	private void addModelEntityInternal(ModelEntry modelEntity, Closure action) {
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

	BandStart getActiveBandStart() {
		activeBandStart
	}

	boolean isOpenConflict() {
		activeConflict != null
	}

}
