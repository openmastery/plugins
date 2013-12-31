package com.ideaflow.report

import test.support.FixtureSupport
import com.ideaflow.model.IdeaFlowModel

@Mixin(FixtureSupport)
class TestXMLTimelineSerializer extends GroovyTestCase {

    IdeaFlowModel model
    XMLTimelineSerializer xmlSerializer

    void setUp() {
        model = new IdeaFlowModel('', new Date(NOW))
        xmlSerializer = new XMLTimelineSerializer()
    }

    void testSerialize_ShouldSerializeIntervalsInXML() {
        model.addInterval(createInterval(FILE, NOW))
        model.addInterval(createInterval(OTHER_FILE, NOW))

        String xml = xmlSerializer.serialize(model)

        def records = parseXml(xml)
        assert 2 == records.interval.size()
    }

    void testSerialize_ShouldSerializeEventsInXML() {
        model.addEvent(createEvent('a nice note', NOW))
        model.addEvent(createEvent('super note', NOW))
        model.addEvent(createEvent('happy note', NOW))


        String xml = xmlSerializer.serialize(model)

        def records = parseXml(xml)
        assert 3 == records.event.size()
    }

    void testDeserialize_ShouldParseModelProperties() {
        String xml = xmlSerializer.serialize(model)

        IdeaFlowModel newModel = xmlSerializer.deserialize(xml)

        assert model.created == newModel.created
    }

    void testDeserialize_ShouldParseEvent() {
        model.addEvent(createEvent('a nice note', NOW))
        String xml = xmlSerializer.serialize(model)

        IdeaFlowModel newModel = xmlSerializer.deserialize(xml)

        assert 1 == newModel.eventList.size()

        def expectedEvent = model.eventList.get(0)
        def actualEvent = newModel.eventList.get(0)

        assert expectedEvent.type == actualEvent.type
        assert expectedEvent.created == actualEvent.created
        assert expectedEvent.comment == actualEvent.comment

    }


    def parseXml(String xml) {
        new XmlSlurper().parseText(xml)
    }
}
