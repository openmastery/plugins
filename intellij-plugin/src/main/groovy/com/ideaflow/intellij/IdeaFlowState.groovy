package com.ideaflow.intellij

import com.ideaflow.controller.IFMController
import com.ideaflow.controller.IFMWorkingSetListener
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project

class IdeaFlowState {

	private static final String OPEN_FILE_PATHS = "IFM.OpenFilePaths"
	private static final String ACTIVE_FILE_PATH = "IFM.ActiveFilePath"

	private IFMController controller
	private PropertiesComponent properties
	private boolean restoringActiveState = false

	// TODO: clean this up

	IdeaFlowState(IFMController controller) {
		this.controller = controller
		properties = PropertiesComponent.getInstance()

		controller.setWorkingSetListener(new IFMWorkingSetListener() {
			@Override
			void onWorkingSetChanged() {
				if (restoringActiveState) {
					return
				}

				ApplicationManager.getApplication().invokeLater(new Runnable() {
					@Override
					void run() {
						saveActiveState()
					}
				})
			}
		})
	}

	public void saveActiveState() {
		saveOpenFiles()
		saveActiveFile()
	}

	public void restoreActiveState(Project project) {
		restoringActiveState = true
		try {
			for (File file : getSavedOpenFiles()) {
				controller.newIdeaFlow(project, file)
			}

			File savedActiveFile = getSavedActiveFile()
			if (savedActiveFile) {
				controller.newIdeaFlow(project, savedActiveFile)
			}
		} finally {
			restoringActiveState = false
		}
	}

	private void saveOpenFiles() {
		List<File> activeFiles = controller.getOpenIdeaFlowFiles()
		properties.setValues(OPEN_FILE_PATHS, toAbsolutePaths(activeFiles))
	}

	private void saveActiveFile() {
		properties.setValue(ACTIVE_FILE_PATH, controller.activeIdeaFlowModel?.file?.absolutePath)
	}

	public List<File> getSavedOpenFiles() {
		String[] filePaths = properties.getValues(OPEN_FILE_PATHS)
		filePaths ? toFileList(filePaths) : []
	}

	public File getSavedActiveFile() {
		String activeFilePath = properties.getValue(ACTIVE_FILE_PATH)
		File activeFile = null
		if (activeFilePath) {
			activeFile = new File(activeFilePath)
		}
		activeFile?.exists() ? activeFile : null
	}

	private String[] toAbsolutePaths(List<File> files) {
		files.collect { File file ->
			file.absolutePath
		} as String[]
	}

	private List<File> toFileList(String[] filePaths) {
		filePaths.collect { String filePath ->
			new File(filePath)
		}.findAll { File file ->
			file.exists()
		}
	}

}
