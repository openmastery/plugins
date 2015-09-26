package com.ideaflow.controller

import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.Task
import com.ideaflow.model.entry.Idle
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIFMController extends Specification {

    IDEService ideService = Mock(IDEService)
	IFMController controller = new IFMController(ideService)

	def setup() {
		DateTimeUtils.setCurrentMillisFixed(NOW)
		controller.newIdeaFlow("context", new Task(taskId: "test"))
	}

	def cleanup() {
		DateTimeUtils.setCurrentMillisSystem()
	}

    void testAddEventWithoutComment_ShouldBeIgnored() {
        when:
		controller.startConflict("", null)

        then:
		assert false == controller.isOpenConflict()
	}

    void testAddEventWithComment_ShouldChangeState() {
        when:
		controller.startConflict("", 'conflict')

        then:
		assert true == controller.isOpenConflict()
	}

    void testIsIdeaFlowOpen() {
        given:
        assert true == controller.isIdeaFlowOpen()

        when:
        controller.closeIdeaFlow(null)

        then:
        assert false == controller.isIdeaFlowOpen()
    }

	def "markActiveFileEventAsIdle should record the active event as an idle event"() {
		given:
		controller.activeIdeaFlowModel.entryList = []
		controller.startFileEvent(null, "some-event")

		when:
		DateTimeUtils.setCurrentMillisFixed(NOW + 1000)
		controller.markActiveFileEventAsIdle("idle comment")

		then:
		List entities = controller.getActiveIdeaFlowModel().getEntryList()
		assert entities[0] == new Idle(new DateTime(NOW), "idle comment", 1)
		assert entities.size() == 1
	}

	def "markActiveFileEventAsIdle should do nothing if there is no active file event"() {
		given:
		controller.endFileEvent(null)
		controller.activeIdeaFlowModel.entryList = []

		when:
		controller.markActiveFileEventAsIdle("idle comment")

		then:
		assert controller.getActiveIdeaFlowModel().getEntryList() == []
	}

	def "newIdeaFlow should suspend existing idea flow but retain old task in open task list"() {
		when:
		Task oldModelTask = controller.activeIdeaFlowModel.task
		Task newModelTask = new Task(taskId: "new_task")
		IdeaFlowModel oldActiveModel = controller.activeIdeaFlowModel
		controller.newIdeaFlow("context", newModelTask)

		then:
		controller.activeIdeaFlowModel != oldActiveModel
		controller.activeIdeaFlowModel.task == newModelTask
		controller.workingSetTasks == [oldModelTask, controller.activeIdeaFlowModel.task]
	}

	def "closeIdeaFlow should remove task from open task list"() {
		when:
		controller.closeIdeaFlow("context")

		then:
		controller.workingSetTasks == []
	}

	def "should not add multiple tasks to open task list if newIdeaFlow called with same task twice"() {
		given:
		Task oldModelTask = controller.activeIdeaFlowModel.task

		when:
		controller.newIdeaFlow("string", oldModelTask)

		then:
		controller.workingSetTasks == [oldModelTask]
	}

}
