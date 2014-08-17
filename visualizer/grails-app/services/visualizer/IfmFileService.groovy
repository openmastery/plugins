package visualizer

import com.newiron.ideaflow.data.IdeaFlowMap
import groovy.io.FileType

class IfmFileService {

	IfmFileService() {
		initBaseDir()
	}

	IdeaFlowMap findOrCreateIdeaFlowMap(String project, String user, String taskId) {
		if (project && user && taskId) {
			File ifmFile = findOrCreateIfmFile(project, user, taskId)
			return new IdeaFlowMap(project, user, taskId, ifmFile)
		} else {
			return findOrCreateIdeaFlowMap(taskId)
		}
	}

	IdeaFlowMap findOrCreateIdeaFlowMap(String taskId) {
		File matchingFile = null
		baseDir.eachFileRecurse(FileType.FILES, { file ->
			if (file.name.contains(taskId)) {
				matchingFile = file
			}
		})

		matchingFile ? new IdeaFlowMap(matchingFile) : null
	}

	File findOrCreateIfmFile(String project, String user, String taskId) {
		createFolder(project, user)
		File file = new File(baseDir.absolutePath + "/$project/$user/$taskId" + ".ifm")
		if (!file.exists()) {
			file.createNewFile()
		}
		return file
	}

	void createFolder(String project, String user) {
		File folder = new File(baseDir.absolutePath + "/$project/$user")
		folder.mkdirs()
	}

	File getBaseDir() {
		String userHome = System.getProperty("user.home")
		new File("$userHome/ifmdata")
	}

	void initBaseDir() {
		String userHome = System.getProperty("user.home")
		File baseIdeaFlowDir = new File("$userHome/ifmdata")
		baseIdeaFlowDir.mkdirs()
	}

}
