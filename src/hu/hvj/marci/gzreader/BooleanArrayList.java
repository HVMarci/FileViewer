package hu.hvj.marci.gzreader;

public class BooleanArrayList {
	private int size = 0, value = 0;

	// TODO byte array for overload
	private byte[] overloadBuffer = new byte[100];
	private int overloadBufferSize = 0;

	public void add(byte b) {
		if (size <= 24) {
			value |= (b & 0xFF) << size;
			size += 8;
		} else {
			int maradekHely = 32 - size;
			value |= (b & 0xFF) << size; // ami befér, azt berakjuk
			int maradek = (b & 0xFF) >>> (8 - maradekHely);
			overloadBuffer[overloadBufferSize] = (byte) maradek;
			overloadBufferSize++;
		}
	}

	public boolean getLast() {
		boolean b = (value & 0b1) == 1;
		value >>>= 1;
		return b;
	}

	public int getNextTwoBitInteger() {
		return this.getNextXBitIntegerLSBFirst(2);
	}

	public int getNextXBitIntegerLSBFirst(int x) {
		int szam = 0;
		for (int i = 0; i < x; i++) {
			int b = this.getLast() ? 1 : 0;
			szam |= b << i;
		}
		return szam;
	}

	public int getNextXBitIntegerMSBFirst(int x) {
		int szam = 0;
		for (int i = 0; i < x; i++) {
			int b = this.getLast() ? 1 : 0;
			szam <<= 1;
			szam |= b;
		}
		return szam;
	}

	public int getNextTwoByteIntegerLSBFirst() {
		int counter = 0, szam = 0;
		while (counter < 16) {
			int b = this.getLast() ? 1 : 0;
			szam |= b << counter;
			counter++;
		}
//		counter = 0;
//		while (counter < 8) {
//			int b = this.getLast() ? 1 : 0;
//			szam |= b << counter;
//			counter++;
//		}
		return szam;
	}

	public void nextByte() {
		while (this.size % 8 != 0) {
			value >>>= 1;
			size--;
		}
	}

	public int size() {
		return size;
	}
}

//public class BooleanArrayList extends ArrayList<Boolean> {
//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 5261307688658786823L;
//
//	@Override
//	public Boolean get(int index) {
//		Boolean b = this.remove(index);
//		return b;
//	}
//
//	public boolean getLast() {
//		return this.get(this.size() - 1);
//	}
//
//	public int getNextTwoBitInteger() {
//		return this.getNextXBitIntegerLSBFirst(2);
//	}
//
//	public int getNextXBitIntegerLSBFirst(int x) {
//		int szam = 0;
//		for (int i = 0; i < x; i++) {
//			int b = this.getLast() ? 1 : 0;
//			szam |= b << i;
//		}
//		return szam;
//	}
//
//	public int getNextXBitIntegerMSBFirst(int x) {
//		int szam = 0;
//		for (int i = 0; i < x; i++) {
//			int b = this.getLast() ? 1 : 0;
//			szam <<= 1;
//			szam |= b;
//		}
//		return szam;
//	}
//
//	public int getNextTwoByteIntegerLSBFirst() {
//		int counter = 0, szam = 0;
//		while (counter < 16) {
//			int b = this.getLast() ? 1 : 0;
//			szam |= b << counter;
//			counter++;
//		}
////		counter = 0;
////		while (counter < 8) {
////			int b = this.getLast() ? 1 : 0;
////			szam |= b << counter;
////			counter++;
////		}
//		return szam;
//	}
//
//	public void nextByte() {
//		while (this.size() % 8 != 0) {
//			this.remove(this.size() - 1);
//		}
//	}
//
//	public byte getNextByte() {
//		int counter = 0, szam = 0;
//		while (counter < 8) {
//			int b = this.getLast() ? 1 : 0;
//			szam |= b << counter;
//			counter++;
//		}
//		return (byte) szam;
//	}
//
//}
