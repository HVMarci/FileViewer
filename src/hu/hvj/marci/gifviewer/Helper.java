package hu.hvj.marci.gifviewer;

public class Helper {

	public static boolean equalsFromIndex(byte[] arr1, byte[] arr2, int start) {
		if (arr1.length < arr2.length + start) {
			throw new IllegalArgumentException("Index out of bounds! " + start);
		} else {
			for (int i = start, j = 0; j < arr2.length; i++, j++) {
				if (arr1[i] != arr2[j]) {
					return false;
				}
			}

			return true;
		}
	}

	public static int twoBytesToIntLSBFirst(byte[] buf) {
		return ((buf[1] & 0xFF) << 8) | (buf[0] & 0xFF);
	}

	public static boolean[] byteToBooleanArray(byte b) {
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

	public static int booleansToInt(boolean[] bools, int start, int end) {
		int b = 0;
		for (int i = start, j = end - start - 1; i < end; i++, j--) {
			b |= (bools[i] == true ? 1 : 0) << j;
		}
		return b;
	}

}
