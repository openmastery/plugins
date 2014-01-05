package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

@Mixin(ActionSupport)
class CreateEventNoteAction extends AnAction {

    @Override
    void actionPerformed(AnActionEvent e) {
        IFMController controller = IdeaFlowComponent.getIFMController(e.project)

        String note = controller.promptForInput("Create Note", "Enter an IdeaFlow event note:")
        controller.addNote(note)
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        disableWhenNoIdeaFlow(e)
    }
}
