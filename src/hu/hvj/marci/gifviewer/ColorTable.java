package hu.hvj.marci.gifviewer;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

public class ColorTable {

	public static final ColorTable STATIC_COLOR_TABLE;

	private final Color[] colors;

	public ColorTable(InputStream is, int size) throws IOException {
		int realSize = 3 * (1 << (size + 1));
		this.colors = new Color[realSize / 3];

		byte[] buf = new byte[realSize];
		is.read(buf);

		for (int i = 0, j = 0; i < realSize; i += 3, j++) {
			this.colors[j] = new Color(buf[i] & 0xFF, buf[i + 1] & 0xFF, buf[i + 2] & 0xFF);
		}
	}

	static {
		Color[] colors = new Color[256];
		colors[0] = Color.BLACK;
		colors[1] = Color.WHITE;

		for (int i = 2, j = 1; i < 255; i++, j++) {
			colors[i + 1] = new Color(j, j, j);
		}

		STATIC_COLOR_TABLE = new ColorTable(colors);
	}

	private ColorTable(Color[] colors) {
		this.colors = colors;
	}

	public Color[] getColors() {
		return this.colors;
	}

	public Color getColor(int index) {
		return this.colors[index];
	}

}
