package com.ideaflow.activity


class InMemoryMessageLogger implements MessageLogger {

	List<Object> messages = []

	@Override
	void writeMessage(Long taskId, Object message) {
		messages.add(message)
	}

}
