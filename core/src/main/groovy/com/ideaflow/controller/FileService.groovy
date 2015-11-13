package com.ideaflow.controller

class FileService {

   	void createNewFile(File file, String contents) {
   		file.parentFile.mkdirs()
        file.text = contents
   	}

   	void writeFile(File file, String contents) {
        if (!file.exists()) {
            throw new Exception("Invalid file: $file.path")
        }

        file.text = contents
   	}

   	boolean fileExists(File file) {
        file.exists()
   	}

   	String readFile(File file) {
        file.text
   	}

}
