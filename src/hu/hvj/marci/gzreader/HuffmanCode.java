package hu.hvj.marci.gzreader;

import java.io.IOException;
import java.util.Arrays;

import hu.hvj.marci.global.Reader;

public class HuffmanCode {
	static class HuffmanNode implements Comparable<HuffmanNode> {
		int len, code, index;

		public HuffmanNode(int len, int index) {
			this.len = len;
			this.index = index;
		}

		@Override
		public String toString() {
//			return String.format("Length: %d, Code: %s (%d)", this.len, Helper.decimalToBinary(this.code, this.len), this.code);
			return String.format("Index: %d, Length: %d, Code: %s (%d)", this.index, this.len,
					Helper.decimalToBinary(this.code, this.len), this.code);
		}

		@Override
		public int compareTo(HuffmanNode o) {
			if (this.len < o.len) {
				return -1;
			} else if (this.len > o.len) {
				return 1;
			} else { // this.len == o.len
				if (this.code < o.code) {
					return -1;
				} else if (this.code > o.code) {
					return 1;
				} else { // this.code == o.code
					return 0;
				}
			}
		}
	}

	public static final int[] HCLEN_INDEXES = { 16, 17, 18, 0, 8, 7, 9, 6, 10, 5, 11, 4, 12, 3, 13, 2, 14, 1, 15 };
	public static final int STATIC_HLIT = 288, STATIC_HDIST = 32;
	public static final int STATIC_FIRST_LIT_SECTION = 48, STATIC_SECOND_LIT_SECTION = 400,
			STATIC_THIRD_LIT_SECTION = 0, STATIC_FOURTH_LIT_SECTION = 192;

	private final int hlit, hdist, hclen;
	public final HuffmanNode[] literal, dist;

	public static final HuffmanCode STATIC_HUFFMAN_TABLE = new HuffmanCode();

	public HuffmanCode(MyBitSet als, Reader is) throws IOException {
		System.out.println("Huffman tábla készítése megkezdve");
		if (als.size() < 14) {
			byte[] bb = new byte[2];
			is.read(bb);
			als.add(bb);
		}
		this.hlit = als.getNextXBitIntegerLSBFirst(5) + 257;
		this.hdist = als.getNextXBitIntegerLSBFirst(5) + 1;
		this.hclen = als.getNextXBitIntegerLSBFirst(4) + 4;

		if (als.size() < 19) {
			byte[] bb = new byte[8];
			is.read(bb);
			als.add(bb);
		}

		System.out.println("  Tábla a táblákhoz megkezdve");
		HuffmanNode[] forHuffmanTables = new HuffmanNode[19];
		for (int i = 0; i < hclen; i++) {
			int a = als.getNextXBitIntegerLSBFirst(3);
			forHuffmanTables[HCLEN_INDEXES[i]] = new HuffmanNode(a, HCLEN_INDEXES[i]);
		}
		for (int i = hclen; i < forHuffmanTables.length; i++) {
			forHuffmanTables[HCLEN_INDEXES[i]] = new HuffmanNode(0, HCLEN_INDEXES[i]);
		}

		buildHuffmanTree(forHuffmanTables, 7);
		System.out.println("  Tábla a táblákhoz kész");
		System.out.println("  Tényleges/hossz tábla megkezdve");
		System.out.println("    minBits számolása megkezdve");
		int minBits = Integer.MAX_VALUE;
		for (int i = 0; i < forHuffmanTables.length; i++) {
			if (forHuffmanTables[i].len < minBits && forHuffmanTables[i].len != 0) {
				minBits = forHuffmanTables[i].len;
			}
		}
		System.out.println("    minBits számolása kész");

		Arrays.sort(forHuffmanTables);
		int[] fhtLengths = getLengthStartArray(forHuffmanTables);
		int[] fhtLengthsEnd = getLengthEndArray(forHuffmanTables);

		System.out.println("    tábla dekódolása megkezdve");
		this.literal = new HuffmanNode[hlit];
		decodeHuffmanTree(literal, forHuffmanTables, fhtLengths, fhtLengthsEnd, als, is, minBits);
		System.out.println("    tábla dekódolása kész");

		System.out.println("    tábla építése megkezdve");
		buildHuffmanTree(literal, 15);
		System.out.println("    tábla építése kész");
		System.out.println("  Tényleges/hossz tábla kész");

		this.dist = new HuffmanNode[hdist];
		decodeHuffmanTree(dist, forHuffmanTables, fhtLengths, fhtLengthsEnd, als, is, minBits);

		buildHuffmanTree(dist, 15);
		System.out.println("  Távolság tábla kész");
		System.out.println("Huffman tábla kész");
	}

