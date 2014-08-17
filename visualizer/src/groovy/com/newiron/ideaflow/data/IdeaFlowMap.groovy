package com.newiron.ideaflow.data

import com.ideaflow.dsl.DSLTimelineSerializer
import com.ideaflow.model.BandType
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.timeline.Timeline
import com.ideaflow.timeline.TimelineFactory
import com.newiron.ideaflow.presentation.TimelineChart
import com.newiron.ideaflow.presentation.TimelineDecorator


class IdeaFlowMap {
	String project
	String user
	String taskId
	private File file
	private IdeaFlowModel model
	private Timeline timelineModel

	IdeaFlowMap(File file) {
		Map props = parsePropertiesFrom(file.absolutePath)

		this.project = props.project
		this.user = props.user
		this.taskId = props.taskId
		this.file = file
		init()
	}

	IdeaFlowMap(String project, String user, String taskId, File file) {
		this.project = project
		this.user = user
		this.taskId = taskId
		this.file = file
		init()
	}

	static Map parsePropertiesFrom(String fileName) {
		println "filename: $fileName"
		Map ifmProps = [:]

		def matcher = fileName =~ /\/.*\/(.*)\/(.*)\/(.*).ifm/
		println matcher
		ifmProps.project = matcher[0][1]
		ifmProps.user = matcher[0][2]
		ifmProps.taskId = matcher[0][3]

		return ifmProps
	}

	void init() {
		model = new DSLTimelineSerializer().deserialize(file)
		timelineModel = new TimelineFactory().create(model)

		new TimelineDecorator().decorate(timelineModel)
	}

	void appendActivity(String payload) {
		file.append(payload)
	}

	SummaryStatus getStatus() {
		SummaryStatus status = new SummaryStatus()

		status.isActiveConflict = model.activeConflict != null
		status.isActiveLearning = model.activeBandStart?.type == BandType.learning
		status.isActiveRework = model.activeBandStart?.type == BandType.rework

		status.project = project
		status.user = user
		status.taskId = taskId
		status.description = file.name

		return status
	}

	TimelineChart getTimelineChart() {
		new TimelineChart(timelineModel)
	}
}
