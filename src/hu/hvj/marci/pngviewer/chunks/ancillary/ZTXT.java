package hu.hvj.marci.pngviewer.chunks.ancillary;

import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;

import hu.hvj.marci.pngviewer.ISO88591;
import hu.hvj.marci.pngviewer.chunks.Chunk;

public class ZTXT extends Chunk {

	public ZTXT(byte[] content, byte[] crc) {
		super(content.length, "zTXt", content, crc);
		if (content.length < 4)
			throw new IllegalArgumentException("Az iCCP chunk hossza minimum 4! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		return "Keyword: \"" + this.getKeyword() + "\", Compression method: "
				+ (this.getCompressionMethod() == 0 ? "Deflate / Inflate (0)" : this.getCompressionMethod());
	}

	public String getKeyword() {
		int length = 0;

		for (int i = 0; i < this.content.length; i++) {
			if (this.content[i] == 0) {
				length = i;
				break;
			}
		}

		if (length == 0 || length > 79) {
			throw new ArrayIndexOutOfBoundsException("A keyword hossza " + length + "!");
		}

		return ISO88591.toISO88591(getArrayPart(this.content, length - 1));
	}

	public byte getCompressionMethod() {
		return this.content[this.getKeyword().length() + 1];
	}

}
