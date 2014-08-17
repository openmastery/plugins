package com.ideaflow.model

import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIdeaFlowModel extends Specification {

	IdeaFlowModel model = new IdeaFlowModel(new File('test'), null)

	void testAddEvent_ShouldNotAdd_IfPaused() {
        given:
		model.isPaused = true

        when:
		model.addModelEntity(createConflict(NOW))

        then:
		assert model.size() == 0
		assert model.isOpenConflict() == false
	}

	void testAddInterval_ShouldNotAdd_IfPaused() {
        given:
		model.isPaused = true

        when:
		model.addModelEntity(createEditorActivity('test', NOW))

        then:
		assert model.size() == 0
	}

	void testAddEvent_ShouldToggleOpenConflict() {
        given:
		assert model.isOpenConflict() == false

        when:
		model.addModelEntity(createConflict(TIME1))

        then:
		assert model.isOpenConflict() == true

        when:
		model.addModelEntity(createResolution(TIME2))
        then:
		assert model.isOpenConflict() == false
	}


}
