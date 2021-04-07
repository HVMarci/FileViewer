package hu.hvj.marci.pngviewer.chunks.ancillary;

import hu.hvj.marci.pngviewer.chunks.Chunk;
import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;

import hu.hvj.marci.pngviewer.ISO88591;

public class ICCP extends Chunk {

	public ICCP(byte[] content, byte[] crc) {
		super(content.length, "iCCP", content, crc);
		if (content.length < 4)
			throw new IllegalArgumentException("Az iCCP chunk hossza minimum 4! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		return "Profile name: \"" + this.getProfileName() + "\", Compression method: "
				+ (this.getCompressionMethod() == 0 ? "Deflate / Inflate (0)" : this.getCompressionMethod());
	}

	public String getProfileName() {
		int length = 0;

		for (int i = 0; i < this.content.length; i++) {
			if (this.content[i] == 0) {
				length = i;
				break;
			}
		}

		if (length == 0 || length > 79) {
			throw new ArrayIndexOutOfBoundsException("A profile name hossza " + length + "!");
		}

		return ISO88591.toISO88591(getArrayPart(this.content, length - 1));
	}

	public byte getCompressionMethod() {
		return this.content[this.getProfileName().length() + 1];
	}

}
