package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class Header {

	public static final byte[] GIF_SIGNATURE = { 'G', 'I', 'F' };

	/**
	 * <code>3684193 = ('8' &lt;&lt; 16) | ('7' &lt;&lt; 8) | 'a'</code>
	 */
	public static final int VERSION_87A = 3684193;

	/**
	 * <code>3684705 = ('8' &lt;&lt; 16) | ('9' &lt;&lt; 8) | 'a'</code>
	 */
	public static final int VERSION_89A = 3684705;

	private final int version;

	public Header(byte[] data) {
		if (Helper.equalsFromIndex(data, GIF_SIGNATURE, 0)) {
			System.out.println("Valid signature");
		} else {
			System.err.println("Invalid signature");
		}

		int version = 0;
		version |= data[3] << 16;
		version |= data[4] << 8;
		version |= data[5];
		this.version = version;
	}

	public Header(InputStream is) throws IOException {
		byte[] buf = new byte[3];
		is.read(buf);
		if (Arrays.equals(buf, GIF_SIGNATURE)) {
			System.out.println("Valid signature");
		} else {
			System.err.println("Invalid signature");
		}

		is.read(buf);
		int version = 0;
		version |= buf[0] << 16;
		version |= buf[1] << 8;
		version |= buf[2];
		this.version = version;
	}

	public int getVersion() {
		return version;
	}

	public String getVersionName() {
		StringBuilder sb = new StringBuilder();
		sb.append((char) ((this.version >>> 16) & 0xFF));
		sb.append((char) ((this.version >>> 8) & 0xFF));
		sb.append((char) (this.version & 0xFF));

		return new String(sb);
	}
}
