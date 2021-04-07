package hu.hvj.marci.pngviewer.zlib;

import static hu.hvj.marci.pngviewer.PNGHelper.btpi;

public class Adler32 {

	private static final int BASE = 65521;

	public static int checksum(byte[] decompressedData) {
		int s1 = 1, s2 = 0;

		for (int i = 0; i < decompressedData.length; i++) {
			s1 = (s1 + btpi(decompressedData[i])) % BASE;
			s2 = (s2 + s1) % BASE;
		}

		return (s2 << 16) + s1;
	}

}
