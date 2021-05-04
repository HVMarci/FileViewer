package hu.hvj.marci.global;

public class NormalBitSet {

	private byte[] data;
	private int bytePointer = 0, bitPointer = 7, endPointer = 0, size = 0;

	public NormalBitSet(int length) {
		this.data = new byte[length];
	}

	public NormalBitSet() {
		this(500);
	}

	public void add(byte b) {
		data[endPointer] = b;
		endPointer++;
		size += 8;
		if (endPointer == data.length) {
			endPointer = 0;
		}
		if (size > data.length * 8 || size < 0) {
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
			if (size > data.length * 8 || size < 0) {
				System.err.println("BAJVANNN");
			}
		}
	}

	public void add(byte[][] b) {
		for (int i = 0; i < b.length; i++) {
			add(b[i]);
		}
	}

	public int getFirstBit() {
		int b = (data[bytePointer] >>> bitPointer) & 0b1;
		bitPointer--;

		if (bitPointer == -1) {
			bitPointer = 7;
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

	public boolean getFirst() {
		boolean b = ((data[bytePointer] >>> bitPointer) & 0b1) == 1;
		bitPointer--;

		if (bitPointer == -1) {
			bitPointer = 7;
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

	public int getNextXBitInteger(int x) {
		int szam = 0;
		for (int i = 0; i < x; i++) {
			int b = this.getFirstBit();
			szam <<= 1;
			szam |= b;
		}
		return szam;
	}

	public void nextByte() {
		if (bitPointer != 7) {
			size -= bitPointer + 1;
			bitPointer = 7;
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
		return (byte) getNextXBitInteger(8);
	}

	@Override
	public String toString() {
		return "Size: " + size + "\nData.length: " + data.length + "\nbitPointer: " + bitPointer + "\nbytePointer: "
				+ bytePointer;
	}

}