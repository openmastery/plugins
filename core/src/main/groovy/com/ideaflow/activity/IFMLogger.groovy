package com.ideaflow.activity

import org.joda.time.LocalDateTime

public class IFMLogger {

	private File activeLog
	private File logDir

	private final Object lock = new Object()

	IFMLogger() {
		init()
	}

	void init() {
		logDir = new File(System.getProperty("user.home") + File.separator + ".ideaflow");
		logDir.mkdirs()

		activeLog = new File(logDir, "ifm_activity.log")
	}

	void logEvent(String message) {
		synchronized (lock) {
			activeLog.append("\n$message")
		}

	}

	void rollLogFile() {
		synchronized (lock) {
			activeLog.renameTo("ifm_activity" + createLogTimestampSuffix())
			activeLog = new File(logDir, "ifm_activity.log")
		}

	}

	String createLogTimestampSuffix() {
		LocalDateTime now = LocalDateTime.now()

		now.toString("-yyyy-MM-dd-HH-mm")
	}


}
