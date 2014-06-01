package com.ideaflow.controller


public interface IDEService {

	String getActiveFileSelection()

	void createNewFile(File file, String initialContent)

	boolean fileExists(File file)

	String readFile(File file)

	void writeFile(File file, String contents)

	String promptForInput(String title, String message)

}