package hu.hvj.marci.gzreader;

public class MyBitSet {

	private byte[] data = new byte[500];
	private int bytePointer = 0, bitPointer = 0, endPointer = 0, size = 0;

	public void add(byte b) {
		data[endPointer] = b;
		endPointer++;
		size += 8;
		if (endPointer == data.length) {
			endPointer = 0;
		}
		if (size > 500 || size < 0) {
			System.err.println("BAJVANNN");
		}
	}

	public void add(byte[] b) {
		if (b.length == 1) {
			add(b[0]);
		} else {
			if (data.length > endPointer + b.length) {
				for (int i = 0; i < b.length; i++) {
					data[endPointer + i] = b[i];
				}
				endPointer += b.length;
			} else {
				int befer = data.length - endPointer;
				for (int i = 0; i < befer; i++) {
					data[endPointer + i] = b[i];
				}
				int elore = b.length - befer;
				for (int i = 0; i < elore; i++) {
					data[i] = b[i + befer];
				}
				endPointer = elore;
			}
			size += b.length * 8;
			if (size > 500 || size < 0) {
				System.err.println("BAJVANNN");
			}
		}
	}

	public int getLastBit() {
		int b = (data[bytePointer] >>> bitPointer) & 0b1;
		bitPointer++;

		if (bitPointer == 8) {
			bitPointer = 0;
			bytePointer++;
			if (bytePointer == data.length) {
				bytePointer = 0;
			}
		}

		size--;
		if (size < 0) {
			System.err.println("BAJJJ");
		}

		return b;
	}

	public boolean getLast() {
		boolean b = ((data[bytePointer] >>> bitPointer) & 0b1) == 1;
		bitPointer++;

		if (bitPointer == 8) {
			bitPointer = 0;
			bytePointer++;
			if (bytePointer == data.length) {
				bytePointer = 0;
			}
		}

		size--;
		if (size < 0) {
			System.err.println("BAJJJ");
		}

		return b;
	}

	public int getNextXBitIntegerLSBFirst(int x) {
		int szam = 0;
		for (int i = 0; i < x; i++) {
			int b = this.getLastBit();
			szam |= b << i;
		}
		return szam;
	}

	public int getNextXBitIntegerMSBFirst(int x) {
		int szam = 0;
		for (int i = 0; i < x; i++) {
			int b = this.getLastBit();
			szam <<= 1;
			szam |= b;
		}
		return szam;
	}

	public void nextByte() {
		int maradek = 8 - bitPointer;
		if (maradek != 8) {
			size -= maradek;
			bitPointer = 0;
			bytePointer++;
			if (bytePointer == data.length) {
				bytePointer = 0;
			}
			if (size < 0) {
				System.err.println("BAJJJ");
			}
		}
	}

	public int size() {
		return size;
	}

	public byte getNextByte() {
		return (byte) getNextXBitIntegerLSBFirst(8);
	}

}