	/**
	 * Statikus Huffman tábla
	 */
	private HuffmanCode() {
		this.hlit = STATIC_HLIT;
		this.hdist = STATIC_HDIST;
		this.hclen = 0;

		this.literal = new HuffmanNode[this.hlit];
		for (int i = 0; i <= 143; i++) {
			literal[i] = new HuffmanNode(8, i);
			literal[i].code = STATIC_FIRST_LIT_SECTION + i;
		}
		for (int i = 144; i <= 255; i++) {
			literal[i] = new HuffmanNode(9, i);
			literal[i].code = STATIC_SECOND_LIT_SECTION + i - 144;
		}
		for (int i = 256; i <= 279; i++) {
			literal[i] = new HuffmanNode(7, i);
			literal[i].code = STATIC_THIRD_LIT_SECTION + i - 256;
		}
		for (int i = 280; i <= 287; i++) {
			literal[i] = new HuffmanNode(8, i);
			literal[i].code = STATIC_FOURTH_LIT_SECTION + i - 280;
		}

		this.dist = new HuffmanNode[this.hdist];
		for (int i = 0; i < dist.length; i++) {
			dist[i] = new HuffmanNode(5, i);
			dist[i].code = i;
		}
		
		Arrays.sort(this.literal);
		Arrays.sort(this.dist);
	}

	/**
	 * @param table    Rendezve! Első szempont: len, Második szempont: code
	 * @param lenStart A len hosszúak kezdőpontja (inclusive)
	 * @param lenEnd   A len hosszúak végpontja (exclusive)
	 */
	public static boolean needBits(int code, int len, HuffmanNode[] table, int lenStart, int lenEnd) {
		// bináris keresés

// 		l = 0, r = table.len
// 		while l < r - 1:
// 		  m = (l + r) / 2
// 		  if table[m].code <= code:
// 		    l = m
// 		  else:
// 		    r = m
// 		return table[l].code == code
// 		; kb így, bár lehet, hogy l = len hosszú kezdete, r = len hosszú vége + 1 kéne
// 		; legyen (kb. 3 lépéssel kevesebb)

		if (len == 0 || lenStart == -1) {
			return true;
		}

		int l = lenStart, r = lenEnd;
		while (l < r - 1) {
			int m = (l + r) / 2;
			if (table[m].code <= code) {
				l = m;
			} else {
				r = m;
			}
		}

		return table[l].code != code;
	}

	/*
	 * public static boolean needBits(int code, int len, HuffmanNode[] table) { //
	 * régi, lassú megoldás, rendezetlen tömbökre for( HuffmanNode h:table) { if
	 * (h.code == code && h.len == len && h.len != 0) { return false; }
	 * }return*true; }
	 */

	/**
	 * 
	 * @param tree Rendezetlen!!
	 */
	public static void buildHuffmanTree(HuffmanNode[] tree, int maxCodeLength) {
		int[] bl_count = new int[maxCodeLength + 1];
		for (int i = 0; i < tree.length; i++) {
			bl_count[tree[i].len]++;
		}

		bl_count[0] = 0;
		int max_bits = 0;
		for (int i = bl_count.length - 1; i >= 0; i--) {
			if (bl_count[i] > 0) {
				max_bits = i;
				break;
			}
		}

		int[] next_code = new int[max_bits + 1];
		int code = 0;
		for (int bits = 1; bits <= max_bits; bits++) {
			code = (code + bl_count[bits - 1]) << 1;
			next_code[bits] = code;
		}

		for (int i = 0; i < tree.length; i++) {
			int len = tree[i].len;
			if (len != 0) {
				tree[i].code = next_code[len];
				next_code[len]++;
			}
		}
	}

