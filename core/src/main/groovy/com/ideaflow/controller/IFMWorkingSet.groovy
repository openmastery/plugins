package com.ideaflow.controller

class IFMWorkingSet {

	private File activeIfmFile
	private List<File> ifmFiles = []
	private List<IFMWorkingSetListener> listeners = []

	void addWorkingSetListener(IFMWorkingSetListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener)
		}
	}

	void removeWorkingSetListener(IFMWorkingSetListener listener) {
		listeners.remove(listener)
	}

	private void notifyListeners() {
		listeners.each { IFMWorkingSetListener listener ->
			listener.onWorkingSetChanged()
		}
	}

	List<File> getIfmFiles() {
		ifmFiles.clone() as List
	}

	void setIfmFiles(List<File> files) {
		ifmFiles.clear()
		ifmFiles.addAll(files)
	}

	void setActiveIfmFile(File ifmFile) {
		if (activeIfmFile != ifmFile) {
			if (!ifmFiles.contains(ifmFile)) {
				ifmFiles.add(ifmFile)
			}
			notifyListeners()
		}
	}

	void removeIfmFile(File ifmFile) {
		if (ifmFiles.remove(ifmFile)) {
			if (ifmFile == activeIfmFile) {
				activeIfmFile = null
			}
			notifyListeners()
		}
	}

	boolean isEmpty() {
		ifmFiles.isEmpty()
	}

}
