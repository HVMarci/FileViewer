package hu.hvj.marci.pngviewer;

import static hu.hvj.marci.pngviewer.PNGHelper.btpi;
import static hu.hvj.marci.pngviewer.PNGHelper.ai;

public class Unfilter {

	public static byte[][] unfilter(byte[][] data, byte[] filterTypes, int bytesPerPixel) {
		byte[][] unfilteredArray = new byte[data.length][data[0].length];

		for (int i = 0; i < data.length; i++) {
			switch (filterTypes[i]) {
			// None
			case 0:
				unfilteredArray[i] = data[i];
				break;
			// Sub
			case 1:
				unfilteredArray[i] = unfilterWithSub(data[i], bytesPerPixel);
				break;
			// Up
			case 2:
				if (i - 1 < 0) {
					unfilteredArray[i] = data[i];
				} else {
					unfilteredArray[i] = unfiterWithUp(data[i], unfilteredArray[i - 1]);
				}
				break;
			// Average
			case 3:
				if (i - 1 < 0) {
					unfilteredArray[i] = unfilterWithAverage(data[i], null, bytesPerPixel);
				} else {
					unfilteredArray[i] = unfilterWithAverage(data[i], unfilteredArray[i - 1], bytesPerPixel);
				}
				break;
			// Paeth
			case 4:
				if (i - 1 < 0) {
					unfilteredArray[i] = unfilterWithPaeth(data[i], null, bytesPerPixel);
				} else {
					unfilteredArray[i] = unfilterWithPaeth(data[i], unfilteredArray[i - 1], bytesPerPixel);
				}
				break;
			default:
				unfilteredArray[i] = data[i];
				break;
			}
		}

		return unfilteredArray;
	}

	/**
	 * {@code Paeth(x) + PaethPredictor(Raw(x - bpp), Prior(x), Prior(x - bpp))},<br>
	 * where {@code x} ranges from zero to the number of bytes representing the
	 * scanline minus one,<br>
	 * {@code Raw()} refers to the raw data byte at that byte position in the
	 * scanline,<br>
	 * {@code Prior()} refers to the unfiltered bytes of the prior scanline,<br>
	 * and {@code bpp} is defined as for the {@code Sub()} filter.
	 * 
	 * @param data     the filtered data
	 * @param upperRow the prior scanline
	 * @param bpp      bytes per pixel
	 * @return The unfiltered data
	 */
	public static byte[] unfilterWithPaeth(byte[] data, byte[] upperRow, int bpp) {
		int[] fd = new int[data.length];
		for (int i = 0; i < fd.length; i++) {
			fd[i] = btpi(data[i]);
		}

		int[] iur = new int[data.length];
		if (upperRow != null)
			for (int i = 0; i < iur.length; i++) {
				iur[i] = btpi(upperRow[i]);
			}
		else
			for (int i = 0; i < data.length; i++) {
				iur[i] = 0;
			}

		int[] ud = new int[fd.length];

		for (int i = 0; i < ud.length; i++) {
			ud[i] = (ai(fd, i) + paethPredictor(ai(ud, i - bpp), ai(iur, i), ai(iur, i - bpp))) % 256;
		}

		return PNGHelper.intArrayToByteArray(ud);
	}

	/**
	 * Paeth predictor function (defined in RFC 2083)
	 * 
	 * @param a left
	 * @param b above
	 * @param c upper left
	 * @return Predictor pixel
	 */
	public static int paethPredictor(int a, int b, int c) {
		// a = left, b = above, c = upper left
		int p = a + b - c; // initial estimate
		int pa = Math.abs(p - a); // distances to a, b, c
		int pb = Math.abs(p - b);
		int pc = Math.abs(p - c);
		// return nearest of a,b,c,
		// breaking ties in order a,b,c.
		if (pa <= pb && pa <= pc)
			return a;
		else if (pb <= pc)
			return b;
		else
			return c;
	}

	/**
	 * {@code Average(x) + floor((Raw(x - bpp) + Prior(x)) / 2)},<br>
	 * where {@code x} ranges from zero to the number of bytes representing the
	 * scanline minus one,<br>
	 * {@code Raw()} refers to the raw data byte at that byte position in the
	 * scanline,<br>
	 * {@code Prior()} refers to the unfiltered bytes of the prior scanline,<br>
	 * and {@code bpp} is defined as for the {@code Sub()} filter.
	 * 
	 * @param data     the filtered data
	 * @param upperRow the prior scanline
	 * @param bpp      bytes per pixel
	 * @return The unfiltered data
	 */
	public static byte[] unfilterWithAverage(byte[] data, byte[] upperRow, int bpp) {
		int[] fd = new int[data.length];
		for (int i = 0; i < fd.length; i++) {
			fd[i] = btpi(data[i]);
		}

		int[] iur = new int[data.length];
		if (upperRow != null)
			for (int i = 0; i < iur.length; i++) {
				iur[i] = btpi(upperRow[i]);
			}
		else
			for (int i = 0; i < data.length; i++) {
				iur[i] = 0;
			}

		int[] ud = new int[fd.length];

		for (int i = 0; i < ud.length; i++) {
			ud[i] = (ai(fd, i) + (ai(ud, i - bpp) + ai(iur, i)) / 2) % 256;
		}

		return PNGHelper.intArrayToByteArray(ud);
	}

	/**
	 * {@code Up(x) + Prior(x)},<br>
	 * where {@code x} ranges from zero to the number of bytes representing the
	 * scanline minus one,<br>
	 * and {@code Prior(x)} refers to the unfiltered bytes of the prior scanline.
	 * 
	 * @param data     the filtered data
	 * @param upperRow the prior scanline
	 * @return The unfiltered data
	 */
	public static byte[] unfiterWithUp(byte[] data, byte[] upperRow) {
		int[] fd = new int[data.length];
		for (int i = 0; i < fd.length; i++) {
			fd[i] = btpi(data[i]);
		}

		int[] iur = new int[upperRow.length];
		for (int i = 0; i < iur.length; i++) {
			iur[i] = btpi(upperRow[i]);
		}

		int[] ud = new int[fd.length];

		for (int i = 0; i < ud.length; i++) {
			ud[i] = (ai(fd, i) + ai(iur, i)) % 256;
		}

		return PNGHelper.intArrayToByteArray(ud);
	}

	/**
	 * {@code Sub(x) + Raw(x - bpp)}, where {@code Raw} refers to the bytes already
	 * decoded.
	 * 
	 * @param data a filtered array
	 * @param bpp  number of bytes per pixel.<br>
	 *             For example, for color type 2 with a bit depth of 16, bpp is
	 *             equal to 6 (three samples, two bytes per sample);<br>
	 *             for color type 0 with a bit depth of 2, bpp is equal to 1
	 *             (rounding up);<br>
	 *             for color type 4 with a bit depth of 16, bpp is equal to 4
	 *             (two-byte grayscale sample, plus two-byte alpha sample).
	 * @return the unfiltered array
	 */
	public static byte[] unfilterWithSub(byte[] data, int bpp) {
		int[] filteredData = new int[data.length];
		for (int i = 0; i < filteredData.length; i++) {
			filteredData[i] = btpi(data[i]);
		}

		int[] unfilteredData = new int[filteredData.length];

		for (int i = 0; i < unfilteredData.length; i++) {
			unfilteredData[i] = (ai(filteredData, i) + ai(unfilteredData, i - bpp)) % 256;
		}

		return PNGHelper.intArrayToByteArray(unfilteredData);
	}

}
