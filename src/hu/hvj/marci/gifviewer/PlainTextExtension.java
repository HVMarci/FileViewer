package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class PlainTextExtension extends GraphicRenderingBlock {

	private final int textGridLeftPosition, textGridTopPosition, textGridWidth, textGridHeight;
	private final int characterCellWidth, characterCellHeight, textForegroundColorIndex, textBackgroundColorIndex;
	private final String data;

	public PlainTextExtension(InputStream is) throws IOException {
		int blockSize = is.read();
		if (blockSize != 12) {
			System.err.println("Invalid block size at PlainTextExtension! " + blockSize);
		}

		textGridLeftPosition = is.read() | is.read() << 8;
		textGridTopPosition = is.read() | is.read() << 8;
		textGridWidth = is.read() | is.read() << 8;
		textGridHeight = is.read() | is.read() << 8;
		characterCellWidth = is.read();
		characterCellHeight = is.read();
		textForegroundColorIndex = is.read();
		textBackgroundColorIndex = is.read();

		String data = "";

		int subblockSize = is.read();
		while (subblockSize != 0) {
			byte[] buf = new byte[subblockSize];
			is.read(buf);

			data += new String(buf, Charset.forName("ASCII"));

			subblockSize = is.read();
		}

		this.data = data;
	}

	public int getTextGridLeftPosition() {
		return textGridLeftPosition;
	}

	public int getTextGridTopPosition() {
		return textGridTopPosition;
	}

	public int getTextGridWidth() {
		return textGridWidth;
	}

	public int getTextGridHeight() {
		return textGridHeight;
	}

	public int getCharacterCellWidth() {
		return characterCellWidth;
	}

	public int getCharacterCellHeight() {
		return characterCellHeight;
	}

	public int getTextForegroundColorIndex() {
		return textForegroundColorIndex;
	}

	public int getTextBackgroundColorIndex() {
		return textBackgroundColorIndex;
	}

	public String getData() {
		return data;
	}

}