	/**
	 * @param forHuffmanTables Rendezve! Első szempont: len, Második szempont: code
	 */
	public static void decodeHuffmanTree(HuffmanNode[] tree, HuffmanNode[] forHuffmanTables, int[] lenStarts,
			int[] lenEnds, MyBitSet als, Reader is, int minBits) throws IOException {
		byte b = 0;
		for (int i = 0; i < tree.length; i++) {
			if (als.size() < minBits) {
				b = is.read();
				als.add(b);
			}
			int val = als.getNextXBitIntegerMSBFirst(minBits), len = minBits;
			while (needBits(val, len, forHuffmanTables, lenStarts[len], lenEnds[len])) {
				if (als.size() < 1) {
					b = is.read();
					als.add(b);
				}
				val <<= 1;
				val |= als.getLastBit();
				len++;
			}
			int eredmeny = getValue(val, len, forHuffmanTables, lenStarts[len], lenEnds[len]);
			if (eredmeny >= 0 && eredmeny <= 15) {
				tree[i] = new HuffmanNode(eredmeny, i);
			} else if (eredmeny == 16) {
				int elozo;
				if (i == 0) {
					elozo = 0;
					System.err.println("HIBA! eredmeny = 16, i = 0");
				} else {
					elozo = tree[i - 1].len;
				}
				if (als.size() < 2) {
					b = is.read();
					als.add(b);
				}
				int repeat = als.getNextXBitIntegerLSBFirst(2) + 3;
				for (int j = 0; j < repeat; j++, i++) {
					tree[i] = new HuffmanNode(elozo, i);
				}
				i--;
			} else if (eredmeny == 17) {
				if (als.size() < 3) {
					b = is.read();
					als.add(b);
				}
				int repeat = als.getNextXBitIntegerLSBFirst(3) + 3;
				for (int j = 0; j < repeat; j++, i++) {
					tree[i] = new HuffmanNode(0, i);
				}
				i--;
			} else if (eredmeny == 18) {
				if (als.size() < 7) {
					b = is.read();
					als.add(b);
				}
				int repeat = als.getNextXBitIntegerLSBFirst(7) + 11;
				for (int j = 0; j < repeat; j++, i++) {
					tree[i] = new HuffmanNode(0, i);
				}
				i--;
			} else {
				System.err.println("MI A FENE?! eredmeny = " + eredmeny);
			}
		}
	}

	public static int getValue(int code, int len, HuffmanNode[] table, int lenStart, int lenEnd) {
		int l = lenStart, r = lenEnd;
		while (l < r - 1) {
			int m = (l + r) / 2;
			if (code >= table[m].code) {
				l = m;
			} else {
				r = m;
			}
		}
		return table[l].index;
		// Régi vacak, rendezetlen tömbökre
//		for (int i = 0; i < table.length; i++) {
//			HuffmanNode h = table[i];
//			if (h.code == code && h.len == len) {
//				return h.index;
//			}
//		}
//		return -1;
	}

	public static int[] getLengthStartArray(HuffmanNode[] n) {
		int[] lengths = new int[n[n.length - 1].len + 1]; // utolsó (leghosszabb) elem hossza + 1 (0..len)
		Arrays.fill(lengths, -1);

		// TODO bináris keresés
		for (int i = 0; i < n.length; i++) {
			if (lengths[n[i].len] == -1) {
				lengths[n[i].len] = i;
			}
		}

		return lengths;
	}

	public static int[] getLengthEndArray(HuffmanNode[] n) {
		int[] lengths = new int[n[n.length - 1].len + 1];
		Arrays.fill(lengths, -2);

		// TODO bináris keresés
		for (int i = 0; i < n.length; i++) {
			if (lengths[n[i].len] < i) {
				lengths[n[i].len] = i;
			}
		}

		for (int i = 0; i < lengths.length; i++) {
			lengths[i]++;
		}

		return lengths;
	}
}
