package hu.hvj.marci.pngviewer.zlib;

public class BitAndByteIndex {

	public int bitIndex, byteIndex;

	public BitAndByteIndex() {
		this.bitIndex = 0;
		this.byteIndex = 0;
	}

	public void leptetes(int count) {
		this.bitIndex += count;

		this.byteIndex += this.bitIndex / 8;
		this.bitIndex -= this.bitIndex / 8 * 8;
	}
	
	public void leptetes() {
		this.leptetes(1);
	}

	public void nextByte() {
		this.leptetes(8 - this.bitIndex);
	}

}
