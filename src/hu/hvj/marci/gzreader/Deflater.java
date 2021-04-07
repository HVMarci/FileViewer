package hu.hvj.marci.gzreader;

import java.util.ArrayList;
import java.util.Arrays;

public class Deflater {

	static abstract class Node implements Comparable<Node> {
		int frequency;

		public Node(int frequency) {
			this.frequency = frequency;
		}

		@Override
		public int compareTo(Node o) {
			if (this.frequency == o.frequency) {
				return 0;
			} else if (this.frequency < o.frequency) {
				return -1;
			} else { // this.frequency > o.frequency
				return 1;
			}
		}
	}

	static class LeafNode extends Node {
		int lenght, code, value;

		public LeafNode(int value, int frequency) {
			super(frequency);
			this.value = value;
		}
	}

	static class FrequencyNode extends Node {
		Node[] children;

		public FrequencyNode(int frequency, Node[] children) {
			super(frequency);
			this.children = children;
		}

	}

	/**
	 * 32768 = 0x8000 = 2<sup>15</sup><br>
	 * log<sub>2</sub>32768 = <b>7</b> + 8 <i>(zlib)</i>
	 */
	public static final int LZ77_WINDOW_SIZE = 32768;

	public static final int LZ77_MAX_LENGTH = 258;

	private static byte[][] tmpArrays1 = new byte[LZ77_WINDOW_SIZE + LZ77_MAX_LENGTH + 5][];
	private static byte[][] tmpArrays2 = new byte[LZ77_MAX_LENGTH + 5][];

	static {
		for (int i = 0; i < tmpArrays1.length; i++) {
			tmpArrays1[i] = new byte[i];
		}
		for (int i = 0; i < tmpArrays2.length; i++) {
			tmpArrays2[i] = new byte[i];
		}
	}

	public byte[] deflate(byte[] inputBlock) {
		ArrayList<Byte> als = new ArrayList<Byte>();

		int[] lz77 = deflateStyleLZ77(inputBlock);

		byte[] r = new byte[als.size()];
		for (int i = 0; i < r.length; i++) {
			r[i] = als.get(i);
		}
		return r;
	}

	public static int[] deflateStyleLZ77(byte[] input) {
		System.out.println("Deflater.deflateStyleLZ77()");
		ArrayList<Integer> als = new ArrayList<Integer>();

		int pos;
		for (pos = 0; pos < 3 && pos < input.length; pos++) {
			als.add(new Integer(input[pos] & 0xFF));
		}

		while (pos + 3 < input.length) {
			int len = 3;
			System.arraycopy(input, 0, tmpArrays1[pos + len], 0, pos + len);
			System.arraycopy(input, pos, tmpArrays2[len], 0, len);
			int rk = RabinKarp(tmpArrays1[pos + len], tmpArrays2[len], pos), erk = rk;
			while (rk != -1 && len <= LZ77_MAX_LENGTH + 1 && len + pos < input.length) {
				erk = rk;
				len++;
				int start = pos > LZ77_WINDOW_SIZE ? pos - LZ77_WINDOW_SIZE : 0;
//				System.out.println(pos + ", " + len + ", " + start + ", " + tmpArrays1.length + ", " + (pos + len - start) + ", " + input.length);
				System.arraycopy(input, start, tmpArrays1[pos + len - start], 0, pos + len - start);
				System.arraycopy(input, pos, tmpArrays2[len], 0, len);
				rk = RabinKarp(tmpArrays1[pos + len - start], tmpArrays2[len], pos);
			}

			if (len == 3) {
				als.add(new Integer(input[pos] & 0xFF));
				len = 1;
			} else {
				len--;
				als.add(getLength(len));
				als.add(lengthExtraBits(len));

				int dist = pos - erk;
				als.add(getDist(dist));
				als.add(distExtraBits(dist));
			}
			pos += len;
		}

		als.add(new Integer(256));

		int[] r = new int[als.size()];
		for (int i = 0; i < r.length; i++) {
			r[i] = als.get(i);
		}
		return r;
	}

	public static Node createHuffmanTree(int[] data) {
		System.out.println("Deflater.createHuffmanTree()");
		LeafNode[] literalValues = new LeafNode[286];
		for (int i = 0; i < literalValues.length; i++) {
			literalValues[i] = new LeafNode(i, 0);
		}

		LeafNode[] distanceValues = new LeafNode[30];
		for (int i = 0; i < distanceValues.length; i++) {
			distanceValues[i] = new LeafNode(i, 0);
		}

		for (int i = 0; i < data.length; i++) {
			if (data[i] <= 256) {
				literalValues[data[i]].frequency++;
			} else {
				literalValues[data[i]].frequency++;
				// skip extra bits
				distanceValues[data[i + 2]].frequency++; // distance
				// skip extra bits
				i += 3;
			}
		}

//		System.out.println("Literal/length values:");
//		for (int i = 0; i < literalValues.length; i++) {
//			System.out.printf("%3d" + (literalValues[i].value < 256 ? " (%1$c)" : "") + ": %d%n",
//					literalValues[i].value, literalValues[i].frequency);
//		}
//		System.out.println("Distance values:");
//		for (int i = 0; i < distanceValues.length; i++) {
//			System.out.printf("%2d: %d%n", distanceValues[i].value, distanceValues[i].frequency);
//		}
//
//		Arrays.sort(literalValues);
//		Arrays.sort(distanceValues);
//
//		System.out.println("Literal/length values:");
//		for (int i = 0; i < literalValues.length; i++) {
//			System.out.printf("%3d" + (literalValues[i].value < 256 ? " (%1$c)" : "") + ": %d%n",
//					literalValues[i].value, literalValues[i].frequency);
//		}
//		System.out.println("Distance values:");
//		for (int i = 0; i < distanceValues.length; i++) {
//			System.out.printf("%2d: %d%n", distanceValues[i].value, distanceValues[i].frequency);
//		}

//		ArrayList<Node> literalAls = new ArrayList<>();
//		for (int i = 0; i < literalValues.length; i++) {
//			literalAls.add(literalValues[i]);
//		}
//
//		Comparator<Node> nodeComparator = new Comparator<Node>() {
//			@Override
//			public int compare(Node o1, Node o2) {
//				return o1.compareTo(o2);
//			}
//		};
//
//		while (literalAls.size() > 1) {
//			literalAls.sort(nodeComparator);
//			Node n1 = literalAls.get(0), n2 = literalAls.get(1);
//			literalAls.remove(n1);
//			literalAls.remove(n2);
//			FrequencyNode n = new FrequencyNode(n1.frequency + n2.frequency, new Node[] { n1, n2 });
//			literalAls.add(n);
//		}
//
//		FrequencyNode literalRoot = (FrequencyNode) literalAls.get(0);
//		setLengths(literalRoot);
//
//		return literalRoot;
		return null;
	}

