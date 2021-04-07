package hu.hvj.marci.pngviewer;

import java.nio.charset.Charset;

public class ISO88591 {

	public static final Charset iso88591charset = Charset.forName("ISO-8859-1");

	public static String toISO88591(byte[] b) {
		return new String(b, iso88591charset);
	}

}
