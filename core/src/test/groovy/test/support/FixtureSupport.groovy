package test.support

import com.ideaflow.model.BandEnd
import com.ideaflow.model.BandStart
import com.ideaflow.model.BandType
import com.ideaflow.model.Conflict
import com.ideaflow.model.EditorActivity
import com.ideaflow.model.Idle
import com.ideaflow.model.ModelEntity
import com.ideaflow.model.Note
import com.ideaflow.model.Resolution
import com.ideaflow.model.StateChange
import com.ideaflow.model.StateChangeType
import com.ideaflow.timeline.ActivityDetail
import com.ideaflow.timeline.ConflictBand
import com.ideaflow.timeline.GenericBand
import com.ideaflow.timeline.TimePosition
import org.joda.time.DateTime
import org.reflections.Reflections

class FixtureSupport {

	static final String FILE = "file"
	static final String OTHER_FILE = "otherFile"
	static final String FILE1 = "file1"
	static final String FILE2 = "file2"
	static final String FILE3 = "file3"
	static final String FILE4 = "file4"


	static final long SHORT_DELAY = 5;
	static final long LONG_DELAY = 999999;

	static final long NOW
	static final long TIME1
	static final long TIME2
	static final long TIME3
	static final long TIME4

	static final TimePosition NOW_POSITION
	static final TimePosition TIME1_POSITION
	static final TimePosition TIME2_POSITION
	static final TimePosition TIME3_POSITION
	static final TimePosition TIME4_POSITION

	static {
		def cal = Calendar.getInstance()
		cal.set(Calendar.MILLISECOND, 0)
		NOW = cal.getTime().time

		TIME1 = NOW + LONG_DELAY
		TIME2 = TIME1 + LONG_DELAY
		TIME3 = TIME2 + LONG_DELAY
		TIME4 = TIME3 + LONG_DELAY

		NOW_POSITION = new TimePosition(new DateTime(NOW), 0)
		TIME1_POSITION = new TimePosition(new DateTime(TIME1), (int)TIME1 - NOW)
		TIME2_POSITION = new TimePosition(new DateTime(TIME2), (int)TIME2 - NOW)
		TIME3_POSITION = new TimePosition(new DateTime(TIME3), (int)TIME3 - NOW)
		TIME4_POSITION = new TimePosition(new DateTime(TIME4), (int)TIME4 - NOW)

	}


	Note createNote() {
		createNote("note")
	}

	Note createNote(String comment) {
		createNote(comment, NOW)
	}

	Note createNote(String comment, long time) {
		Note note = new Note(comment)
		setCreated(note, time)
		note.comment = comment
		note
	}

	StateChange createStateChange() {
		createStateChange(StateChangeType.startIdeaFlowRecording)
	}

	StateChange createStateChange(StateChangeType type) {
		createStateChange(type, NOW)
	}

	StateChange createStateChange(StateChangeType type, long time) {
		StateChange event = new StateChange(type)
		setCreated(event, time)
		event
	}

	Conflict createConflict() {
		createConflict(NOW)
	}

	Conflict createConflict(long time) {
		createConflict(time, 'question')
	}

	Conflict createConflict(long time, String question) {
		Conflict conflict = new Conflict(question)
		setCreated(conflict, time)
		conflict
	}

	Resolution createResolution() {
		createResolution(NOW)
	}

	Resolution createResolution(long time) {
		createResolution(time, 'answer')
	}

	Resolution createResolution(long time, String answer) {
		Resolution resolution = new Resolution(answer)
		setCreated(resolution, time)
		resolution
	}

	private void setCreated(def item, long time) {
		item.created = new DateTime(time)
	}

	EditorActivity createEditorActivity(String name) {
		createEditorActivity(name, NOW)
	}

	EditorActivity createEditorActivity(String name, long time) {
		createEditorActivity(name, 5, time)
	}

	EditorActivity createEditorActivity(String name, int duration, long time) {
		new EditorActivity(new DateTime(time), name, false, duration)
	}

	BandStart createBandStart() {
		createBandStart(BandType.learning, NOW)
	}

	BandStart createBandStart(BandType type, long time) {
		BandStart start = new BandStart(type, 'comment', false, false)
		start.created = new DateTime(time)
		start
	}

	BandEnd createBandEnd() {
		createBandEnd(BandType.learning, NOW)
	}

	BandEnd createBandEnd(BandType type, long time) {
		BandEnd end = new BandEnd(type)
		end.created = new DateTime(time)
		end
	}

	Idle createIdle() {
		createIdle(NOW, 'comment', 5)
	}

	Idle createIdle(long time, String comment, int duration) {
		new Idle(new DateTime(time), comment, duration)
	}

	ActivityDetail createActivityDetail(TimePosition time) {
		new ActivityDetail(time, createEditorActivity('file1.txt'))
	}

	GenericBand createGenericBand() {
		createGenericBand(NOW_POSITION)
	}

	GenericBand createGenericBand(TimePosition timePosition) {
		createGenericBand(timePosition, timePosition)
	}

	GenericBand createGenericBand(TimePosition startPosition, TimePosition endPosition) {
		GenericBand band = new GenericBand()
		band.bandStart = new BandStart(BandType.learning, "want to learn!", false, false)
		band.bandEnd = new BandEnd(BandType.learning)
		band.setStartPosition(startPosition)
		band.setEndPosition(endPosition)
		return band
	}

	ConflictBand createConflictBand() {
		createConflictBand(NOW_POSITION)
	}

	ConflictBand createConflictBand(TimePosition timePosition) {
		createConflictBand(timePosition, timePosition)
	}

	ConflictBand createConflictBand(TimePosition startPosition, TimePosition endPosition) {
		ConflictBand band = new ConflictBand()
		band.conflict = new Conflict("Conflict Question")
		band.resolution = new Resolution("Resolution Answer")
		band.setStartPosition(startPosition)
		band.setEndPosition(endPosition)
		return band
	}


	List<ModelEntity> getModelEntitySubClassInstances() {
		Reflections reflections = new Reflections(ModelEntity.package.name)
		reflections.getSubTypesOf(ModelEntity).collect { Class subType ->
			subType.newInstance() as ModelEntity
		}
	}

}
