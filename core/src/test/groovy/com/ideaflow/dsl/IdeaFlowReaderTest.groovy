package com.ideaflow.dsl

import com.ideaflow.model.EditorActivity
import com.ideaflow.model.IdeaFlowModel
import com.ideaflow.model.StateChange
import com.ideaflow.model.StateChangeType
import spock.lang.Specification

class IdeaFlowReaderTest extends Specification {

    void testStringBooleans_ShouldParseAsBooleans() {
        given:
        IdeaFlowReader reader = new IdeaFlowReader(1)
        String content = """
initialize (dateFormat: "yyyy-MM-dd'T'HH:mm:ss", created: '2014-08-19T00:00:00', )
editorActivity(created: '2014-08-19T00:00:00', name: 'somefile.txt', modified: 'false')
"""
        when:
        IdeaFlowModel model = reader.readModel(null, content)

        then:

        model.entityList[0] instanceof EditorActivity
        model.entityList[0].modified == false

    }



    def "should read complete content if line count larger than chunks size"() {
		given:
		IdeaFlowReader reader = new IdeaFlowReader(1)
		String content = """
initialize (dateFormat: "yyyy-MM-dd'T'HH:mm:ss", created: '2014-08-19T00:00:00', )
stateChange (created: '2014-08-19T00:00:00', type: 'startIdeaFlowRecording', )
"""

		when:
		IdeaFlowModel model = reader.readModel(null, content)

		then:
		model.entityList[0] instanceof StateChange
		model.entityList[0].type == StateChangeType.startIdeaFlowRecording
		model.entityList.size() == 1
	}
}
