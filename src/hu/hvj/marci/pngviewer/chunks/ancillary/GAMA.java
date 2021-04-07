package hu.hvj.marci.pngviewer.chunks.ancillary;

import hu.hvj.marci.pngviewer.PNGHelper;
import hu.hvj.marci.pngviewer.chunks.Chunk;

public class GAMA extends Chunk {

	public GAMA(byte[] content, byte[] crc) {
		super(4, "gAMA", content, crc);
		if (content.length != 4)
			throw new IllegalArgumentException("A gAMA chunk hossza csak 4 lehet! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		return "Value: " + PNGHelper.format(PNGHelper.fourBytesToIntMSBFirst(this.content));
	}

}
