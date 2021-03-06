package hu.hvj.marci.pngviewer;

import static hu.hvj.marci.pngviewer.PNGHelper.btpi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import hu.hvj.marci.global.NormalBitSet;
import hu.hvj.marci.pngviewer.chunks.Chunk;
import hu.hvj.marci.pngviewer.chunks.ancillary.BKGD;
import hu.hvj.marci.pngviewer.chunks.ancillary.HIST;
import hu.hvj.marci.pngviewer.chunks.critical.IDAT;
import hu.hvj.marci.pngviewer.chunks.critical.IHDR;
import hu.hvj.marci.pngviewer.chunks.critical.PLTE;
import hu.hvj.marci.pngviewer.exceptions.MissingPaletteException;
import hu.hvj.marci.pngviewer.zlib.ZLib;

public class Image extends Component {

	/**
	 * Random generált UID
	 */
	private static final long serialVersionUID = -2903175179212477224L;

	private static final int[] starting_row = { 0, 0, 4, 0, 2, 0, 1 };
	private static final int[] starting_col = { 0, 4, 0, 2, 0, 1, 0 };
	private static final int[] row_increment = { 8, 8, 8, 4, 4, 2, 2 };
	private static final int[] col_increment = { 8, 8, 4, 4, 2, 2, 1 };

	private Chunk[] chunks;
	private IHDR ihdr;
	private IDAT[] idats;
	private PLTE plte;
	private BKGD bkgd;
	private HIST hist;
	private byte[] decompressedData;
	private double scale;
	private boolean unfilter = true;
	private int[] filterMethods = new int[5];

	public Image(Chunk[] chunks) {
		this.chunks = chunks;

		ArrayList<IDAT> idats = new ArrayList<>();

		for (int i = 0; i < chunks.length; i++) {
			if (chunks[i] instanceof IHDR) {
				this.ihdr = (IHDR) chunks[i];
			} else if (chunks[i] instanceof IDAT) {
				idats.add((IDAT) chunks[i]);
			} else if (chunks[i] instanceof PLTE) {
				this.plte = (PLTE) chunks[i];
			} else if (chunks[i] instanceof BKGD) {
				this.bkgd = (BKGD) chunks[i];
			} else if (chunks[i] instanceof HIST) {
				this.hist = (HIST) chunks[i];
			}
		}

		if (this.ihdr == null) {
			throw new IllegalArgumentException("Nincs IHDR chunk!");
		}

		if (!PNGHelper.isValidColorAndBitDepth(new int[] { this.ihdr.getColorType(), this.ihdr.getBitDepth() })) {
			throw new IllegalArgumentException("Nem létező Color Type és Bit Depth kombináció! ("
					+ this.ihdr.getColorType() + ", " + this.ihdr.getBitDepth() + ")");
		}

		if (idats.size() == 0) {
			throw new IllegalArgumentException("Nincs IDAT chunk!");
		} else {
			this.idats = PNGHelper.idatAlsToArray(idats);
		}

		this.decompressedData = this.decompress(this.combineIDATChunks());

		if (this.hasBKGD() && this.ihdr.getColorType() == 3) {
			if (!this.hasPLTE()) {
				throw new MissingPaletteException();
			} else {
				this.bkgd.setPLTE(this.plte);
			}
		}
	}

	public Chunk[] getChunks() {
		return chunks;
	}

	public IHDR getIHDR() {
		return this.ihdr;
	}

	public IDAT[] getIDATs() {
		return this.idats;
	}

	public PLTE getPLTE() {
		return this.plte;
	}

	public boolean hasBKGD() {
		return this.bkgd != null;
	}

	public boolean hasHIST() {
		return this.hist != null;
	}

	public boolean hasPLTE() {
		return this.plte != null;
	}

	public BKGD getBKGD() {
		return this.bkgd;
	}

