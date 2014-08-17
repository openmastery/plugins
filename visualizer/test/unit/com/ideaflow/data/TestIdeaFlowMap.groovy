package com.ideaflow.data

import com.newiron.ideaflow.data.IdeaFlowMap
import org.junit.Before
import org.junit.Test


class TestIdeaFlowMap {

	IdeaFlowMap map

	@Test
	void parseProperties_ShouldUseParentDirectoriesAsProps() {
		Map props = IdeaFlowMap.parsePropertiesFrom("/Users/jklein/ifmdata/ifmtools/jklein/if_tools.ifm")

		assert props.project == "ifmtools"
		assert props.user == "jklein"
		assert props.taskId == "if_tools"
	}
}
