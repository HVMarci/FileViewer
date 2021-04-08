package hu.hvj.marci.pngviewer.chunks.critical;

import static hu.hvj.marci.pngviewer.PNGHelper.btpi;
import static hu.hvj.marci.pngviewer.PNGViewer.LS;

import hu.hvj.marci.pngviewer.PNGHelper;
import hu.hvj.marci.pngviewer.chunks.Chunk;

public class IHDR extends Chunk {

	private int width, height, bitDepth, colorType, compressionMethod, filterMethod, interlaceMethod;
	
	public IHDR(byte[] content, byte[] crc) {
		super(13, "IHDR", content, crc);
		if (content.length != 13)
			throw new IllegalArgumentException("Az IHDR chunk hossza csak 13 lehet, ez pedig: " + content.length);
		
		this.width = PNGHelper.fourBytesToIntMSBFirst(PNGHelper.getArrayPart(this.content, 3));
		this.height = PNGHelper.fourBytesToIntMSBFirst(PNGHelper.getArrayPart(this.content, 4, 7));
		this.bitDepth = btpi(this.content[8]);
		this.colorType = btpi(this.content[9]);
		this.compressionMethod = btpi(this.content[10]);
		this.filterMethod = btpi(this.content[11]);
		this.interlaceMethod = btpi(this.content[12]);
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getBitDepth() {
		return this.bitDepth;
	}

	public int getColorType() {
		return this.colorType;
	}

	public int getCompressionMethod() {
		return this.compressionMethod;
	}

	public int getFilterMethod() {
		return this.filterMethod;
	}

	public int getInterlaceMethod() {
		return this.interlaceMethod;
	}

	@Override
	public String toString() {
		return "Width: " + this.getWidth() + LS + "Height: " + this.getHeight() + LS + "Bit depth: "
				+ this.getBitDepth() + LS + "Color type: " + this.getColorType() + LS + "Compression method: "
				+ this.getCompressionMethod() + LS + "Filter method: " + this.getFilterMethod() + LS
				+ "Interlace method: " + this.getInterlaceMethod();
	}

	@Override
	public String getInfo() {
		return this.toString().replace(LS, "; ");
	}

	public String getColorTypeAsString(boolean hasNumber) {
		String text;

		switch (this.getColorType()) {
		case 0:
			text = "grayscale";
			break;
		case 2:
			text = "RGB";
			break;
		case 3:
			text = "paletted";
			break;
		case 4:
			text = "grayscale + alpha";
			break;
		case 6:
			text = "RGB + alpha";
			break;
		default:
			text = "unrecognized color mode";
			break;
		}

		if (hasNumber) {
			text += " (" + this.getColorType() + ")";
		}

		return text;
	}

	public String getInterlaceMethodAsString() {
		switch (this.getInterlaceMethod()) {
		case 0:
			return "no interlace";
		case 1:
			return "Adam7 interlace";
		default:
			return "unknown interlace";
		}
	}

	public int getBitsPerPixel() {
		switch (this.getColorType()) {
		case 0:
		case 3:
			return this.getBitDepth();
		case 2:
			return this.getBitDepth() * 3;
		case 4:
			return this.getBitDepth() * 2;
		case 6:
			return this.getBitDepth() * 4;
		default:
			return 0;
		}
	}
}
