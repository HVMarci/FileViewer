package hu.hvj.marci.pngviewer.chunks.ancillary;

import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;

import hu.hvj.marci.pngviewer.ISO88591;
import hu.hvj.marci.pngviewer.chunks.Chunk;

public class TEXT extends Chunk {

	public TEXT(byte[] content, byte[] crc) {
		super(content.length, "tEXt", content, crc);
		if (content.length < 3)
			throw new IllegalArgumentException("A tEXt chunk hossza minimum 3! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		return "Keyword: \"" + this.getKeyword() + "\", Text: \"" + this.getText() + "\"";
	}

	public String getKeyword() {
		int length = 0;

		for (int i = 0; i < this.content.length; i++) {
			if (this.content[i] == 0) {
				length = i;
				break;
			}
		}

		if (length == 0 || length > 79) {
			throw new ArrayIndexOutOfBoundsException("A keyword hossza " + length + "!");
		}

		return ISO88591.toISO88591(getArrayPart(this.content, length - 1));
	}

	public String getText() {
		int kl = this.getKeyword().length();

		byte[] text = getArrayPart(this.content, kl + 1, this.content.length - 1);

		return ISO88591.toISO88591(text);
	}
}
