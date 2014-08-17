package ifm

import com.newiron.ideaflow.data.IdeaFlowMap
import grails.converters.JSON
import grails.converters.XML
import visualizer.IfmFileService


class IdeaflowController {

	IfmFileService ifmFileService

	def show(String project, String user, String taskId) {
		println project
		println user
		println taskId
		IdeaFlowMap ifm = ifmFileService.findOrCreateIdeaFlowMap(project, user, taskId)
		renderIf ifm, { ifm.status as JSON }
	}

	def save(String project, String user, String taskId) {
		IdeaFlowMap ifm = ifmFileService.findOrCreateIdeaFlowMap(project, user, taskId)
		ifm.appendActivity(request.reader.text)
		render 'OK'
	}

	def timeline(String taskId) {
		IdeaFlowMap ifm = ifmFileService.findOrCreateIdeaFlowMap(taskId)

		response.setContentType("application/json")

		renderIf ifm, { ifm.getTimelineChart().toJSON() }

	}

	void renderIf(Object itemThatMustExist, Closure closure) {
		if (itemThatMustExist) {
			render closure()
		} else {
			render ([] as JSON)
		}
	}
}
