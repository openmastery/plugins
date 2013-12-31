package com.ideaflow.report

import groovy.xml.MarkupBuilder
import java.text.SimpleDateFormat
import com.ideaflow.model.Interval
import com.ideaflow.model.Event
import com.ideaflow.model.EventType
import com.ideaflow.model.IdeaFlowModel


class XMLTimelineSerializer {

    String serialize(IdeaFlowModel model) {
        def writer = new StringWriter()
        MarkupBuilder xml = new MarkupBuilder(writer)
        xml.ideaflow(created: format(model.created)) {

            model.intervalList.each {
                    interval(created: format(it.created), name:it.name, duration: it.duration)
            }
            model.eventList.each {
                    event(created: format(it.created), type:it.type, comment: it.comment)
            }
        }
        writer.toString()
    }

    IdeaFlowModel deserialize(String xml) {

        def ideaflow = new XmlSlurper().parseText(xml)

        Date createDate = toDate(ideaflow.@created.text())

        IdeaFlowModel model = new IdeaFlowModel('', createDate)

        inflateEvents(model, ideaflow.event)
        inflateIntervals(model, ideaflow.interval)

        return model

    }

    void inflateEvents(IdeaFlowModel model, events) {
        events.each { event ->
            Date created = toDate(event.@created.text())
            EventType type = toEventType(event.@type.text())
            String comment = event.@comment.text()

            model.addEvent(new Event(type, comment, created))
        }
    }

    void inflateIntervals(IdeaFlowModel model, intervals) {
        intervals.each { interval ->
            String name = interval.@name.text()
            int duration = toInt(interval.@duration.text())
            Date created = toDate(interval.@created.text())

            model.addInterval(new Interval(created, name, duration))
        }
    }


    String format(Date date) {
        SimpleDateFormat format = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        format.format(date)
    }

    Date toDate(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss')
        format.parse(dateStr)
    }

    Integer toInt(String intStr) {
        intStr as Integer
    }

    EventType toEventType(String typeName) {
        EventType.valueOf(EventType.class, typeName)
    }
}
