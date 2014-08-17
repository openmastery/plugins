package com.ideaflow.intellij.file;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class IdeaFlowMapFileTypeLoader extends FileTypeFactory {
	public void createFileTypes(@NotNull FileTypeConsumer consumer) {
		consumer.consume(IdeaFlowMapFileType.IMF_FILE_TYPE, IdeaFlowMapFileType.IFM_EXTENSION);
	}
}
