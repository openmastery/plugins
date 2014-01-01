package com.ideaflow.intellij

import com.ideaflow.controller.IDEService
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.ui.UIBundle
import com.intellij.openapi.module.ModuleManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.ui.Messages


class IDEServiceImpl implements IDEService {

    Project project

    IDEServiceImpl(Project project) {
        this.project = project
    }

    String getActiveFileSelection() {
        String file = null
        def files = FileEditorManager.getInstance(project).getSelectedFiles()
        if (files.size() > 0) {
            file = files[0].name
        }
        return file
    }

    String promptForInput(String title, String message) {
        String note = Messages.showInputDialog(message,
                UIBundle.message(title), Messages.getQuestionIcon());
        return note

    }

    void createNewFile(String relativePath, String contents) {
        FileHandler handler = createHandler(relativePath)
        handler.validateFolder()

        runWriteAction {
            handler.create(contents)
        }
    }

    void writeToFile(String relativePath, String contents) {
        FileHandler handler = createHandler(relativePath)
        handler.validateFolder()
        handler.validateFileExists()

        runWriteAction {
            handler.write(contents)
        }
    }

    boolean fileExists(String relativePath) {
        FileHandler handler = createHandler(relativePath)
        handler.fileExists()
    }

    void validateFilePath(String relativePath) {
        FileHandler handler = createHandler(relativePath)
        handler.validateFolder()
    }

    String readFile(String relativePath) {
        FileHandler handler = createHandler(relativePath)
        handler.validateFileExists()
        handler.read()
    }

    private FileHandler createHandler(String relativePath) {
        Module module = ModuleManager.getInstance(project).findModuleByName('ideaflow')
        if (module.moduleFile == null) {
            throw new Exception("Module file missing! Did you accidentally delete it?")
        }
        new FileHandler(module.moduleFile.parent, relativePath)
    }

    private void runWriteAction(Closure closure) {
        WriteAction action = new WriteAction(closure)
        CommandProcessor.getInstance().executeCommand(project, action, 'cmd', null);
        if (action.exception) {
            throw action.exception
        }
    }

    private static class WriteAction implements Runnable {
        Closure closure
        Exception exception

        WriteAction(Closure closure) {
            this.closure = closure
        }

        void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    try {
                        closure.call()
                    }
                    catch (Exception e) {
                        exception = e;
                    }
                }
            });
        }
    }

    private static class FileHandler {

        VirtualFile baseFolder
        String relativePath
        String subFolder
        String fileName

        FileHandler(VirtualFile baseFolder, String relativePath) {
            this.baseFolder = baseFolder
            this.relativePath = relativePath
            parseSubFolder()
            parseFileName()
        }

        private String parseSubFolder() {
            int index = relativePath.lastIndexOf('/')
            if (index >= 0) {
                subFolder = relativePath.substring(0, index)
            }
        }

        private String parseFileName() {
            int index = relativePath.lastIndexOf('/')
            if (index >= 0) {
                fileName = relativePath.substring(index + 1)
            } else {
                fileName = relativePath
            }
        }

        void validateFolder() {
            if (subFolder) {
                VirtualFile folder = baseFolder.findFileByRelativePath(subFolder)
                if (folder == null || !folder.isDirectory() || !folder.exists()) {
                    throw new Exception("Invalid folder: $subFolder")
                }
            }
        }

        void validateFileExists() {
            if (!fileExists()) {
                throw new Exception("Invalid file: $fileName")
            }
        }

        boolean fileExists() {
            VirtualFile file = getFile()
            file != null && !file.isDirectory() && file.exists()
        }

        String read() {
            VirtualFile file = getFile()
            VfsUtil.loadText(file)
        }

        void create(String contents) {
            VirtualFile file = findSubFolder().createChildData(this, fileName);
            VfsUtil.saveText(file, contents);
        }

        void write(String contents) {
            VirtualFile file = getFile()
            VfsUtil.saveText(file, contents)
        }

        VirtualFile getFile() {
            baseFolder.findFileByRelativePath(relativePath)
        }

        VirtualFile findSubFolder() {
            VirtualFile folder = baseFolder
            if (subFolder) {
                folder = baseFolder.findFileByRelativePath(subFolder)
            }
            return folder
        }

    }


}
