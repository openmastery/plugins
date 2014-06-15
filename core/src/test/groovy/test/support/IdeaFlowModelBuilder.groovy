package test.support

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange
import org.joda.time.DateTime

class IdeaFlowModelBuilder {

	IdeaFlowModel ifm = new IdeaFlowModel()
	FixtureSupport fs = new FixtureSupport()

	static IdeaFlowModelBuilder create() {
		return new IdeaFlowModelBuilder()
	}

	IdeaFlowModelBuilder defaults() {
		ifm.created = new DateTime(fs.NOW)
		ifm.file = new File(fs.FILE)
		return this
	}

	IdeaFlowModelBuilder addBandEnd(BandEnd bandEnd) {
		ifm.addModelEntity(bandEnd)
		return this
	}

	IdeaFlowModelBuilder addBandStart(BandStart bandStart) {
		ifm.addModelEntity(bandStart)
		return this
	}

	IdeaFlowModelBuilder addConflict(Conflict conflict) {
		ifm.addModelEntity(conflict)
		return this
	}

	IdeaFlowModelBuilder addResolution(Resolution resolution) {
		ifm.addModelEntity(resolution)
		return this
	}

	IdeaFlowModelBuilder addEditorActivity(int duration) {
		EditorActivity activity = fs.createEditorActivity('name', duration, fs.NOW)
		return addEditorActivity(activity)
	}

	IdeaFlowModelBuilder addEditorActivity(EditorActivity activity) {
		ifm.addModelEntity(activity)
		return this
	}

	IdeaFlowModelBuilder addNote(Note note) {
		ifm.addModelEntity(note)
		return this
	}

	IdeaFlowModelBuilder addStateChange(StateChange stateChange) {
		ifm.addModelEntity(stateChange)
		return this
	}

	IdeaFlowModel build() {
		return ifm
	}
}

