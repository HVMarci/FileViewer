package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class CommentExtension extends SpecialPurpuseBlock {

	private final byte[] data;

	public CommentExtension(InputStream is) throws IOException {
		ArrayList<Byte> als = new ArrayList<>();
		int size = is.read();
		while (size != 0) {
			while (size-- != 0) {
				als.add((byte) is.read());
			}

			size = is.read();
		}

		this.data = new byte[als.size()];
		for (int i = 0; i < als.size(); i++) {
			this.data[i] = als.get(i);
		}
	}

	public byte[] getData() {
		return data;
	}

}
