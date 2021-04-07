package hu.hvj.marci.pngviewer.chunks.ancillary;

import hu.hvj.marci.pngviewer.chunks.Chunk;

public class SRGB extends Chunk {

	public SRGB(byte[] content, byte[] crc) {
		super(1, "sRGB", content, crc);
		if (content.length != 1)
			throw new IllegalArgumentException("Az sRGB chunk hossza csak 1 lehet! (" + content.length + ")");
		if (content[0] < 0 || content[0] > 4)
			throw new IllegalArgumentException("Az sRGB chunk értéke csak 0, 1, 2 vagy 3 lehet! (" + content[0] + ")");
	}

	@Override
	public String getInfo() {
		switch (this.content[0]) {
		case 0:
			return "Perceptual intent (0)";
		case 1:
			return "Relative colorimetric intent (1)";
		case 2:
			return "Saturation intent (2)";
		case 3:
			return "Absolute colorimetric intent (3)";
		default:
			return "ERROR (" + this.content[0] + ")";
		}
	}

}
