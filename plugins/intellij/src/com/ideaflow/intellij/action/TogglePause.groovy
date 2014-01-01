package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowComponent
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction

@Mixin(ActionSupport)
class TogglePause extends ToggleAction {

    private static final String PAUSE_TITLE = "Pause IdeaFlow"
    private static final String RESUME_TITLE = "Resume IdeaFlow"


    @Override
    boolean isSelected(AnActionEvent e) {
        return isPaused(e)
    }

    @Override
    void setSelected(AnActionEvent e, boolean state) {
        IFMController controller = IdeaFlowComponent.getIFMController(e.project)

        if (controller.isPaused()) {
            controller.resume()
        } else {
            controller.pause()
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation()
        presentation.setEnabled(isIdeaFlowOpen(e));

        if (isPaused()) {
            presentation.setText(RESUME_TITLE)
        } else {
            presentation.setText(PAUSE_TITLE)
        }

    }

}
