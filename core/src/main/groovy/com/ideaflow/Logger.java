package com.ideaflow;

public interface Logger {

	void debug(String message);

	void info(String message);

	void error(String message);

	void error(String message, Throwable exception);

}
