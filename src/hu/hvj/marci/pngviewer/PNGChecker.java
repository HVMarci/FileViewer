package hu.hvj.marci.pngviewer;

import java.io.IOException;
import java.io.InputStream;

public class PNGChecker {

	public static boolean isValid(InputStream is) throws IOException {
		byte[] b = PNGReader.readHeader(is);
		if (startsWith(b, PNGHEADER)) {
			return true;
		}
		return false;
	}

	private static boolean startsWith(byte[] array, byte[] prefix) {
		if (array.length < prefix.length) {
			throw new ArrayIndexOutOfBoundsException(
					"Az array tömb hosszabb, mint a prefix tömb! (" + array.length + "; " + prefix.length + ")");
		} else {
			for (int i = 0; i < prefix.length; i++) {
				if (prefix[i] != array[i]) {
					return false;
				}
			}

			return true;
		}
	}

	private static final byte[] PNGHEADER;

	static {
		PNGHEADER = PNGHelper.castIntArrayToByteArray(0x89, 'P', 'N', 'G', 0x0D, 0x0A, 0x1A, 0x0A);
	}

}
