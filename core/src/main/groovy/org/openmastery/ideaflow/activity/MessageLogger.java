package org.openmastery.ideaflow.activity;


public interface MessageLogger {

	void writeMessage(Long taskId, Object message);
}
