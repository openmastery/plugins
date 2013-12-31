package com.ideaflow.controller


public interface IDEService {

    String getActiveFileSelection()

    void createNewFile(String relativePath, String initialContent)

    boolean fileExists(String relativePath)

    String readFile(String relativePath)

    void writeToFile(String relativePath, String contents)

    void validateFilePath(String relativePath)
	
	String promptForInput(String title, String message)

}