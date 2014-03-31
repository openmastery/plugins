package test.support

import com.ideaflow.model.BandType
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.StateChangeType
import org.joda.time.DateTime

class IdeaFlowModelBuilder {

	IdeaFlowModel ifm = new IdeaFlowModel()
	FixtureSupport fs = new FixtureSupport()

	static IdeaFlowModelBuilder create() {
		return new IdeaFlowModelBuilder()
	}

	IdeaFlowModelBuilder defaults() {
		ifm.created = new DateTime(fs.NOW)
		ifm.fileName = fs.FILE
		return this
	}

	IdeaFlowModelBuilder addBandEnd() {
		ifm.addModelEntity(fs.createBandEnd())
		return this
	}

	IdeaFlowModelBuilder addBandEnd(BandType type, long time) {
		ifm.addModelEntity(fs.createBandEnd(type, time))
		return this
	}

	IdeaFlowModelBuilder addBandStart() {
		ifm.addModelEntity(fs.createBandStart())
		return this
	}

	IdeaFlowModelBuilder addBandStart(BandType type, long time) {
		ifm.addModelEntity(fs.createBandStart(type, time))
		return this
	}

	IdeaFlowModelBuilder addConflict() {
		ifm.addModelEntity(fs.createConflict())
		return this
	}

	IdeaFlowModelBuilder addConflict(long time) {
		ifm.addModelEntity(fs.createConflict(time))
		return this
	}

	IdeaFlowModelBuilder addEditorActivity(String name) {
		ifm.addModelEntity(fs.createEditorActivity(name))
		return this
	}

	IdeaFlowModelBuilder addEditorActivity(String name, int duration, long time) {
		ifm.addModelEntity(fs.createEditorActivity(name, duration, time))
		return this
	}

	IdeaFlowModelBuilder addEditorActivity(String name, long time) {
		ifm.addModelEntity(fs.createEditorActivity(name, time))
		return this
	}

	IdeaFlowModelBuilder addNote(String comment) {
		ifm.addModelEntity(fs.createNote(comment))
		return this
	}

	IdeaFlowModelBuilder addNote(String comment, long time) {
		ifm.addModelEntity(fs.createNote(comment, time))
		return this
	}

	IdeaFlowModelBuilder addResolution() {
		ifm.addModelEntity(fs.createResolution())
		return this
	}

	IdeaFlowModelBuilder addResolution(long time) {
		ifm.addModelEntity(fs.createResolution(time))
		return this
	}

	IdeaFlowModelBuilder addStateChange(StateChangeType type) {
		ifm.addModelEntity(fs.createStateChange(type))
		return this
	}

	IdeaFlowModelBuilder addStateChange(StateChangeType type, long time) {
		ifm.addModelEntity(fs.createStateChange(type, time))
		return this
	}

	IdeaFlowModel build() {
		return ifm
	}
}

