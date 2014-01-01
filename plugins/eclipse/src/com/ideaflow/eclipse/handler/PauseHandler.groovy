package com.ideaflow.eclipse.handler

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler
import com.ideaflow.controller.IFMController;
import com.ideaflow.eclipse.IdeaFlowActivator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

class PauseHandler extends ToggleHandler {

	void executeHandler(ExecutionEvent event) throws ExecutionException {
		IFMController controller = IdeaFlowActivator.getController()
		if (controller.isPaused()) {
			controller.resume()
		} else {
			controller.pause()
		}		
	}
	
	boolean isChecked() {
		IFMController controller = IdeaFlowActivator.getController()
		controller.isPaused()
	}
	


}
