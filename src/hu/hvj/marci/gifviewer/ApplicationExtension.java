package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ApplicationExtension extends SpecialPurpuseBlock {

	public static final byte[] NETSCAPE_ID = { 'N', 'E', 'T', 'S', 'C', 'A', 'P', 'E' },
			NETSCAPE_AUTH = { '2', '.', '0' };

	public static final byte[] ANIMEXTS_ID = { 'A', 'N', 'I', 'M', 'E', 'X', 'T', 'S' },
			ANIMEXTS_AUTH = { '1', '.', '0' };

	private final byte[] appID, appAuth, data;

	public ApplicationExtension(InputStream is) throws IOException {
		int blockSize = is.read();
		if (blockSize != 11) {
			System.err.println("Invalid block size at ApplicationExtension! " + blockSize);
		}

		appID = new byte[8];
		is.read(appID);

		appAuth = new byte[3];
		is.read(appAuth);

		int subblockSize = is.read();
		ArrayList<Byte> als = new ArrayList<>();
		while (subblockSize != 0) {
			while (subblockSize-- != 0) {
				als.add((byte) is.read());
			}

			subblockSize = is.read();
		}

		this.data = new byte[als.size()];
		for (int i = 0; i < als.size(); i++) {
			this.data[i] = als.get(i);
		}
	}

	public byte[] getAppID() {
		return appID;
	}

	public byte[] getAppAuth() {
		return appAuth;
	}

	public byte[] getData() {
		return data;
	}

}
