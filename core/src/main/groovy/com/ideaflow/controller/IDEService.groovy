package com.ideaflow.controller


public interface IDEService<T> {

	String getActiveFileSelection(T context)

	void createNewFile(T context, File file, String initialContent)

	boolean fileExists(T context, File file)

	String readFile(T context, File file)

	void writeFile(T context, File file, String contents)

	String promptForInput(T context, String title, String message)

}