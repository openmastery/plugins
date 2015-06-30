package com.ideaflow.model

import com.ideaflow.model.entry.BandEnd
import com.ideaflow.model.entry.BandStart
import com.ideaflow.model.entry.Conflict
import com.ideaflow.model.entry.ModelEntry
import com.ideaflow.model.entry.Resolution
import org.joda.time.DateTime

class IdeaFlowModel {

	Task task

	List<ModelEntry> entryList = []
	boolean isPaused = false
	/**
	 * @deprecated
	 */
	File file
	DateTime created

	private Conflict activeConflict = null
	private BandStart activeBandStart = null


	IdeaFlowModel(Task task, DateTime created) {
		this.task = task
		this.created = created
	}

	/**
	 * @deprecated
	 * @param file
	 * @param created
	 */
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
			entryList.add(modelEntity)
			action?.call()
		}
	}

	int size() {
		entryList.size()
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
