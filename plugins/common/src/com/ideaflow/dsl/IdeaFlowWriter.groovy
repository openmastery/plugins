package com.ideaflow.dsl

import com.ideaflow.model.Conflict
import com.ideaflow.model.GenericEvent
import com.ideaflow.model.Interval
import com.ideaflow.model.Resolution

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

	void write(GenericEvent event) {
		writeItem("event", event, [
			"type: '${event.type}'",
			"comment: '''${event.comment}'''"
		])
	}

	void write(Conflict conflict) {
		writeItem("conflict", conflict, [
			"question: '''${conflict.question}'''"
		])
	}

	void write(Resolution resolution) {
		writeItem("resolution", resolution, [
			"answer: '''${resolution.answer}'''"
		])
	}

	void write(Interval interval) {
		writeItem("interval", interval, [
			"name: '${interval.name}'",
			"duration: ${interval.duration}"
		])
	}

	private void writeItem(String name, def item, List<String> additionalLines = []) {
		writer.print "${name} ("
		writer.print "created: '${dateFormat.format(item.created)}', "
		additionalLines.each { String line ->
			writer.print "${line}, "
		}
		writer.println ")"
		writer.flush()
	}

}
