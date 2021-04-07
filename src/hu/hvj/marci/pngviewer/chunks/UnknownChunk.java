package hu.hvj.marci.pngviewer.chunks;

public class UnknownChunk extends Chunk {

	public UnknownChunk(byte[] length, byte[] name, byte[] content, byte[] crc) {
		super(length, name, content, crc);
	}

	public UnknownChunk(int length, String name, byte[] content, byte[] crc) {
		super(length, name, content, crc);
	}

	@Override
	public String getInfo() {
		return "Unknown chunk";
	}

	@Override
	public String getAttributes() {
		return super.getAttributes() + "; " + (this.isPrivate() ? "Private" : "Public");
	}

}
