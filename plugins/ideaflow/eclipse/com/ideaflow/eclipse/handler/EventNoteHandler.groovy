package com.ideaflow.eclipse.handler

import org.eclipse.core.commands.AbstractHandler
import com.ideaflow.controller.IFMController;
import com.ideaflow.eclipse.IdeaFlowActivator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.ideaflow.model.EventType;

class EventNoteHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFMController controller = IdeaFlowActivator.getController()
		String note = controller.promptForInput("Create Event Note", "Enter an IdeaFlow event note:")
        if (note) {
			controller.addEvent(EventType.note, note)
        }
	}

}
