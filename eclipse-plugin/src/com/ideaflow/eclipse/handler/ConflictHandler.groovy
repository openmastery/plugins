package com.ideaflow.eclipse.handler

import org.eclipse.core.commands.AbstractHandler
import com.ideaflow.controller.IFMController;
import com.ideaflow.eclipse.IdeaFlowActivator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

class ConflictHandler extends ToggleHandler {

	private static final String START_CONFLICT_TITLE = "Start Conflict"
	private static final String START_CONFLICT_MSG = "What conflict question is in your head?"

	private static final String END_CONFLICT_TITLE = "End Conflict"
	private static final String END_CONFLICT_MSG = "What answer resolved the conflict?"

	void executeHandler(ExecutionEvent event) throws ExecutionException {
		IFMController controller = IdeaFlowActivator.getController()
		
		if (controller.isOpenConflict()) {
			String note = controller.promptForInput(END_CONFLICT_TITLE, END_CONFLICT_MSG)
			if (note) controller.endConflict(note)
		} else {
			String note = controller.promptForInput(START_CONFLICT_TITLE, START_CONFLICT_MSG)
			if (note) controller.startConflict(note)
		}
	}

	boolean isChecked() {
		IFMController controller = IdeaFlowActivator.getController()
		controller.isOpenConflict()
	}
}
