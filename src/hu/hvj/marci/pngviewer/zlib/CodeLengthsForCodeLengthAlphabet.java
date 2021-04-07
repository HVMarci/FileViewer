package hu.hvj.marci.pngviewer.zlib;

public class CodeLengthsForCodeLengthAlphabet {

	private final int[] decoded;
	private final static int[] indexes = { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };

	public CodeLengthsForCodeLengthAlphabet(int[] array) {
		this.decoded = new int[19];

		for (int i = 0; i < this.decoded.length; i++) {
			this.decoded[i] = 0;
		}

		for (int i = 0; i < array.length; i++) {
			this.decoded[indexes[i]] = array[i];
		}
	}

	public int getLength(int index) {
		return decoded[index];
	}

}
