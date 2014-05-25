package visualizer

import com.ideaflow.timeline.Timeline


class IfmController {

	static defaultAction = "open"

	IfmService ifmService

	def open(String filePath) {
		ifmService.loadIdeaFlowMap(filePath)

		redirect(controller: "timeline", action: "view")
	}
}
