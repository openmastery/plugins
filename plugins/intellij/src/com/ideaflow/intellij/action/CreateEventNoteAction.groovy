package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowComponent
import com.ideaflow.model.Event
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.Messages
import com.intellij.ui.UIBundle
import com.ideaflow.model.EventType

@Mixin(ActionSupport)
class CreateEventNoteAction extends AnAction {

    @Override
    void actionPerformed(AnActionEvent e) {
        IFMController controller = IdeaFlowComponent.getIFMController(e.project)

        String note = controller.promptForInput("Create Event Note", "Enter an IdeaFlow event note:")
        controller.addNote(note)
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e);
        disableWhenNoIdeaFlow(e)
    }
}
