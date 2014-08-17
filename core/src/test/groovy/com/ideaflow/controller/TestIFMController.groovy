package com.ideaflow.controller

import com.ideaflow.model.Idle
import org.joda.time.DateTime
import org.joda.time.DateTimeUtils
import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIFMController extends Specification {

    IDEService ideService = Mock(IDEService)
	IFMController controller = new IFMController(ideService)

	def setup() {
		controller.newIdeaFlow("string", File.createTempFile("tmp", ".ifm"))
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
		controller.activeIdeaFlowModel.entityList = []
		DateTimeUtils.setCurrentMillisFixed(NOW)
		controller.startFileEvent(null, "some-event")

		when:
		DateTimeUtils.setCurrentMillisFixed(NOW + 1000)
		controller.markActiveFileEventAsIdle("idle comment")

		then:
		List entities = controller.getActiveIdeaFlowModel().getEntityList()
		assert entities[0] == new Idle(new DateTime(NOW), "idle comment", 1)
		assert entities.size() == 1
	}

	def "markActiveFileEventAsIdle should do nothing if there is no active file event"() {
		given:
		controller.endFileEvent(null)
		controller.activeIdeaFlowModel.entityList = []

		when:
		controller.markActiveFileEventAsIdle("idle comment")

		then:
		assert controller.getActiveIdeaFlowModel().getEntityList() == []
	}

}
