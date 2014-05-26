package com.ideaflow.intellij.action

import com.ideaflow.controller.IFMController
import com.ideaflow.intellij.IdeaFlowComponent
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ToggleAction
import com.intellij.openapi.module.Module
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import javax.swing.*

@Mixin(ActionSupport)
class ToggleOpenIdeaFlow extends ToggleAction {

    private static final String OPEN_TITLE = "Open IdeaFlow"
    private static final String CLOSE_TITLE = "Close IdeaFlow"


    @Override
    boolean isSelected(AnActionEvent e) {
        return isIdeaFlowOpen(e)
    }

    @Override
    void setSelected(AnActionEvent e, boolean state) {
        IFMController controller = IdeaFlowComponent.getIFMController(e.project)

        if (controller.isIdeaFlowOpen()) {
            controller.closeIdeaFlow()
        } else {
            createNewIdeaFlow(e)
        }
    }

    @Override
    public void update(AnActionEvent e) {
        super.update(e)
        Presentation presentation = e.getPresentation()

        if (isIdeaFlowOpen(e)) {
            presentation.setText(CLOSE_TITLE)
        } else {
            presentation.setText(OPEN_TITLE)
        }
    }

    private void createNewIdeaFlow(AnActionEvent e) {
        Module module = findIdeaFlowModule(e.project)
        if (module == null) {
            Messages.showWarningDialog("Please first create an 'ideaflow' module", "No 'ideaflow' module found.")
            return
        }

	    String ideaFlowMapFileName = getSelectedOrCreateNewIdeaFlowMapFileName(module, e)

        if (ideaFlowMapFileName != null) {
            IFMController controller = IdeaFlowComponent.getIFMController(e.project)
            controller.newIdeaFlow(ideaFlowMapFileName)
        }
    }

    private Module findIdeaFlowModule(Project project) {
        return ModuleManager.getInstance(project).findModuleByName('ideaflow')
    }

	private String getSelectedOrCreateNewIdeaFlowMapFileName(Module module, AnActionEvent e) {
		VirtualFile ideaFlowMapFile = getSelectedIdeaFlowMapFile(e)

		String ideaFlowMapRelativeFileName
		if (ideaFlowMapFile) {
			ideaFlowMapRelativeFileName = ideaFlowMapFile.path - module.moduleFile.parent.path
		} else {
			ideaFlowMapRelativeFileName = createNewFile(e.project)
		}
		ideaFlowMapRelativeFileName
	}

	private String createNewFile(Project project) {

        String newFileName

        MessageSpec messageToShow = MessageSpec.question("Create IdeaFlow map", "Name of IdeaFlow mapping file:")

        while (messageToShow) {
            newFileName = promptWith(messageToShow)
            messageToShow = checkForErrors(newFileName, project)
        }
       return newFileName
    }

    private MessageSpec checkForErrors(String fileName, Project project) {
        MessageSpec errorToShow = null
        if (isBadFileName(fileName)) {
            errorToShow = MessageSpec.error("Invalid file name, please enter a valid name:")
        } else if (fileName != null) {
            Exception failReason = tryToValidateFile(fileName, project)
            if (failReason) {
                errorToShow = MessageSpec.error(failReason.message)
            }
        }
        return errorToShow
    }

    private String promptWith(MessageSpec spec) {
        return Messages.showInputDialog(spec.message, spec.title, spec.icon)
    }

    private static class MessageSpec {
        String title
        String message
        Icon icon

        static MessageSpec error(String message) {
            return new MessageSpec(title: 'Error', message: message, icon: Messages.getErrorIcon())
        }

        static MessageSpec question(String title, String message) {
            return new MessageSpec(title: title, message: message, icon: Messages.getQuestionIcon())
        }
    }

    private Exception tryToValidateFile(String newFileName, Project project) {
        IFMController controller = IdeaFlowComponent.getIFMController(project)
        Exception failReason = null
        try {
            controller.validateFilePath(newFileName)
        } catch (Exception ex) {
            ex.printStackTrace()
            failReason = ex
        }
        return failReason
    }

    private boolean isBadFileName(String newFileName) {

    }
}