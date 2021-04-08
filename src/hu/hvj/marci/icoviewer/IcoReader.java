package hu.hvj.marci.icoviewer;

import java.io.IOException;
import java.io.InputStream;

public class IcoReader {

	public static Header readHeader(InputStream is) throws IOException {
		byte[] buf = new byte[6];
		is.read(buf);

		Header h = new Header(buf);
		return h;
	}

	public static IconDirEntry readIconDirEntry(InputStream is) throws IOException {
		byte[] buf = new byte[16];
		is.read(buf);

		IconDirEntry ide = new IconDirEntry(buf);
		return ide;
	}

}
