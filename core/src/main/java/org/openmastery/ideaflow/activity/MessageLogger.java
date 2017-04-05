package org.openmastery.ideaflow.activity;


public interface MessageLogger {

	void flush();

	void writeMessage(Long taskId, Object message);

}
