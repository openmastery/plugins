package com.ideaflow.model

import spock.lang.Specification
import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIdeaFlowModel extends Specification {

	IdeaFlowModel model = new IdeaFlowModel(new File('test'), null)

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