	// TODO Írjuk ki egy tmp fájlba
	public byte[] combineIDATChunks() {
		int length = 0;
		for (IDAT idat : this.idats) {
			length += idat.getLength();
		}

		byte[] combinedData = new byte[length];
		int index = 0;
		for (IDAT idat : this.idats) {
			for (int i = 0; i < idat.getLength(); i++) {
				combinedData[index] = idat.getContent()[i];
				index++;
			}
		}

		return combinedData;
	}

	public byte[] decompress(byte[] bytesToDecompress) {
		ZLib.decompress(bytesToDecompress);

		byte[] returnValues = null;

		Inflater inflater = new Inflater();

		int numberOfBytesToDecompress = bytesToDecompress.length;

		inflater.setInput(bytesToDecompress, 0, numberOfBytesToDecompress);

		int bufferSizeInBytes = numberOfBytesToDecompress;

		ArrayList<Byte> bytesDecompressedSoFar = new ArrayList<Byte>();

		try {
			while (inflater.needsInput() == false) {
				byte[] bytesDecompressedBuffer = new byte[bufferSizeInBytes];

				int numberOfBytesDecompressedThisTime = inflater.inflate(bytesDecompressedBuffer);

				for (int b = 0; b < numberOfBytesDecompressedThisTime; b++) {
					bytesDecompressedSoFar.add(bytesDecompressedBuffer[b]);
				}
			}

			returnValues = new byte[bytesDecompressedSoFar.size()];
			for (int b = 0; b < returnValues.length; b++) {
				returnValues[b] = (byte) (bytesDecompressedSoFar.get(b));
			}

		} catch (DataFormatException dfe) {
			dfe.printStackTrace();
		}

		inflater.end();

		return returnValues;
	}

	public void printDecompressedData() {
		Logger.info("Image.printDecompressedData", String.format("a következő %d sor", this.decompressedData.length));
		for (int i = 0; i < this.decompressedData.length; i++) {
			Logger.message(i + ": " + btpi(this.decompressedData[i]));
		}
	}

	/**
	 * Prints the filter method used for each scanline.
	 */
	public void printFilterMethods() {
		int[] counter = this.getFilterMethods();

		Logger.message("Filtering:");
		Logger.message("None: " + counter[0]);
		Logger.message("Sub: " + counter[1]);
		Logger.message("Up: " + counter[2]);
		Logger.message("Average: " + counter[3]);
		Logger.message("Paeth: " + counter[4]);
	}

	public int[] getFilterMethods() {
//		int bpp = this.ihdr.getBitsPerPixel();
//		int[] counter = new int[5];
//
//		if (this.ihdr.getWidth() * bpp % 8 == 0) {
//			for (int i = 0; i < this.decompressedData.length; i += this.ihdr.getWidth() * bpp / 8 + 1) {
//				counter[this.decompressedData[i]]++;
//			}
//		} else {
//			for (int i = 0; i < this.decompressedData.length; i += this.ihdr.getWidth() * bpp + 8
//					- (this.ihdr.getWidth() * bpp) % 8) {
//				counter[this.decompressedData[i]]++;
//			}
//		}
//
//		return counter;
		return this.filterMethods;
	}

