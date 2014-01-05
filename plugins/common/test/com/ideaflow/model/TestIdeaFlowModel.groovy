package com.ideaflow.model

import test.support.FixtureSupport

@Mixin(FixtureSupport)
class TestIdeaFlowModel extends GroovyTestCase {

    IdeaFlowModel model


    void setUp() {
        model = new IdeaFlowModel('test', null)
    }

    void testAddEvent_ShouldNotAdd_IfPaused() {

        model.isPaused = true
        model.addModelEntity(createConflict(NOW))

        assert model.size() == 0
        assert model.isOpenConflict() == false
    }

    void testAddInterval_ShouldNotAdd_IfPaused() {
        model.isPaused = true
        model.addModelEntity(createEditorActivity('test', NOW))

        assert model.size() == 0
    }

    void testAddEvent_ShouldToggleOpenConflict() {
        assert model.isOpenConflict() == false

        model.addModelEntity(createConflict(TIME1))
        assert model.isOpenConflict() == true

        model.addModelEntity(createResolution(TIME2))
        assert model.isOpenConflict() == false
    }


}
