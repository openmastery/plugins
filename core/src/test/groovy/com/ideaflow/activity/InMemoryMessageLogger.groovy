package com.ideaflow.activity


class InMemoryMessageLogger implements MessageLogger {

	List<Object> messages = []

	@Override
	void writeMessage(Object message) {
		messages.add(message)
	}

}
