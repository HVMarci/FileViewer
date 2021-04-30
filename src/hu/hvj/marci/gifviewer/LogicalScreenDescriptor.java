package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;

public class LogicalScreenDescriptor {

	private final int logicalScreenWidth, logicalScreenHeight; // 2 byte unsigned
	private final boolean globalColorTableFlag, sortFlag; // packed fields
	private final int colorResolution, sizeOfGlobalColorTable; // packed fields
	private final byte backgroundIndex, pixelAspectRatio;

	public LogicalScreenDescriptor(InputStream is) throws IOException {
		byte[] buf = new byte[2];
		is.read(buf);
		this.logicalScreenWidth = Helper.twoBytesToIntLSBFirst(buf);

		is.read(buf);
		this.logicalScreenHeight = Helper.twoBytesToIntLSBFirst(buf);

		buf = new byte[3];
		is.read(buf);
		boolean[] packedFields = Helper.byteToBooleanArray(buf[0]);
		this.globalColorTableFlag = packedFields[0];
		this.colorResolution = Helper.booleansToInt(packedFields, 1, 4);
		this.sortFlag = packedFields[4];
		this.sizeOfGlobalColorTable = Helper.booleansToInt(packedFields, 5, 8);

		this.backgroundIndex = buf[1];
		this.pixelAspectRatio = buf[2];
	}

	public boolean getGlobalColorTableFlag() {
		return globalColorTableFlag;
	}

	public boolean getSortFlag() {
		return sortFlag;
	}

	public int getColorResolution() {
		return colorResolution;
	}

	public int getSizeOfGlobalColorTable() {
		return sizeOfGlobalColorTable;
	}

	public byte getBackgroundIndex() {
		return backgroundIndex;
	}

	public byte getPixelAspectRatio() {
		return pixelAspectRatio;
	}

	public int getLogicalScreenWidth() {
		return logicalScreenWidth;
	}

	public int getLogicalScreenHeight() {
		return logicalScreenHeight;
	}

}
