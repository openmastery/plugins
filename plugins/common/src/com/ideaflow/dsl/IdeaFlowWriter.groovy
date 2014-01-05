package com.ideaflow.dsl

import com.ideaflow.model.Event
import com.ideaflow.model.Interval

import java.text.SimpleDateFormat

class IdeaFlowWriter {

	private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

	private BufferedWriter writer
	private SimpleDateFormat dateFormat

	IdeaFlowWriter(Writer writer) {
		this.writer = createBufferedWriter(writer)
		this.dateFormat = new SimpleDateFormat(DEFAULT_DATE_FORMAT)
	}

	private BufferedWriter createBufferedWriter(Writer writer) {
		writer instanceof BufferedWriter ? writer as BufferedWriter : new BufferedWriter(writer)
	}

	void close() {
		writer.close()
	}

	void writeInitialization(Date created) {
		writer.print "initialize ("
		writer.print "dateFormat: \"${DEFAULT_DATE_FORMAT}\", "
		writer.print "created: '${dateFormat.format(created)}', "
		writer.println ")"
		writer.flush()
	}

	void write(Event event) {
		writeTimelineEvent(event)
	}

	void write(Interval interval) {
		writeInterval(interval)
	}

	void writeTimelineEvent(Event event) {
		writer.print "event ("
		writer.print "created: '${dateFormat.format(event.created)}', "
		writer.print "type: '${event.type}', "
		writer.print "comment: '''${event.comment}''', "
		writer.println ")"
		writer.flush()
	}

	void writeInterval(Interval interval) {
		writer.print "interval ("
		writer.print "created: '${dateFormat.format(interval.created)}', "
		writer.print "name: '${interval.name}', "
		writer.print "duration: ${interval.duration}, "
		writer.println ")"
		writer.flush()
	}

}
