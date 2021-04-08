package hu.hvj.marci.icoviewer;

import java.util.Arrays;

import hu.hvj.marci.fileviewer.Forditas;

public class Header {

	private final byte[] content;
	private final int type, numberOfImages;

	public Header(byte[] content) {
		this.content = content;
		this.type = Helper.twoBytesToIntLSBFirst(this.content[2], this.content[3]);
		this.numberOfImages = Helper.twoBytesToIntLSBFirst(this.content[4], this.content[5]);

		if (content.length != 6)
			throw new IllegalArgumentException(
					String.format("A content tömb hossza csak 6 lehet! (%d)", content.length));

		if (!checkContent())
			throw new IllegalArgumentException(String.format("Hibás header! (%s)", Arrays.toString(this.content)));
	}

	private boolean checkContent() {
		int reserved = Helper.twoBytesToIntLSBFirst(this.content[0], this.content[1]);
		int type = Helper.twoBytesToIntLSBFirst(this.content[2], this.content[3]);
		return reserved == 0 && (type == 1 || type == 2);
	}

	public int getType() {
		return type;
	}
	
	public String getTypeName() {
		if (this.type == 1) {
			return Forditas.DEFAULT.getText("ico.type.icon");
		} else {
			return Forditas.DEFAULT.getText("ico.type.cursor");
		}
	}

	public int getNumberOfImages() {
		return numberOfImages;
	}

}
