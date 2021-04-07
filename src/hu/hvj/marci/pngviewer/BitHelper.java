package hu.hvj.marci.pngviewer;

public class BitHelper {

	public static boolean[] bytesToBooleans(byte[] bytes) {
		boolean[] bools = new boolean[bytes.length * 8];

		for (int i = 0; i < bytes.length; i++) {
			int j = i * 8;
			bools[j] = (bytes[i] & 0x80) != 0;
			bools[j + 1] = (bytes[i] & 0x40) != 0;
			bools[j + 2] = (bytes[i] & 0x20) != 0;
			bools[j + 3] = (bytes[i] & 0x10) != 0;
			bools[j + 4] = (bytes[i] & 0x8) != 0;
			bools[j + 5] = (bytes[i] & 0x4) != 0;
			bools[j + 6] = (bytes[i] & 0x2) != 0;
			bools[j + 7] = (bytes[i] & 0x1) != 0;
		}

		return bools;
	}

	public static boolean[] byteToBooleans(byte b) {
		boolean[] bools = new boolean[8];

		bools[0] = (b & 0x80) != 0;
		bools[1] = (b & 0x40) != 0;
		bools[2] = (b & 0x20) != 0;
		bools[3] = (b & 0x10) != 0;
		bools[4] = (b & 0x8) != 0;
		bools[5] = (b & 0x4) != 0;
		bools[6] = (b & 0x2) != 0;
		bools[7] = (b & 0x1) != 0;

		return bools;
	}

	public static byte booleansToByte(boolean[] b) {
		if (b.length < 1 || b.length > 8)
			throw new IllegalArgumentException(
					String.format("A b tömb hossza csak min. 1 és max. 8 lehet! (%d)", b.length));

		byte szam = 0;

		for (int i = 0; i < b.length; i++) {
			int elem = b[i] ? 1 : 0;
			szam |= elem * (byte) Math.pow(2, b.length - i - 1);
		}

		return szam;
	}

	public static byte[] booleansToBytes(boolean[] b) {
		if (b.length % 8 != 0)
			throw new IllegalArgumentException(String.format("b.length %% 8 == %d", b.length % 8));

		byte[] bytes = new byte[b.length / 8];

		for (int i = 0; i < b.length; i += 8) {
			bytes[i / 8] = booleansToByte(PNGHelper.getArrayPart(b, i, i + 7));
		}

		return bytes;
	}

	public static boolean[] rotateBytesInBooleanArray(boolean[] bits) {
		if (bits.length % 8 != 0)
			throw new IllegalArgumentException(String.format("bits.length %% 8 == %d", bits.length % 8));

		boolean[] rotated = new boolean[bits.length];

		for (int i = 0; i < bits.length; i++) {
			rotated[i + 8 - (i % 8) * 2 - 1] = bits[i];
		}

		return rotated;
	}

	public static boolean[] rbiba(boolean[] bits) {
		return rotateBytesInBooleanArray(bits);
	}

	public static boolean[] rotateBits(boolean[] bits) {
		boolean[] rotated = new boolean[bits.length];

		for (int i = 0; i < bits.length; i++) {
			rotated[i] = bits[bits.length - i - 1];
		}

		return rotated;
	}

	public static boolean[] rb(boolean[] bits) {
		return rotateBits(bits);
	}

	public static int binaryToDecimal(boolean[] bits) {
		if (bits.length == 0) {
			return 0;
		}
		int szam = 0;

		for (int i = 0; i < bits.length; i++) {
			szam += (bits[bits.length - i - 1] ? 1 : 0) << i;
		}

		return szam;
	}

}
