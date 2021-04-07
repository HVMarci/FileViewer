package hu.hvj.marci.pngviewer.chunks.ancillary;

import hu.hvj.marci.pngviewer.chunks.Chunk;
import hu.hvj.marci.pngviewer.chunks.critical.PLTE;
import hu.hvj.marci.pngviewer.exceptions.InvalidChunkLengthException;
import hu.hvj.marci.pngviewer.exceptions.MissingPaletteException;

import static hu.hvj.marci.pngviewer.PNGHelper.twoBytesToIntMSBFirst;

import java.awt.Color;

import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;

public class BKGD extends Chunk {

	private PLTE palette;
	private Color color;

	public BKGD(byte[] content, byte[] crc) {
		super(content.length, "bKGD", content, crc);
		if (content.length != 1 && content.length != 2 && content.length != 6)
			throw new IllegalArgumentException("A bKGD chunk hossza csak 1, 2 vagy 6 lehet! (" + content.length + ")");
	}

	@Override
	public String getInfo() {
		String ri = "Background color: ";

		switch (this.getType()) {
		case 1:
			if (this.palette == null)
				throw new MissingPaletteException();
			ri += "palette index: " + this.content[0];
			break;
		case 2:
			ri += "gray: " + twoBytesToIntMSBFirst(this.content);
			break;
		case 6:
			ri += "RGB(" + twoBytesToIntMSBFirst(getArrayPart(this.content, 1)) + "; "
					+ twoBytesToIntMSBFirst(getArrayPart(this.content, 2, 3)) + "; "
					+ twoBytesToIntMSBFirst(getArrayPart(this.content, 4, 5)) + ")";
			break;
		default:
			return "ERROR";
		}

		return ri;
	}

	public Color getColor(int bitDepth) {
		if (this.color == null) {
			switch (this.getType()) {
			case 1:
				if (this.palette == null)
					throw new MissingPaletteException();
				this.color = this.palette.getColor(this.content[0]);
				break;
			case 2:
				float oszto = (float) (Math.pow(2, bitDepth) - 1);
				float val = twoBytesToIntMSBFirst(this.content) / oszto;
				this.color = new Color(val, val, val);
				break;
			case 6:
				float r = twoBytesToIntMSBFirst(getArrayPart(this.content, 1)) / 65535.0f;
				float g = twoBytesToIntMSBFirst(getArrayPart(this.content, 2, 3)) / 65535.0f;
				float b = twoBytesToIntMSBFirst(getArrayPart(this.content, 4, 5)) / 65535.0f;
				this.color = new Color(r, g, b);
				break;
			default:
				throw new InvalidChunkLengthException("bKGD", this.getType(), "1, 2, 6");
			}
		}

		return this.color;
	}

	public int getType() {
		return this.content.length;
	}

	public void setPLTE(PLTE plte) {
		this.palette = plte;
	}

}
