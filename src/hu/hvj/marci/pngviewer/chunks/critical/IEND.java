package hu.hvj.marci.pngviewer.chunks.critical;

import hu.hvj.marci.pngviewer.chunks.Chunk;

public class IEND extends Chunk {

	public IEND(byte[] content, byte[] crc) {
		super(0, "IEND", content, crc);
		if (content.length != 0)
			throw new IllegalArgumentException("Az IEND chunk hossza csak 0 lehet! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		return "Image end chunk";
	}

}
