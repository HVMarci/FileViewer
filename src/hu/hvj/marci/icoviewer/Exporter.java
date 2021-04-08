package hu.hvj.marci.icoviewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Exporter {

	public static final int MAX_BUFFER_SIZE = 32768;
	public static final byte[] PNG_SIGNATURE = { (byte) 137, 80, 78, 71, 13, 10, 26, 10 };

	public static void export(File outputFile, IconDirEntry ide, InputStream is) throws IOException {
		boolean isBMP = false;
		if (ide.getFormat() == IconDirEntry.BMP) {
			isBMP = true;
		}

		outputFile.createNewFile();

		FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
		long remainingBytes = ide.getImageSize();

		is.skip(ide.getImageOffset());

		if (isBMP) {
			byte[] header = new byte[4];
			is.read(header);
			// BM
			fileOutputStream.write(new byte[] { 0x42, 0x4D });
			// Image size + BMP header size (14)
			fileOutputStream.write(Helper.intToByteArrayLSBFirst((int) ide.getImageSize() + 14));
			// Reserved bytes
			fileOutputStream.write(new byte[] { 0, 0, 0, 0 });
			// DIB header size + BMP header size (14)
			int bmpHeaderSize = Helper.fourBytesToIntLSBFirst(header[0], header[1], header[2], header[3]);
			int offset = bmpHeaderSize + 14;
			// Image data offset
			fileOutputStream.write(Helper.intToByteArrayLSBFirst(offset));
			fileOutputStream.write(header);
//			if (!keepMasks) {
				// Image width
				is.read(header);
				fileOutputStream.write(header);
				// Image height
				is.skip(4);
				fileOutputStream.write(Helper.intToByteArrayLSBFirst(ide.getHeight()));
				remainingBytes -= 8;
//			}
			remainingBytes -= 4;
		}

		while (remainingBytes > 0) {
			byte[] buf;

			if (remainingBytes > MAX_BUFFER_SIZE) {
				buf = new byte[MAX_BUFFER_SIZE];
				remainingBytes -= MAX_BUFFER_SIZE;
			} else {
				buf = new byte[(int) remainingBytes];
				remainingBytes = 0;
			}
			is.read(buf);
			fileOutputStream.write(buf);
		}

		fileOutputStream.close();
	}

}
