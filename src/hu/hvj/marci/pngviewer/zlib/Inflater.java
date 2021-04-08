package hu.hvj.marci.pngviewer.zlib;

import static hu.hvj.marci.pngviewer.BitHelper.booleansToBytes;
import static hu.hvj.marci.pngviewer.BitHelper.bytesToBooleans;
import static hu.hvj.marci.pngviewer.BitHelper.rbiba;
import static hu.hvj.marci.pngviewer.PNGHelper.byteAlsToArray;
import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;
import static hu.hvj.marci.pngviewer.PNGHelper.rotateBytes;
import static hu.hvj.marci.pngviewer.PNGHelper.twoBitsToInt;
import static hu.hvj.marci.pngviewer.PNGHelper.twoBytesToIntLSBFirst;

import java.util.ArrayList;

import hu.hvj.marci.pngviewer.Logger;

public class Inflater {

	public static byte[] decompress(byte[] compressedData) {
		ArrayList<Byte> decompressedData = new ArrayList<Byte>();
		boolean[] bits = bytesToBooleans(rotateBytes(compressedData));

		BitAndByteIndex bb = new BitAndByteIndex();
		boolean lastBlock;
		do {
			lastBlock = bits[bits.length - (bb.byteIndex * 8 + bb.bitIndex) - 1];
			bb.leptetes();

			int compressionMethod = twoBitsToInt(bits[bits.length - (bb.byteIndex * 8 + bb.bitIndex + 1) - 1],
					bits[bits.length - (bb.byteIndex * 8 + bb.bitIndex) - 1]);
			bb.leptetes(2);

			if (compressionMethod == 0) {
				bb.nextByte();
				Logger.debug("Inflater.decompress", bb.bitIndex + ", " + bb.byteIndex, 3);
				int len = readLEN(bits, bb.bitIndex, bb.byteIndex);
				bb.leptetes(16);
				Logger.debug("Inflater.decompress",
						String.format("bb.bitIndex: %d%nbb.byteIndex: %d%n", bb.bitIndex, bb.byteIndex), 3);
				int nlen = readNLEN(bits, bb.bitIndex, bb.byteIndex);
				bb.leptetes(16);
				Logger.debug("Inflater.decompress", String.format("LEN: 0x%02X%nNLEN: 0x%02X%n", len, nlen), 2);
				if (checkNLEN(len, nlen)) {
					Logger.debug("Inflater.decompress", String.format("bb.byteIndex = %d%n", bb.byteIndex), 3);
					byte[] data = getArrayPart(compressedData, bb.byteIndex, bb.byteIndex + len - 1);
					for (int i = 0; i < data.length; i++) {
						decompressedData.add(Byte.valueOf(data[i]));
					}
					bb.leptetes(len * 8);
					Logger.debug("Inflater.decompress",
							String.format("bb.bitIndex: %d%nbb.byteIndex: %d%n", bb.bitIndex, bb.byteIndex), 3);
				} else {
					System.err.println("Invalid NLEN!");
					break;
				}
			} else {
				HuffmanTable huffman;
				if (compressionMethod == 2) {
					huffman = new HuffmanTable(rbiba(bits));
				}
			}
		} while (!lastBlock);

		return byteAlsToArray(decompressedData);
	}

	public static int readLEN(boolean[] bits, int bitIndex, int byteIndex) {
		Logger.debug("Inflater.readLEN",
				String.format("byteIndex * 8 + bitIndex = %d%nbyteIndex * 8 + bitIndex + 15 = %d%n",
						byteIndex * 8 + bitIndex, byteIndex * 8 + bitIndex + 15),
				3);
//		boolean[] b = PNGHelper.getArrayPart(bits, byteIndex * 8 + bitIndex, byteIndex * 8 + bitIndex + 15);
//		System.out.printf(
//				"BitHelper.booleansToBytes(PNGHelper.getArrayPart(bits, byteIndex * 8 + bitIndex, byteIndex * 8 + bitIndex + 15))[0] = 0x%02X%n",
//				BitHelper.booleansToBytes(b)[0]);
//
//		for (int i = 0; i < b.length; i++) {
//			if (i % 8 == 0 && i != 0) {
//				System.out.print(" ");
//			}
//			System.out.print(b[i] ? 1 : 0);
//		}
//		System.out.println();

		return twoBytesToIntLSBFirst(rotateBytes(booleansToBytes(getArrayPart(bits,
				bits.length - (byteIndex * 8 + bitIndex + 15) - 1, bits.length - (byteIndex * 8 + bitIndex) - 1))));
	}

	public static int readNLEN(boolean[] bits, int bitIndex, int byteIndex) {
		return readLEN(bits, bitIndex, byteIndex);
	}

	public static boolean checkNLEN(int len, int nlen) {
		short slen = (short) len;
		short snlen = (short) nlen;

		return ~slen == snlen;
	}

//	public static void main(String[] args) {
//		byte[] data = PNGHelper.castIntArrayToByteArray(0xCC, 0xA3, 0xD8, 0xFF, 0x00, 0x00, 0x12);
//		Deflater d = new Deflater(0);
//		d.setInput(data);
//		d.finish();
//
//		ArrayList<Byte> bytesCompressedSoFar = new ArrayList<Byte>();
//
//		while (d.needsInput() == false) {
//			byte[] bytesCompressedBuffer = new byte[100];
//
//			int numberOfBytesDecompressedThisTime = d.deflate(bytesCompressedBuffer);
//
//			for (int b = 0; b < numberOfBytesDecompressedThisTime; b++) {
//				bytesCompressedSoFar.add(bytesCompressedBuffer[b]);
//			}
//		}
//
//		byte[] returnValues = new byte[bytesCompressedSoFar.size()];
//		for (int b = 0; b < returnValues.length; b++) {
//			returnValues[b] = (byte) (bytesCompressedSoFar.get(b));
//		}
//
//		d.end();
//
//		Logger.message(Arrays.toString(returnValues));
//
//	}

}
