package visualizer

import com.ideaflow.timeline.Timeline
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter


class IfmController {

	static defaultAction = "view"

	IfmService ifmService

	def view() {
		if (ifmService.activeModel) {
			ifmService.refresh()
			renderView()
		} else {
			render "Open an IFM file in your IDE to view"
		}

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
