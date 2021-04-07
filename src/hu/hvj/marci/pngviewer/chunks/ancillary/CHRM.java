package hu.hvj.marci.pngviewer.chunks.ancillary;

import hu.hvj.marci.pngviewer.chunks.Chunk;
import static hu.hvj.marci.pngviewer.PNGHelper.fourBytesToIntMSBFirst;
import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;
import static hu.hvj.marci.pngviewer.PNGHelper.format;

public class CHRM extends Chunk {

	public CHRM(byte[] content, byte[] crc) {
		super(32, "cHRM", content, crc);
		if (content.length != 32)
			throw new IllegalArgumentException("A cHRM chunk hossza csak 32 lehet! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		return "WP(" + format(this.getWPx()) + "; " + format(this.getWPy()) + "), R(" + format(this.getRx()) + "; "
				+ format(this.getRy()) + "), G(" + format(this.getGx()) + "; " + format(this.getGy()) + "), B("
				+ format(this.getBx()) + "; " + format(this.getBy()) + ")";
	}

	public int getWPx() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 3));
	}

	public int getWPy() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 4, 7));
	}

	public int getRx() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 8, 11));
	}

	public int getRy() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 12, 15));
	}

	public int getGx() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 16, 19));
	}

	public int getGy() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 20, 23));
	}

	public int getBx() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 24, 27));
	}

	public int getBy() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 28, 31));
	}
}
