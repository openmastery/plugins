package com.ideaflow.intellij.file;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import javax.swing.Icon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class IdeaFlowMapFileType implements FileType {

	public static final IdeaFlowMapFileType IMF_FILE_TYPE = new IdeaFlowMapFileType();
	public static final String IFM_EXTENSION = "ifm";

	public static final Icon IFM_ICON = IconLoader.findIcon("/icons/ideaflow.png");

	private IdeaFlowMapFileType() {
	}

	@NotNull
	public String getName() {
		return "IdeaFlowMap";
	}

	@NotNull
	public String getDescription() {
		return "Idea Flow Map";
	}

	@NotNull
	public String getDefaultExtension() {
		return IFM_EXTENSION;
	}

	public Icon getIcon() {
		return IFM_ICON;
	}

	public boolean isBinary() {
		return false;
	}

	public boolean isReadOnly() {
		return false;
	}

	@Nullable
	public String getCharset(@NotNull VirtualFile file, byte[] content) {
		return null;
	}

}
