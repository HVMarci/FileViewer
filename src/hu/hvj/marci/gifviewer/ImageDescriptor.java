package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;

public class ImageDescriptor {

	private final int imageLeftPosition, imageTopPosition, imageWidth, imageHeigth; // two byte unsigned
	private final boolean localColorTableFlag, interlaceFlag, sortFlag; // packed fields
	private final int sizeOfLocalColorTable;

	public ImageDescriptor(InputStream is) throws IOException {
		byte[] buf = new byte[2];
		is.read(buf);
		this.imageLeftPosition = Helper.twoBytesToIntLSBFirst(buf);

		is.read(buf);
		this.imageTopPosition = Helper.twoBytesToIntLSBFirst(buf);

		is.read(buf);
		this.imageWidth = Helper.twoBytesToIntLSBFirst(buf);

		is.read(buf);
		this.imageHeigth = Helper.twoBytesToIntLSBFirst(buf);

		byte packedFieldsByte = (byte) is.read();
		boolean[] packedFields = Helper.byteToBooleanArray(packedFieldsByte);

		this.localColorTableFlag = packedFields[0];
		this.interlaceFlag = packedFields[1];
		this.sortFlag = packedFields[2];

		// packedFields[3..4] reserved bits

		this.sizeOfLocalColorTable = Helper.booleansToInt(packedFields, 5, 8);
	}

	public int getImageLeftPosition() {
		return imageLeftPosition;
	}

	public int getImageTopPosition() {
		return imageTopPosition;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public int getImageHeigth() {
		return imageHeigth;
	}

	public boolean getLocalColorTableFlag() {
		return localColorTableFlag;
	}

	public boolean getInterlaceFlag() {
		return interlaceFlag;
	}

	public boolean getSortFlag() {
		return sortFlag;
	}

	public int getSizeOfLocalColorTable() {
		return sizeOfLocalColorTable;
	}

}
