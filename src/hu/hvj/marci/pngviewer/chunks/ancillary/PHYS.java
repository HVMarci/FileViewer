package hu.hvj.marci.pngviewer.chunks.ancillary;

import static hu.hvj.marci.pngviewer.PNGHelper.fourBytesToIntMSBFirst;
import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;

import hu.hvj.marci.pngviewer.chunks.Chunk;

public class PHYS extends Chunk {

	public PHYS(byte[] content, byte[] crc) {
		super(9, "pHYs", content, crc);
		if (content.length != 9)
			throw new IllegalArgumentException("A pHYs chunk hossza csak 9 lehet! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		String suffix = " pixels per ";
		if (this.getUnitType()) {
			suffix += "meter";
		} else {
			suffix += "unit";
		}
		return this.getPixelsPerUnitX() + "×" + this.getPixelsPerUnitY() + suffix;
	}

	public int getPixelsPerUnitX() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 3));
	}

	public int getPixelsPerUnitY() {
		return fourBytesToIntMSBFirst(getArrayPart(this.content, 4, 7));
	}

	public boolean getUnitType() {
		return this.content[8] == 1;
	}

}
