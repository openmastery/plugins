package org.openmastery.ideaflow.intellij;

public class Logger implements com.ideaflow.Logger {

	com.intellij.openapi.diagnostic.Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance("org.ideaflow");

	@Override
	public void debug(String message) {
		logger.debug(message);
	}

	@Override
	public void info(String message) {
		logger.info(message);
	}

	@Override
	public void error(String message) {
		logger.error(message);
	}

	@Override
	public void error(String message, Throwable exception) {
		logger.error(message, exception);
	}

}
