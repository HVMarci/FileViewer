package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;

public class LogicalScreen {

	private final LogicalScreenDescriptor logicalScreenDescriptor;
	private final ColorTable globalColorTable;

	public LogicalScreen(InputStream is) throws IOException {
		this.logicalScreenDescriptor = new LogicalScreenDescriptor(is);

		if (this.logicalScreenDescriptor.getGlobalColorTableFlag()) {
			this.globalColorTable = new ColorTable(is, this.logicalScreenDescriptor.getSizeOfGlobalColorTable());
		} else {
			this.globalColorTable = ColorTable.STATIC_COLOR_TABLE;
		}
	}

	public LogicalScreenDescriptor getLogicalScreenDescriptor() {
		return logicalScreenDescriptor;
	}

	public int getImageWidth() {
		return this.logicalScreenDescriptor.getLogicalScreenWidth();
	}

	public int getImageHeight() {
		return this.logicalScreenDescriptor.getLogicalScreenHeight();
	}

	public boolean hasGlobalColorTable() {
		return this.logicalScreenDescriptor.getGlobalColorTableFlag();
	}

	public ColorTable getGlobalColorTable() {
		return globalColorTable;
	}

}
