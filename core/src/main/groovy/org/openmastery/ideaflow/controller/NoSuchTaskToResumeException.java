package org.openmastery.ideaflow.controller;

public class NoSuchTaskToResumeException extends Exception {

	public NoSuchTaskToResumeException(String taskName) {
		super("No task with name=" + taskName + " found on server");
	}

}
