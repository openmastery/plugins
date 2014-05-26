package visualizer

import com.ideaflow.timeline.Timeline


class IfmController {

	static defaultAction = "view"

	IfmService ifmService

	def view() {
		ifmService.refresh()
	}

	def open(String filePath) {
		ifmService.loadIdeaFlowMap(filePath)

		render(view: "view")
	}
}
