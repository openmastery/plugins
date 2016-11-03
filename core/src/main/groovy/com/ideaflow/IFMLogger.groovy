package com.ideaflow;

import java.io.File;
import java.io.PrintWriter;

public class IFMLogger {

	File activityLog

	IFMLogger() {
		init()
	}

	void init() {
		File ideaflowDir = new File(System.getProperty("user.home") + File.separator + ".ideaflow");
		ideaflowDir.mkdirs()
		activityLog = new File(ideaflowDir, "ifm_activity.log")
	}

	void logEvent(String message) {
		activityLog.append("\n$message")
	}


}
