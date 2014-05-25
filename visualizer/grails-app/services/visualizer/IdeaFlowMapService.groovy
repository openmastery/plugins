package visualizer

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.timeline.Timeline
import com.ideaflow.timeline.TimelineFactory

class IdeaFlowMapService {

	Timeline activeTimeline

	IdeaFlowMapService() {
		File file = new File("/Users/katrea/code/ideaflow/ifm/convert_to_ifm_model.ifm")
		IdeaFlowModel model = new DSLTimelineSerializer().deserialize(file)
		activeTimeline = new TimelineFactory().create(model)
	}

}
