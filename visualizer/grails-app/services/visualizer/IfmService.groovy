package visualizer

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.timeline.Timeline
import com.ideaflow.timeline.TimelineFactory

class IfmService {

	Timeline activeTimeline
	String filePath

	void loadIdeaFlowMap(String filePath) {
		this.filePath = filePath
		File file = new File(filePath)
		IdeaFlowModel model = new DSLTimelineSerializer().deserialize(file)
		activeTimeline = new TimelineFactory().create(model)
	}

	void refresh() {
		loadIdeaFlowMap(filePath)
	}

}
