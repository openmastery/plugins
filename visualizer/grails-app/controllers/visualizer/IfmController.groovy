package visualizer

import com.ideaflow.timeline.Timeline
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


class IfmController {

	static defaultAction = "view"

	IfmService ifmService

	def view() {
		ifmService.refresh()
		renderView()
	}

	def open(String filePath) {
		ifmService.loadIdeaFlowMap(filePath)
		renderView()
	}

	private void renderView() {
		DateTimeFormatter format = DateTimeFormat.forPattern("MMMM dd hh:mmaa")
		String formattedDate = format.print(ifmService.activeModel.created)
		render(view: "view", model: [createdDate: formattedDate,
			ifmName: ifmService.activeModel.file.name])
	}
}
