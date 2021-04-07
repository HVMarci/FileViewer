package hu.hvj.marci.pngviewer;

public class CRC32 {

	/* Table of CRCs of all 8-bit messages. */
	private static int[] crc_table = new int[256];

	/* Flag: has the table been computed? Initially false. */
	private static boolean crc_table_computed = false;

	/* Make the table for a fast CRC. */
	public static void make_crc_table() {
		int c;

		for (int n = 0; n < 256; n++) {
			c = n;
			for (int k = 0; k < 8; k++) {
				if ((c & 1) != 0) {
					c = 0xEDB88320 ^ (c >>> 1);
				} else {
					c = c >>> 1;
				}
			}
			crc_table[n] = c;
		}
		crc_table_computed = true;
	}

	/*
	 * Update a running CRC with the bytes buf[0..len-1]--the CRC should be
	 * initialized to all 1's, and the transmitted value is the 1's complement of
	 * the final running CRC (see the crc() routine below).
	 */

	public static int update_crc(int crc, byte[] buf) {
		int c = crc;
		int n;

		if (!crc_table_computed)
			make_crc_table();
		for (n = 0; n < buf.length; n++) {
			c = crc_table[(c ^ buf[n]) & 0xFF] ^ (c >>> 8);
		}
		return c;
	}

	/* Return the CRC of the bytes buf[0..len-1]. */
	public static int crc(byte[] buf) {
		return update_crc(0xFFFFFFFF, buf) ^ 0xFFFFFFFF;
	}

}
