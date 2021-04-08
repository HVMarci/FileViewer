package hu.hvj.marci.pngviewer.chunks.critical;

import static hu.hvj.marci.pngviewer.PNGHelper.btpi;

import java.awt.Color;

import hu.hvj.marci.pngviewer.chunks.Chunk;

public class PLTE extends Chunk {

	private final Color[] palette;
	private byte[] reds, greens, blues;

	public PLTE(byte[] content, byte[] crc) {
		super(content.length, "PLTE", content, crc);
		if (content.length % 3 != 0)
			throw new IllegalArgumentException("A hossz nem osztható 3-mal! (A hossz: " + content.length + ")");

		this.palette = new Color[this.length / 3];

		for (int i = 0; i < this.palette.length; i++) {
			this.palette[i] = new Color(btpi(this.content[i * 3]), btpi(this.content[i * 3 + 1]),
					btpi(this.content[i * 3 + 2]));
		}
	}

	@Override
	public String getInfo() {
		return "Palette, " + (this.length / 3) + " entries";
	}

	public Color[] getPalette() {
		return this.palette;
	}

	public Color getColor(int index) {
		return this.palette[index];
	}

	public byte[] getReds() {
		if (this.reds == null) {
			byte[] reds = new byte[this.palette.length];

			for (int i = 0; i < this.palette.length; i++) {
				reds[i] = (byte) this.palette[i].getRed();
			}

			this.reds = reds;
		}

		return this.reds;
	}

	public byte[] getGreens() {
		if (this.greens == null) {
			byte[] greens = new byte[this.palette.length];

			for (int i = 0; i < this.palette.length; i++) {
				greens[i] = (byte) this.palette[i].getGreen();
			}

			this.greens = greens;
		}

		return this.greens;
	}

	public byte[] getBlues() {
		if (this.blues == null) {
			byte[] blues = new byte[this.palette.length];

			for (int i = 0; i < this.palette.length; i++) {
				blues[i] = (byte) this.palette[i].getBlue();
			}

			this.blues = blues;
		}

		return this.blues;
	}

}