	public static void setLengths(FrequencyNode rootNode) {
		setLengths(rootNode, 0);
	}

	public static void setLengths(FrequencyNode rootNode, int length) {
		Node[] children = rootNode.children;
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof FrequencyNode) {
				setLengths((FrequencyNode) children[i], length + 1);
			} else {
				((LeafNode) children[i]).lenght = length;
			}
		}
	}

	public static LeafNode[] extractLeafs(FrequencyNode rootNode) {
		Node[] children = rootNode.children;

		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof FrequencyNode) {
				extractLeafs((FrequencyNode) children[i]);
			} else {
				((LeafNode) children[i]).lenght = 1;
			}
		}
		return null;
	}

	public static int hash(byte[] b) {
		int h = 0;
		if (b.length > 0) {
			for (int i = 0; i < b.length; i++) {
				h = 31 * h + (b[i] & 0xFF);
			}
		}
		return h;
	}

	public static int RabinKarp(byte[] s, byte[] pattern, int pos) {
		int m = pattern.length;
		int hpattern = hash(pattern);
		byte[] part = new byte[m];
		for (int i = pos - 1; i >= 0; i--) {
			System.arraycopy(s, i, part, 0, m);
			int hs = hash(part);
			if (hs == hpattern) {
				if (Arrays.equals(part, pattern)) {
					return i;
				}
			}
		}
		return -1;
	}

	public static int getLength(int len) {
		if (len <= 10) {
			return len + 254;
		} else if (len == 258) {
			return 285;
		} else {
			int c1 = 11, c2 = 1, c3 = 0, c4 = 265;
			while (len >= c1) {
				c1 += 1 << c2;

				if (c3 == 3) {
					c2++;
					c3 = 0;
				} else {
					c3++;
				}
				c4++;
			}

			return c4 - 1;
		}
	}

	public static int lengthUpperBound(int len) {
		if (len <= 10 || len == 258) {
			return len;
		} else {
			int c1 = 11, c2 = 1, c3 = 0;
			while (len >= c1) {
				c1 += 1 << c2;

				if (c3 == 3) {
					c2++;
					c3 = 0;
				} else {
					c3++;
				}
			}

			return c1 - 1;
		}
	}

	public static int lengthLowerBound(int len) {
		return lengthUpperBound(len) - (1 << lengthExtraBitCount(len)) + 1;
	}

	public static int lengthExtraBitCount(int len) {
		if (len <= 10) {
			return 0;
		} else if (len == 258) {
			return 0;
		} else {
			int c1 = 11, c2 = 1, c3 = 0, c4 = 1;
			while (len >= c1) {
				c1 += 1 << c2;
				c4 = c2;

				if (c3 == 3) {
					c3 = 0;
					c2++;
				} else {
					c3++;
				}
			}

			return c4;
		}
	}

	public static int lengthExtraBits(int len) {
		return len - lengthLowerBound(len);
	}

	public static int getDist(int dist) {
		if (dist <= 4) {
			return dist - 1;
		} else {
			int c1 = 5, c2 = 1, c3 = 0, c4 = 4;
			while (dist >= c1) {
				// 2^c2
				c1 += 1 << c2;
				if (c3 == 1) {
					c2++;
					c3 = 0;
				} else {
					c3++;
				}
				c4++;
			}

			return c4 - 1;
		}
	}

	public static int distExtraBitCount(int dist) {
		if (dist <= 4) {
			return 0;
		} else {
			int c1 = 5, c2 = 1, c3 = 0, c4 = 1;
			while (dist >= c1) {
				c1 += 1 << c2;
				c4 = c2;

				if (c3 == 1) {
					c3 = 0;
					c2++;
				} else {
					c3++;
				}
			}

			return c4;
		}
	}

	public static int distUpperBound(int dist) {
		if (dist <= 4) {
			return dist;
		} else {
			int c1 = 5, c2 = 1, c3 = 0;
			while (dist >= c1) {
				c1 += 1 << c2;

				if (c3 == 1) {
					c2++;
					c3 = 0;
				} else {
					c3++;
				}
			}

			return c1 - 1;
		}
	}

	public static int distLowerBound(int dist) {
		return distUpperBound(dist) - (1 << distExtraBitCount(dist)) + 1;
	}

	public static int distExtraBits(int dist) {
		return dist - distLowerBound(dist);
	}
}
