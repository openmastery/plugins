package com.ideaflow.timeline

import com.ideaflow.model.BandType
import org.joda.time.DateTime
import sun.net.www.content.text.Generic

class TimeBandContainer extends AbstractTimeBand {

	private GenericBand primaryGenericBand
	private ConflictBand primaryConflict
	private List<TimeBand> timeBands = []

	TimeBandContainer(GenericBand primaryGenericBand) {
		this(primaryGenericBand, null)
	}

	TimeBandContainer(GenericBand primaryGenericBand, ConflictBand primaryConflict) {
		this.primaryConflict = primaryConflict
		this.primaryGenericBand = primaryGenericBand
		addTimeBand(primaryGenericBand)
		if (primaryConflict != null) {
			addTimeBand(primaryConflict)
		}
	}

	GenericBand getPrimaryGenericBand() {
		primaryGenericBand
	}

	ConflictBand getPrimaryConflict() {
		primaryConflict
	}

	void addTimeBand(TimeBand timeBand) {
		timeBands.add(timeBand)
	}

	List<TimeBand> getTimeBands() {
		timeBands.clone() as List
	}

	boolean isEmpty() {
		timeBands.size() == (primaryConflict ? 2 : 1)
	}

	@Override
	protected void setActivityStartCreated(DateTime created) {
		// TODO: should this explode?
	}

	@Override
	protected void setActivityEndCreated(DateTime created) {
		// TODO: should this explode?
	}

	@Override
	BandType getBandType() {
		(primaryConflict ?: primaryGenericBand).bandType
	}

	@Override
	String getId() {
		"${primaryGenericBand.id}-container"
	}

	@Override
	String getComment() {
		primaryGenericBand.comment
	}

	String toString() {
		"Container: ${primaryGenericBand.comment}"
	}
}
