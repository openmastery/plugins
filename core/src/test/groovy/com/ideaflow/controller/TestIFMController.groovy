package com.ideaflow.controller

import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIFMController extends Specification {

    IDEService ideService = Mock(IDEService)
	IFMController controller = new IFMController(ideService)

	def setup() {
		controller.newIdeaFlow("string", File.createTempFile("tmp", ".ifm"))
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

}