	// TODO move to constructor
	@Override
	public void paint(Graphics g) {
		// gyorsabb megoldás, NormalBitSet-tel (hu.hvj.marci.global.NormalBitSet)
		long startTime = System.currentTimeMillis();
		BufferedImage bi = new BufferedImage(this.ihdr.getWidth(), this.ihdr.getHeight(), BufferedImage.TYPE_INT_ARGB);
		final int samplesPerPixel;
		switch (this.ihdr.getColorType()) {
		case 0:
		case 3:
			samplesPerPixel = 1;
			break;
		case 2:
			samplesPerPixel = 3;
			break;
		case 4:
			samplesPerPixel = 2;
			break;
		case 6:
			samplesPerPixel = 4;
			break;
		default:
			System.err.println("HIBA! Ismeretlen szín típus! " + this.ihdr.getColorType());
			return;
		}

		final Color bkgd;
		if (this.hasBKGD()) {
			this.bkgd.setPLTE(this.plte);
			bkgd = this.bkgd.getColor(this.ihdr.getBitDepth());
		} else if (this.hasPLTE() && this.hasHIST()) {
			bkgd = this.plte.getColor(this.hist.getMostCommonIndex());
		} else {
			bkgd = new Color(255, 255, 255);
		}
		g.setColor(bkgd);
		g.fillRect(0, 0, (int) (this.ihdr.getWidth() * this.scale), (int) (this.ihdr.getHeight() * this.scale));

		if (this.ihdr.getInterlaceMethod() == 0) {
			int w = (int) Math.ceil(this.ihdr.getWidth() * this.ihdr.getBitDepth() * samplesPerPixel / 8.);
			byte[][] unfilteredArray = new byte[this.ihdr.getHeight()][w];
			byte[] filterMethods = new byte[this.ihdr.getHeight()];

			int index = 0;
			for (int i = 0; i < this.ihdr.getHeight(); i++) {
				filterMethods[i] = this.decompressedData[index++];
				this.filterMethods[filterMethods[i]]++;
				for (int j = 0; j < w; j++, index++) {
					unfilteredArray[i][j] = this.decompressedData[index];
				}
			}

			unfilteredArray = Unfilter.unfilter(unfilteredArray, filterMethods,
					(int) Math.ceil(this.ihdr.getBitsPerPixel() / 8.));
			NormalBitSet bs = new NormalBitSet(unfilteredArray.length * unfilteredArray[0].length);
			bs.add(unfilteredArray);

			int[] samples = new int[samplesPerPixel]; // memóriacsökkentés miatt itt deklarálom
			for (int i = 0; i < this.ihdr.getHeight(); i++) {
				for (int j = 0; j < this.ihdr.getWidth(); j++) {
					for (int k = 0; k < samplesPerPixel; k++) {
						samples[k] = bs.getNextXBitInteger(this.ihdr.getBitDepth());
					}
					bi.setRGB(j, i, getColor(samples).getRGB());
				}
				bs.nextByte();
			}
		} else if (this.ihdr.getInterlaceMethod() == 1) {
			int[] widths = new int[7];
			int[] heights = new int[7];
			byte[][][] passes = new byte[7][][];
			byte[][] filterMethods = new byte[7][];
			NormalBitSet[] bs = new NormalBitSet[7];

			int pass = 0;

			for (; pass < 7; pass++) {
				widths[pass] = (int) Math
						.ceil((this.ihdr.getWidth() - starting_col[pass]) / (double) col_increment[pass]);
				heights[pass] = (int) Math
						.ceil((this.ihdr.getHeight() - starting_row[pass]) / (double) row_increment[pass]);
				passes[pass] = new byte[heights[pass]][(int) Math
						.ceil(widths[pass] * this.ihdr.getBitDepth() * samplesPerPixel / 8.)];
				filterMethods[pass] = new byte[heights[pass]];
				bs[pass] = new NormalBitSet(passes[pass].length * passes[pass][0].length);
			}

			pass = 0;

			int szamlalo = 0;
			while (pass < 7) {
				int w = (int) Math.ceil(widths[pass] * this.ihdr.getBitDepth() * samplesPerPixel / 8.);
				for (int i = 0; i < heights[pass]; i++) {
					filterMethods[pass][i] = this.decompressedData[szamlalo++];
					this.filterMethods[filterMethods[pass][i]]++;
					for (int j = 0; j < w; j++, szamlalo++) {
						passes[pass][i][j] = this.decompressedData[szamlalo];
					}
				}
				pass++;
			}

			for (int i = 0; i < passes.length; i++) {
				passes[i] = Unfilter.unfilter(passes[i], filterMethods[i],
						(int) Math.ceil(this.ihdr.getBitsPerPixel() / 8.));
				bs[i].add(passes[i]);
			}

			pass = 0;
			int row, col;

			int[] samples = new int[samplesPerPixel];
			while (pass < 7) {
				row = starting_row[pass];
				while (row < this.ihdr.getHeight()) {
					col = starting_col[pass];
					while (col < this.ihdr.getWidth()) {
						for (int k = 0; k < samplesPerPixel; k++) {
							samples[k] = bs[pass].getNextXBitInteger(this.ihdr.getBitDepth());
						}
						bi.setRGB(col, row, getColor(samples).getRGB());
						col += col_increment[pass];
					}
					row += row_increment[pass];
					bs[pass].nextByte();
				}
				pass++;
			}
		} else {
			System.err.println("HIBA! Ismeretlen interlacing methódus! " + this.ihdr.getInterlaceMethod());
			return;
		}
		// régi, hosszú megoldás
//		int bytesPerPixel;
//		switch (this.ihdr.getColorType()) {
//		case 0:
//			if (this.ihdr.getBitDepth() % 8 == 0)
//				bytesPerPixel = this.ihdr.getBitDepth() / 8;
//			else {
//				boolean[] bitArray = BitHelper.bytesToBooleans(this.decompressedData);
//
//				Color bkgda = new Color(255, 255, 255);
//				if (this.hasBKGD()) {
//					bkgda = this.getBKGD().getColor(this.ihdr.getBitDepth());
//				}
//				g.setColor(bkgda);
//				g.fillRect(0, 0, (int) (this.ihdr.getWidth() * this.scale), (int) (this.ihdr.getHeight() * this.scale));
//
//				if (this.ihdr.getInterlaceMethod() == 0) {
//					byte[] filterMethods = new byte[this.ihdr.getHeight()];
//					byte[][] pixels = new byte[this.ihdr.getHeight()][this.ihdr.getWidth()];
//
//					int unusedBits = (8 - this.ihdr.getWidth() * this.ihdr.getBitDepth() % 8) % 8;
//					int rowSize = this.ihdr.getWidth() * this.ihdr.getBitDepth() + 8 - unusedBits;
//					for (int i = 0, index1 = 0, index2 = 0; i < bitArray.length; i++) {
//						int b = bitArray[i] ? 1 : 0;
//						if (rowSize - i % rowSize == unusedBits) {
//							i += unusedBits - 1;
//							continue;
//						}
//						if (i % rowSize == 0) {
//							filterMethods[index1] = BitHelper
//									.booleansToByte(PNGHelper.getArrayPart(bitArray, i, i + 7));
//							i += 7;
//							index1++;
//							index2 = 0;
//						} else {
//							pixels[index1 - 1][index2] |= b
//									* (int) Math.pow(2, this.ihdr.getBitDepth() - i % this.ihdr.getBitDepth() - 1);
//							if ((i + 1) % this.ihdr.getBitDepth() == 0)
//								index2++;
//						}
//					}
//
//					if (this.unfilter)
//						pixels = Unfilter.unfilter(pixels, filterMethods, this.ihdr.getBitDepth());
//
//					for (int i = 0; i < pixels.length; i++) {
//						for (int j = 0; j < pixels[i].length; j++) {
//							float gray = (float) (pixels[i][j] / (Math.pow(2, this.ihdr.getBitDepth()) - 1));
//							bi.setRGB(j, i, new Color(gray, gray, gray).getRGB());
//						}
//					}
//
//					g.drawImage(bi, 0, 0, (int) (this.ihdr.getWidth() * this.scale),
//							(int) (this.ihdr.getHeight() * this.scale), 0, 0, bi.getWidth(), bi.getHeight(), null);
//
//					Logger.debug(String.format("A kép festése befejeződött %.2f másodperc alatt",
//							(System.currentTimeMillis() - startTime) / 1000.0), 1);
//				} else if (this.ihdr.getInterlaceMethod() == 1) { // Adam-7
//					int[] widths = new int[7];
//					int[] heights = new int[7];
//					byte[][][] passes = new byte[7][][];
//					byte[][] filterMethods = new byte[7][];
//
//					int pass = 0;
//
//					while (pass < 7) {
//						widths[pass] = (int) Math
//								.ceil((this.ihdr.getWidth() - starting_col[pass]) / (double) col_increment[pass]);
//						heights[pass] = (int) Math
//								.ceil((this.ihdr.getHeight() - starting_row[pass]) / (double) row_increment[pass]);
//						passes[pass] = new byte[heights[pass]][widths[pass]];
//						filterMethods[pass] = new byte[heights[pass]];
//
//						pass++;
//					}
//
//					pass = 0;
//
//					int szamlalo = 0;
//					while (pass < 7) {
//						for (int i = 0; i < passes[pass].length; i++) {
//							filterMethods[pass][i] = this.decompressedData[szamlalo++];
//							for (int j = 1; j < passes[pass][i].length; j++, szamlalo++) {
//								try {
//									passes[pass][i][j] = this.decompressedData[szamlalo];
//								} catch (Exception e) {
//									System.err.println(
//											pass + " " + i + " " + j + " " + szamlalo + " " + passes[pass][i].length);
//									System.exit(0);
//								}
//							}
//						}
//						pass++;
//					}
//
//					for (int i = 0; i < filterMethods.length; i++) {
//						passes[i] = Unfilter.unfilter(passes[i], filterMethods[i], 1);
//					}
//
//					pass = 0;
//					int row, col;
//
//					while (pass < 7) {
//						row = starting_row[pass];
//						int i = 0;
//						while (row < this.ihdr.getHeight()) {
//							col = starting_col[pass];
//							int j = 0;
//							while (col < this.ihdr.getWidth()) {
//								bi.setRGB(col, row, this.plte.getColor(passes[pass][i][j]).getRGB());
//								col += col_increment[pass];
//								j++;
//							}
//							row += row_increment[pass];
//							i++;
//						}
//
//						pass++;
//					}
//				} else {
//					Logger.error("Image.paint",
//							String.format("Unsupported interlacing method (%d)", this.ihdr.getInterlaceMethod()));
//				}
//				return;
//			}
//			break;
//		case 3: // paletted
//			boolean[] bitArray = BitHelper.bytesToBooleans(this.decompressedData);
//
//			byte[] filterMethods = new byte[this.ihdr.getHeight()];
//			byte[][] pixels = new byte[this.ihdr.getHeight()][this.ihdr.getWidth()];
//
//			int unusedBits = (8 - this.ihdr.getWidth() * this.ihdr.getBitDepth() % 8) % 8;
//			int rowSize = this.ihdr.getWidth() * this.ihdr.getBitDepth() + unusedBits + 8;
//			for (int i = 0, index1 = 0, index2 = 0; i < bitArray.length; i++) {
//				int b = bitArray[i] ? 1 : 0;
//				if (rowSize - i % rowSize == unusedBits) {
//					i += unusedBits - 1;
//					continue;
//				}
//				if (i % rowSize == 0) {
//					filterMethods[index1] = BitHelper.booleansToByte(PNGHelper.getArrayPart(bitArray, i, i + 7));
//					i += 7;
//					index1++;
//					index2 = 0;
//				} else {
//					pixels[index1 - 1][index2] |= b
//							* (int) Math.pow(2, this.ihdr.getBitDepth() - i % this.ihdr.getBitDepth() - 1);
//					if ((i + 1) % this.ihdr.getBitDepth() == 0)
//						index2++;
//				}
//			}
//
//			pixels = Unfilter.unfilter(pixels, filterMethods, this.ihdr.getBitDepth());
//
//			Color bkgda;
//			if (this.hasBKGD()) {
//				bkgda = this.getBKGD().getColor(this.ihdr.getBitDepth());
//			} else if (this.hasHIST()) {
//				bkgda = this.plte.getColor(this.hist.getMostCommonIndex());
//			} else {
//				bkgda = this.plte.getColor(0);
//			}
//			g.setColor(bkgda);
//			g.fillRect(0, 0, (int) (pixels[0].length * this.scale), (int) (pixels.length * this.scale));
//
//			for (int i = 0; i < pixels.length; i++) {
//				for (int j = 0; j < pixels[i].length; j++) {
//					bi.setRGB(j, i, this.plte.getColor(btpi(pixels[i][j])).getRGB());
//				}
//			}
//
//			g.drawImage(bi, 0, 0, (int) (this.ihdr.getWidth() * this.scale), (int) (this.ihdr.getHeight() * this.scale),
//					0, 0, bi.getWidth(), bi.getHeight(), null);
//
//			Logger.debug(String.format("A kép festése befejeződött %.2f másodperc alatt",
//					(System.currentTimeMillis() - startTime) / 1000.0), 1);
//			return;
//		case 2:
//			bytesPerPixel = 3 * this.ihdr.getBitDepth() / 8;
//			break;
//		case 4:
//			bytesPerPixel = 2 * this.ihdr.getBitDepth() / 8;
//			break;
//		case 6:
//			bytesPerPixel = 4 * this.ihdr.getBitDepth() / 8;
//			break;
//		default:
//			Logger.error("Image.paint",
//					String.format("Color type is not supported this time. (%d)%n", this.ihdr.getColorType()));
//			return;
//		}
//
//		if (this.ihdr.getInterlaceMethod() == 0) {
//			byte[] filterMethods = new byte[this.ihdr.getHeight()];
//			byte[][] pixels = new byte[this.ihdr.getHeight()][this.ihdr.getWidth() * bytesPerPixel];
//
//			for (int i = 0, index1 = 0, index2 = 0; i < this.decompressedData.length; i++) {
//				if (i % (this.ihdr.getWidth() * bytesPerPixel + 1) == 0) {
//					filterMethods[index1] = this.decompressedData[i];
//					index1++;
//					index2 = 0;
//				} else {
//					pixels[index1 - 1][index2] = this.decompressedData[i];
//					index2++;
//				}
//			}
//
//			if (this.unfilter)
//				pixels = Unfilter.unfilter(pixels, filterMethods, bytesPerPixel);
//
//			Color bkgda = new Color(255, 255, 255);
//			if (this.hasBKGD()) {
//				bkgda = this.getBKGD().getColor(this.ihdr.getBitDepth());
//			}
//			g.setColor(bkgda);
//			g.fillRect(0, 0, (int) (pixels[0].length / bytesPerPixel * this.scale), (int) (pixels.length * this.scale));
//
//			for (int i = 0; i < pixels.length; i++) {
//				for (int j = 0, x = 0; j < pixels[i].length; j += bytesPerPixel, x++) {
//					bi.setRGB(x, i, getColor(this.ihdr.getColorType(), bytesPerPixel, pixels[i], j).getRGB());
//				}
//			}
//		} else if (this.ihdr.getInterlaceMethod() == 1) { // Adam-7
//			int[] widths = new int[7];
//			int[] heights = new int[7];
//			byte[][][] passes = new byte[7][][];
//			byte[][] filterMethods = new byte[7][];
//
//			int pass = 0;
//
//			while (pass < 7) {
//				widths[pass] = (int) Math
//						.ceil((this.ihdr.getWidth() - starting_col[pass]) / (double) col_increment[pass]);
//				heights[pass] = (int) Math
//						.ceil((this.ihdr.getHeight() - starting_row[pass]) / (double) row_increment[pass]);
//				passes[pass] = new byte[heights[pass]][widths[pass] * bytesPerPixel];
//				filterMethods[pass] = new byte[heights[pass]];
//
//				pass++;
//			}
//
//			pass = 0;
//
//			int szamlalo = 0;
//			while (pass < 7) {
//				for (int i = 0; i < passes[pass].length; i++) {
//					filterMethods[pass][i] = this.decompressedData[szamlalo++];
//					for (int j = 0; j < passes[pass][i].length; j += bytesPerPixel, szamlalo += bytesPerPixel) {
//						for (int k = 0; k < bytesPerPixel; k++) {
//							passes[pass][i][j + k] = this.decompressedData[szamlalo + k];
//						}
//					}
//				}
//				pass++;
//			}
//
//			for (int i = 0; i < filterMethods.length; i++) {
//				passes[i] = Unfilter.unfilter(passes[i], filterMethods[i], bytesPerPixel);
//			}
//
//			pass = 0;
//			int row, col;
//
//			while (pass < 7) {
//				row = starting_row[pass];
//				int i = 0;
//				while (row < this.ihdr.getHeight()) {
//					col = starting_col[pass];
//					int j = 0;
//					while (col < this.ihdr.getWidth()) {
//						bi.setRGB(col, row,
//								getColor(this.ihdr.getColorType(), bytesPerPixel, passes[pass][i], j).getRGB());
//						col += col_increment[pass];
//						j += bytesPerPixel;
//					}
//					row += row_increment[pass];
//					i++;
//				}
//
//				pass++;
//			}
//
//		} else {
//			Logger.error("Image.paint",
//					String.format("Undefined interlace method: %d", this.ihdr.getInterlaceMethod()));
//			return;
//		}

		g.drawImage(bi, 0, 0, (int) (this.ihdr.getWidth() * this.scale), (int) (this.ihdr.getHeight() * this.scale), 0,
				0, bi.getWidth(), bi.getHeight(), null);
		Logger.debug(String.format("A kép festése befejeződött %.2f másodperc alatt",
				(System.currentTimeMillis() - startTime) / 1000.0), 1);

	}

