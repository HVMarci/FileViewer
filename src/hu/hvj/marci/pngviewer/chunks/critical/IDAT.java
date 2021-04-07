package hu.hvj.marci.pngviewer.chunks.critical;

import hu.hvj.marci.pngviewer.chunks.Chunk;

public class IDAT extends Chunk {

	public IDAT(byte[] content, byte[] crc) {
		super(content.length, "IDAT", content, crc);
	}

	@Override
	public String getInfo() {
		return "Image data";
	}

}
