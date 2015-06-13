package com.ideaflow.intellij.action.meta;

import com.ideaflow.controller.IFMController;
import com.ideaflow.intellij.IdeaFlowApplicationComponent;
import com.ideaflow.intellij.file.IdeaFlowMapFileType;
import com.intellij.CommonBundle;
import com.intellij.ide.actions.CreateElementActionBase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import java.io.File;

/**
 * @deprecated
 */
class NewIfmFileAction extends CreateElementActionBase {

	private static final String TITLE = "Create IFM File";
	private static final String DESCRIPTION = "Create a new IFM file";

	public NewIfmFileAction() {
		super(DESCRIPTION, DESCRIPTION, IdeaFlowMapFileType.IFM_ICON);
	}

	@Override
	protected String getCommandName() {
		return "IFM File";
	}

	@Override
	protected String getActionName(PsiDirectory directory, String newName) {
		return "Create IFM file";
	}

	@Override
	protected String getErrorTitle() {
		return CommonBundle.getErrorTitle();
	}

	@Override
	protected PsiElement[] invokeDialog(Project project, PsiDirectory directory) {
		MyInputValidator validator = new MyInputValidator(project, directory);
		Messages.showInputDialog(project, DESCRIPTION, TITLE, Messages.getQuestionIcon(), "", validator);

		PsiElement[] createdElements = validator.getCreatedElements();
		if (createdElements.length > 0) {
			String ifmFilePath = createdElements[0].getContainingFile().getVirtualFile().getPath();
			File ifmFile = new File(ifmFilePath);
			openIdeaFlowMap(project, ifmFile);
		}
		return createdElements;
	}

	@Override
	protected PsiElement[] create(String newName, PsiDirectory directory) {
		PsiFile file = createFileFromTemplate(directory, newName);
		PsiElement child = file.getLastChild();
		return child != null ? new PsiElement[]{file, child} : new PsiElement[]{file};
	}

	PsiFile createFileFromTemplate(PsiDirectory directory, String fileName) {
		String usedExtension = FileUtil.getExtension(fileName);

		if (IdeaFlowMapFileType.IFM_EXTENSION.equalsIgnoreCase(usedExtension) == false) {
			fileName += "." + IdeaFlowMapFileType.IFM_EXTENSION;
		}
		return createFromTemplate(directory, fileName);
	}

	private PsiFile createFromTemplate(PsiDirectory directory, String fileName) {
		PsiFileFactory factory = PsiFileFactory.getInstance(directory.getProject());
		PsiFile file = factory.createFileFromText(fileName, IdeaFlowMapFileType.IMF_FILE_TYPE, "");
		return (PsiFile) directory.add(file);
	}

	private void openIdeaFlowMap(final Project project, final File file) {
		final IFMController<Project> controller = IdeaFlowApplicationComponent.getIFMController();

		ApplicationManager.getApplication().invokeLater(new Runnable() {
			public void run() {
				controller.newIdeaFlow(project, file);
			}
		});
	}

}