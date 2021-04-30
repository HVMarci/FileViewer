package hu.hvj.marci.gzreader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;

import hu.hvj.marci.global.Reader;

public class Inflater {

	public static final int STORED = 0;
	public static final int FIXED_HUFFMAN_CODES = 1;
	public static final int DYNAMIC_HUFFMAN_CODES = 2;
	public static final int RESERVED_COMPRESSED_METHOD = 3;

	private int blockCount;
	private int[] blockTypes;

	public byte[] inflate(Reader is) throws IOException {
		return inflate(is, null);
	}

	public byte[] inflate(Reader is, JLabel txt) throws IOException {
		long start = System.currentTimeMillis();
		ArrayList<Byte> als = new ArrayList<Byte>();
		MyBitSet bals = new MyBitSet();
		boolean isLastBlock;
		this.blockCount = 0;
		this.blockTypes = new int[3];
		byte b;
		do {
			/*
			 * if (blockCount == 139) { System.out.println("139");
			 * System.out.println("bals.size() = " + bals.size()); break; }
			 */
			if (bals.size() < 3) {
				b = is.read();
				bals.add(b);
			}
			isLastBlock = bals.getLast();
			int compressionMethod = bals.getNextXBitIntegerLSBFirst(2);
			String tomoritesiMetodus;
			this.blockTypes[compressionMethod]++;
			switch (compressionMethod) {
			case STORED:
				tomoritesiMetodus = "tárolva";
				break;
			case FIXED_HUFFMAN_CODES:
				tomoritesiMetodus = "fix huffman tábla";
				break;
			case DYNAMIC_HUFFMAN_CODES:
				tomoritesiMetodus = "dinamikus huffman tábla";
				break;
			default:
				tomoritesiMetodus = "definiálatlan (" + compressionMethod + ") HIBA";
				break;
			}
			System.out.printf("Blokk %d megkezdve (%s, tömörítési mód: %s)%n", ++this.blockCount,
					isLastBlock ? "utolsó" : "nem utolsó", tomoritesiMetodus);
			if (txt != null) {
				txt.setText(this.blockCount + ". blokk feldolgozása");
			}
			if (compressionMethod == STORED) {
				bals.nextByte();
				if (bals.size() < 32) {
					byte[] buf = new byte[4];
					is.read(buf);
					bals.add(buf);
					int len = bals.getNextXBitIntegerLSBFirst(16);
					int nlen = bals.getNextXBitIntegerLSBFirst(16);
					System.out.println("  hossz: " + len);
					if (nlen != (~len & 0xFFFF)) {
						System.err.printf("Az NLEN hibás! (LEN: 0x%04X, NLEN: 0x%04X)%n", len, nlen);
						break;
					} else {
						System.out.printf("Az NLEN helyes! (LEN: 0x%04X, NLEN: 0x%04X)%n", len, nlen);
					}
					int minusz = (bals.size() / 8) <= len ? bals.size() / 8 : len;
					len -= minusz;
					while (minusz > 0) {
						als.add(bals.getNextByte());
						minusz--;
					}
					byte[] sbuf = new byte[len];
					is.read(sbuf);
					for (int i = 0; i < len; i++) {
						als.add(sbuf[i]);
					}
				}
			} else {
				HuffmanCode huffman;
				if (compressionMethod == DYNAMIC_HUFFMAN_CODES) {
					huffman = new HuffmanCode(bals, is);
				} else {
					huffman = HuffmanCode.STATIC_HUFFMAN_TABLE;
				}

				Arrays.sort(huffman.literal);
				Arrays.sort(huffman.dist);

				int[] litLengths = HuffmanCode.getLengthStartArray(huffman.literal);
				int[] distLengths = HuffmanCode.getLengthStartArray(huffman.dist);
				int[] litLengthsEnd = HuffmanCode.getLengthEndArray(huffman.literal);
				int[] distLengthsEnd = HuffmanCode.getLengthEndArray(huffman.dist);

				while (true) {
					/*
					 * if (als.size() >= 4688342) { System.out.println("6E"); }
					 */
					int val = 0, len = 0;
					while (HuffmanCode.needBits(val, len, huffman.literal, litLengths[len], litLengthsEnd[len])) {
						if (bals.size() < 1) {
							b = is.read();
							bals.add(b);
						}
						val <<= 1;
						val |= bals.getLastBit();
						len++;
					}
					int decodedVal = HuffmanCode.getValue(val, len, huffman.literal, litLengths[len],
							litLengthsEnd[len]);
					if (decodedVal < 256) {
						als.add((byte) decodedVal);
					} else if (decodedVal >= 257 && decodedVal <= 285) {
						int lenExtraBitCount = lengthExtraBits(decodedVal);
						if (bals.size() < lenExtraBitCount) {
							byte[] bb = new byte[(int) Math.ceil(lenExtraBitCount / 8.)];
							is.read(bb);
							bals.add(bb);
						}
						int lenExtraBits = bals.getNextXBitIntegerLSBFirst(lenExtraBitCount);
						int keszlen = lengthValue(decodedVal, lenExtraBits);

						int dval = 0, dlen = 0;
						while (HuffmanCode.needBits(dval, dlen, huffman.dist, distLengths[dlen],
								distLengthsEnd[dlen])) {
							if (bals.size() < 1) {
								b = is.read();
								bals.add(b);
							}
							dval <<= 1;
							dval |= bals.getLastBit();
							dlen++;
						}
						int decodedDist = HuffmanCode.getValue(dval, dlen, huffman.dist, distLengths[dlen],
								distLengthsEnd[dlen]);
						int distExtraBitCount = distExtraBits(decodedDist);
						if (distExtraBitCount == -1) {
							System.err.printf("Hiba! decodedDist = %d%n", decodedDist);
						}
						if (bals.size() < distExtraBitCount) {
							byte[] bb = new byte[(int) Math.ceil(distExtraBitCount / 8.)];
							is.read(bb);
							bals.add(bb);
						}
						int distExtraBits = bals.getNextXBitIntegerLSBFirst(distExtraBitCount);
						int dist = distValue(decodedDist, distExtraBits);

						int startIndex = als.size() - dist;
						int index = startIndex;
						for (int i = 0; i < keszlen; i++) {
							byte szam = als.get(index);
							als.add(szam);
							if (++index >= als.size()) {
								index = startIndex;
							}
						}
					} else if (decodedVal == 256) {
						break;
					}
				}
			}
		} while (!isLastBlock);
		System.out.printf("%f másodperc alatt kész!%n", (System.currentTimeMillis() - start) / 1000.);
		
		byte[] output = new byte[als.size()];
		for (int i = 0; i < als.size(); i++) {
			output[i] = als.get(i);
		}
		return output;
	}

