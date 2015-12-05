package com.ideaflow.controller


public interface IDEService<T> {

	String getActiveFileSelection(T context)

	String promptForInput(T context, String title, String message)

}