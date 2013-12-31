package com.ideaflow.eclipse.handler

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;

abstract class ToggleHandler extends AbstractHandler implements IElementUpdater {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		executeHandler(event);
		ICommandService service = PlatformUI.getWorkbench().getService(ICommandService.class)
		service.refreshElements("ideaflow.IdeaFlowCmd", null)
		service.refreshElements("ideaflow.PauseCmd", null)
		service.refreshElements("ideaflow.ConflictCmd", null)
	}

	@Override
	public void updateElement(UIElement element, Map parameters) {
		element.setChecked (isChecked())
	}

	abstract boolean isChecked()
	
	abstract void executeHandler(ExecutionEvent event) throws ExecutionException 
}