	private Color getColor(int[] samples) {
		switch (this.ihdr.getColorType()) {
		case 0:
			float g = (float) samples[0] / ((1 << this.ihdr.getBitDepth()) - 1);
			return new Color(g, g, g);
		case 2:
			if (this.ihdr.getBitDepth() == 8) {
				return new Color(samples[0], samples[1], samples[2]);
			} else {
				return new Color(samples[0] / 65535.0f, samples[1] / 65535.0f, samples[2] / 65535.0f);
			}
		case 3:
			return this.plte.getColor(samples[0]);
		case 4:
			g = (float) samples[0] / ((1 << this.ihdr.getBitDepth()) - 1);
			float a = (float) samples[1] / ((1 << this.ihdr.getBitDepth()) - 1);
			return new Color(g, g, g, a);
		case 6:
			if (this.ihdr.getBitDepth() == 8) {
				return new Color(samples[0], samples[1], samples[2], samples[3]);
			} else {
				return new Color(samples[0] / 65535.0f, samples[1] / 65535.0f, samples[2] / 65535.0f,
						samples[3] / 65535.0f);
			}
		default:
			System.err.println("HIBA! Ismeretlen szín típus!");
			return null;
		}
	}

	/**
	 * Régi, statikus megoldás a színek megszerzésére
	 */
	public static Color getColor(int colorType, int bytesPerPixel, byte[] pixels, int index) {
		switch (colorType) {
		case 0:
			if (bytesPerPixel == 1)
				return new Color(btpi(pixels[index]), btpi(pixels[index]), btpi(pixels[index]));
			else {
				float gray = PNGHelper.twoBytesToIntMSBFirst(PNGHelper.getArrayPart(pixels, index, index + 1))
						/ 65535.0f;
				return new Color(gray, gray, gray);
			}
		case 2:
			if (bytesPerPixel == 3)
				return new Color(btpi(pixels[index]), btpi(pixels[index + 1]), btpi(pixels[index + 2]));
			else
				return PNGHelper.getColorFromTwoByteRGB(PNGHelper.getArrayPart(pixels, index, index + 5));
		case 4:
			if (bytesPerPixel == 2)
				return PNGHelper.getColorFromGrayscaleAndAlpha(pixels[index], pixels[index + 1]);
			else
				return PNGHelper.getColorFromTwoByteGrayscaleAndAlpha(PNGHelper.getArrayPart(pixels, index, index + 3));
		case 6:
			if (bytesPerPixel == 4)
				return new Color(btpi(pixels[index]), btpi(pixels[index + 1]), btpi(pixels[index + 2]),
						btpi(pixels[index + 3]));
			else
				return PNGHelper.getColorFromTwoByteRGBA(PNGHelper.getArrayPart(pixels, index, index + 7));
		default:
			throw new IllegalArgumentException(String.format("Invalid colorType! (%d)", colorType));
		}
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public void switchFilter() {
		this.unfilter = !this.unfilter;
	}

}
