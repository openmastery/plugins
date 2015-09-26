package test.support

import com.ideaflow.model.Task
import com.ideaflow.model.entry.BandEnd
import com.ideaflow.model.entry.BandStart
import com.ideaflow.model.entry.Conflict
import com.ideaflow.model.entry.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.entry.Idle
import com.ideaflow.model.entry.Note
import com.ideaflow.model.entry.Resolution
import com.ideaflow.model.entry.StateChange
import org.joda.time.DateTime

class IdeaFlowModelBuilder {

	IdeaFlowModel ifm = new IdeaFlowModel()
	FixtureSupport fs = new FixtureSupport()

	static IdeaFlowModelBuilder create() {
		return new IdeaFlowModelBuilder()
	}

	IdeaFlowModelBuilder defaults() {
		ifm.created = new DateTime(fs.NOW)
		ifm.task = new Task(taskId: 'test')
		return this
	}

	IdeaFlowModelBuilder addBandEnd(BandEnd bandEnd) {
		ifm.addModelEntry(bandEnd)
		return this
	}

	IdeaFlowModelBuilder addIdle(Idle idle) {
		ifm.addModelEntry(idle)
		return this
	}

	IdeaFlowModelBuilder addBandStart(BandStart bandStart) {
		ifm.addModelEntry(bandStart)
		return this
	}

	IdeaFlowModelBuilder addConflict(Conflict conflict) {
		ifm.addModelEntry(conflict)
		return this
	}

	IdeaFlowModelBuilder addResolution(Resolution resolution) {
		ifm.addModelEntry(resolution)
		return this
	}

	IdeaFlowModelBuilder addEditorActivity(int duration) {
		EditorActivity activity = fs.createEditorActivity('name', duration, fs.NOW)
		return addEditorActivity(activity)
	}

	IdeaFlowModelBuilder addEditorActivity(EditorActivity activity) {
		ifm.addModelEntry(activity)
		return this
	}

	IdeaFlowModelBuilder addNote(Note note) {
		ifm.addModelEntry(note)
		return this
	}

	IdeaFlowModelBuilder addStateChange(StateChange stateChange) {
		ifm.addModelEntry(stateChange)
		return this
	}

	IdeaFlowModel build() {
		return ifm
	}
}