	public static int lengthExtraBits(int length) {
		if (length >= 257 && length <= 264) {
			return 0;
		} else if (length >= 265 && length <= 268) {
			return 1;
		} else if (length >= 269 && length <= 272) {
			return 2;
		} else if (length >= 273 && length <= 276) {
			return 3;
		} else if (length >= 277 && length <= 280) {
			return 4;
		} else if (length >= 281 && length <= 284) {
			return 5;
		} else if (length == 285) {
			return 0;
		} else {
			return -1;
		}
	}

	public static int lengthValue(int length, int extraBits) {
		if (length >= 257 && length <= 264) {
			return length - 254;
		} else if (length >= 265 && length <= 268) {
			int lower = (length - 265) * 2 + 11;
			return lower + extraBits;
		} else if (length >= 269 && length <= 272) {
			int lower = (length - 269) * 4 + 19;
			return lower + extraBits;
		} else if (length >= 273 && length <= 276) {
			int lower = (length - 273) * 8 + 35;
			return lower + extraBits;
		} else if (length >= 277 && length <= 280) {
			int lower = (length - 277) * 16 + 67;
			return lower + extraBits;
		} else if (length >= 281 && length <= 284) {
			int lower = (length - 281) * 32 + 131;
			return lower + extraBits;
		} else if (length == 285) {
			return 258;
		} else {
			return -1;
		}
	}

	public static int distExtraBits(int dist) {
		if (dist < 4) {
			return 0;
		} else {
			return (dist - 2) / 2;
		}
	}

	public static int distValue(int dist, int extraBits) {
		return distLowerBound(dist) + extraBits;
	}

	public static int distLowerBound(int dist) {
		if (dist < 4) {
			return dist + 1;
		} else if (dist == 4) {
			return 5;
		} else {
			return distUpperBound(dist - 1) + 1;
		}
	}

	public static int distUpperBound(int dist) {
		if (dist < 4) {
			return dist + 1;
		} else if (dist == 4) {
			return 6;
		} else {
			return (1 << distExtraBits(dist)) + distUpperBound(dist - 1);
		}
	}

	public int getBlockCount() {
		return this.blockCount;
	}

	public int[] getBlockTypes() {
		return this.blockTypes;
	}

	public int getBlockTypeCount(int type) {
		return this.blockTypes[type];
	}

}
