package hu.hvj.marci.icoviewer;

import hu.hvj.marci.fileviewer.Forditas;

public class IconDirEntry {

	public static final int BMP = 0, PNG = 1;

	private final byte[] content;
	private final int width, height, paletteEntryCount, colorPlane, bitsPerPixel;
	private final long imageSize, imageOffset;
	private int format;

	public IconDirEntry(byte[] content) {
		this.content = content;
		if (this.content.length != 16)
			throw new IllegalArgumentException(
					String.format("A content tömb hossza csak 16 lehet! (%d)", this.content.length));
		if (this.content[3] != 0)
			throw new IllegalArgumentException(
					String.format("The reserved bit is %d instead of 0!", Helper.btpi(this.content[3])));

		int tmp = Helper.btpi(this.content[0]);
		if (tmp == 0)
			tmp = 256;
		this.width = tmp;
		tmp = Helper.btpi(this.content[1]);
		if (tmp == 0)
			tmp = 256;
		this.height = tmp;
		this.paletteEntryCount = Helper.btpi(this.content[2]);
		this.colorPlane = Helper.twoBytesToIntLSBFirst(this.content[4], this.content[5]);
		this.bitsPerPixel = Helper.twoBytesToIntLSBFirst(this.content[6], this.content[7]);
		this.imageSize = (long) Helper.fourBytesToIntLSBFirst(this.content[8], this.content[9], this.content[10],
				this.content[11]) & 0xFFFFFFFFL;
		this.imageOffset = (long) Helper.fourBytesToIntLSBFirst(this.content[12], this.content[13], this.content[14],
				this.content[15]) & 0xFFFFFFFFL;
		this.format = -1;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getPaletteEntryCount() {
		return paletteEntryCount;
	}

	public boolean hasPalette() {
		return this.paletteEntryCount != 0;
	}

	public int getColorPlane() {
		return colorPlane;
	}

	public int getBitsPerPixel() {
		return bitsPerPixel;
	}

	public long getImageSize() {
		return imageSize;
	}

	public long getImageOffset() {
		return imageOffset;
	}

	public void setFormat(int format) {
		this.format = format;
	}

	public int getFormat() {
		return this.format;
	}

	public String getFormatName() {
		switch (this.format) {
		case BMP:
			return Forditas.DEFAULT.getText("ico.format.bmp");
		case PNG:
			return Forditas.DEFAULT.getText("ico.format.png");
		default:
			return Forditas.DEFAULT.getText("ico.undefined");
		}
	}

	@Override
	public String toString() {
		return String.format("   width: %d%n   height: %d%n   has palette: %b%n"
				+ (this.hasPalette() ? "   palette entries: " + this.getPaletteEntryCount() + "%n" : "")
				+ "   color plane: %d%n   bits per pixel: %d%n   image size: %d bytes%n   image offset: %d bytes",
				this.getWidth(), this.getHeight(), this.hasPalette(), this.getColorPlane(), this.getBitsPerPixel(),
				this.getImageSize(), this.getImageOffset());
	}

}
