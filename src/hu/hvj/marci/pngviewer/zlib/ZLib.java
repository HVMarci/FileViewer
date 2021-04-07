package hu.hvj.marci.pngviewer.zlib;

import static hu.hvj.marci.pngviewer.PNGHelper.btpi;

import hu.hvj.marci.pngviewer.Logger;
import hu.hvj.marci.pngviewer.PNGHelper;

public class ZLib {

	public static byte getCMF(byte[] b) {
		return b[0];
	}

	public static byte getCompressionMethod(byte[] b) {
		byte cmf = b[0];
		byte cm = (byte) (cmf & 0x0F);

		return cm;
	}

	public static byte getCompressionInfo(byte[] b) {
		byte cmf = b[0];
		byte cinfo = (byte) ((cmf >> 4) & 0x0F);

		return cinfo;
	}

	public static int getLZ77WindowSize(byte[] b) {
		byte cinfo = getCompressionInfo(b);

		int windowSize = (int) Math.pow(2, btpi(cinfo) + 8);

		return windowSize;
	}

	public static byte getFlags(byte[] b) {
		return b[1];
	}

	public static byte getFCHECK(byte[] b) {
		byte flg = b[1];

		byte fcheck = (byte) (flg & 0x1F);

		return fcheck;
	}

	/**
	 * Checks the {@code CMF} and {@code FLG} bytes, with the {@code FCHECK} method.
	 * 
	 * @param b the zlib datastream
	 * @return If the check outputs valid, then {@code true}, else {@code false}.
	 */
	public static boolean checkWithFCHECK(byte[] b) {
		int cmf = btpi(getCMF(b)), flg = btpi(getFlags(b));

		if ((cmf * 256 + flg) % 31 == 0) {
			return true;
		} else {
			return false;
		}
	}

	public static byte getFDICT(byte[] b) {
		byte flg = b[1];

		byte fdict = (byte) ((flg & 0x20) >> 5);

		return fdict;
	}

	public static boolean hasDICTID(byte[] b) {
		byte fdict = getFDICT(b);

		return fdict == 1;
	}

	public static byte getCompressionLevel(byte[] b) {
		byte flg = getFlags(b);

		byte flevel = (byte) ((flg & 0xC0) >> 6);

		return flevel;
	}

	public static int getCompressedDataLength(byte[] b) {
		if (hasDICTID(b)) {
			return b.length - 10;
		} else {
			return b.length - 6;
		}
	}

	/**
	 * Returns {@code true}, when the compressed data's length is more than 0.
	 * 
	 * @param b the zlib datastream
	 */
	public static boolean hasValidLength(byte[] b) {
		int cdl = getCompressedDataLength(b);

		return cdl > 0;
	}

	public static byte[] getCompressedData(byte[] b) {
		int cdl = getCompressedDataLength(b);

		byte[] compressedData;
		if (cdl <= 0) {
			compressedData = new byte[0];
		} else {
			if (hasDICTID(b)) {
				compressedData = PNGHelper.getArrayPart(b, 6, 6 + cdl - 1);
			} else {
				compressedData = PNGHelper.getArrayPart(b, 2, 2 + cdl - 1);
			}
		}

		return compressedData;
	}

	public static int getADLER32(byte[] b) {
		byte[] adler32 = PNGHelper.getArrayPart(b, b.length - 4, b.length - 1);

		return PNGHelper.fourBytesToIntMSBFirst(adler32);
	}

	public static boolean checkADLER32(byte[] decompressedData, int adler32) {
		int newAdler32 = Adler32.checksum(decompressedData);
		return adler32 == newAdler32;
	}

	public static String printInfo(byte[] data) {
		String s = String.format(
				"ZLIB data:%nCMF: 0x%02X%nCM: %d%nCINFO: %d%nLZ77 window size: %d%nFLG: 0x%02X%nFCHECK: %d%nIs CMF and FLG valid: %b%nFDICT: %d%nHas DICTID: %b%nFLEVEL: %d%n"
						+ "Compressed data's length: %d%nHas valid length: %b%nCompressed data's first byte: 0x%02X%nCompressed data's last byte: 0x%02X%n"
						+ "Adler-32: %d",
				getCMF(data), getCompressionMethod(data), getCompressionInfo(data), getLZ77WindowSize(data),
				getFlags(data), getFCHECK(data), checkWithFCHECK(data), getFDICT(data), hasDICTID(data),
				getCompressionLevel(data), getCompressedDataLength(data), hasValidLength(data),
				getCompressedData(data)[0], getCompressedData(data)[getCompressedData(data).length - 1],
				getADLER32(data));

		Logger.message(s);

		return s;
	}

	public static void main(String[] args) {
//		byte[] data = PNGHelper.castIntArrayToByteArray(0x08, 0xD7, 0x63, 0xF8, 0xCF, 0xC0, 0x00, 0x00, 0x03, 0x01,
//				0x01, 0x00);
		// PNG kép, tömörítés nélkül
		byte[] data = PNGHelper.castIntArrayToByteArray(0x78, 0x01, 0x01, 0x11, 0x00, 0xEE, 0xFF, 0x00, 0xCC, 0x00,
				0x00, 0xFF, 0x00, 0xCC, 0x00, 0xFF, 0x00, 0x00, 0xCC, 0xFF, 0xCC, 0xCC, 0xCC, 0xCC, 0x3D, 0x3A, 0x08,
				0x92);

		decompress(data);

		printInfo(data);
	}

	public static byte[] decompress(byte[] zlibStream) {
		byte[] compressedData = getCompressedData(zlibStream);
		byte[] decompressedData = Inflater.decompress(compressedData);
		boolean validAdler32 = checkADLER32(decompressedData, getADLER32(zlibStream));
		for (int i = 0; i < decompressedData.length; i++) {
			Logger.info("ZLib.decompress", String.format("%" + String.valueOf(decompressedData.length).length() + "d: 0x%02X", i, decompressedData[i]));
		}

		if (!validAdler32) {
			System.err.printf("Az ADLER32 hibás! (jelenlegi: %d, helyes: %d)%n", getADLER32(zlibStream),
					Adler32.checksum(decompressedData));
		} else {
			Logger.info("ZLib.decompress", "Helyes ADLER32!");
		}
		printInfo(zlibStream);

		return decompressedData;
	}

}
