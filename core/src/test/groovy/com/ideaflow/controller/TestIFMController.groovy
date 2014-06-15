package com.ideaflow.controller

import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIFMController extends GroovyTestCase {

	IFMController controller
	IDEService stubIdeService

	void setUp() {
		stubIdeService = createIdeServiceStub()
		controller = new IFMController(stubIdeService)
		controller.newIdeaFlow(File.createTempFile("tmp", ".ifm"))
	}

	void testAddEventWithoutComment_ShouldBeIgnored() {
		controller.startConflict(null)
		assert false == controller.isOpenConflict()
	}

	void testAddEventWithComment_ShouldChangeState() {
		controller.startConflict('conflict')
		assert true == controller.isOpenConflict()
	}

	void testIsIdeaFlowOpen() {
		assert true == controller.isIdeaFlowOpen()

		controller.closeIdeaFlow()
		assert false == controller.isIdeaFlowOpen()
	}

	IDEService createIdeServiceStub() {
		[
				getActiveFileSelection: { 'testfile' },
				fileExists: { false },
				createNewFile: { file, contents -> },
				writeFile: { file, contents -> }
		] as IDEService
	}

}
