package hu.hvj.marci.pngviewer;

import java.awt.Color;
import java.util.ArrayList;

import hu.hvj.marci.pngviewer.chunks.Chunk;
import hu.hvj.marci.pngviewer.chunks.critical.IDAT;

public class PNGHelper {

	/**
	 * First byte: color type, second byte: bit depth
	 */
	public static final int[][] SUPPORTED_COLOR_AND_BIT_DEPTHS = {
			// Each pixel is a grayscale sample.
			{ 0, 1 }, { 0, 2 }, { 0, 4 }, { 0, 8 }, { 0, 16 },
			// Each pixel is an R,G,B triple.
			{ 2, 8 }, { 2, 16 },
			// Each pixel is a palette index; a PLTE chunk must appear.
			{ 3, 1 }, { 3, 2 }, { 3, 4 }, { 3, 8 },
			// Each pixel is a grayscale sample, followed by an alpha sample.
			{ 4, 8 }, { 4, 16 },
			// Each pixel is an R,G,B triple, followed by an alpha sample.
			{ 6, 8 }, { 6, 16 } };

	public static int fourBytesToIntMSBFirst(byte... b) {
		if (b.length != 4) {
			throw new IllegalArgumentException("A tömb hossza csak 4 lehet, ez pedig: " + b.length);
		}

		return (b[0] & 0xFF) << 24 | (b[1] & 0xFF) << 16 | (b[2] & 0xFF) << 8 | (b[3] & 0xFF);
	}

	public static int twoBytesToIntMSBFirst(byte... b) {
		if (b.length != 2) {
			throw new IllegalArgumentException("A tömb hossza csak 2 lehet, ez pedig: " + b.length);
		}

		return (b[0] & 0xFF) << 8 | (b[1] & 0xFF);
	}

	public static int twoBytesToIntLSBFirst(byte... b) {
		if (b.length != 2) {
			throw new IllegalArgumentException("A tömb hossza csak 2 lehet, ez pedig: " + b.length);
		}

		return (b[1] & 0xFF) << 8 | (b[0] & 0xFF);
	}

	public static byte[] getArrayPart(byte[] b, int startIndex, int endIndex) {
		byte[] a = new byte[endIndex - startIndex + 1];

		for (int i = 0; i < a.length; i++) {
			a[i] = b[i + startIndex];
		}

		return a;
	}

	public static byte[] getArrayPart(byte[] b, int endIndex) {
		return getArrayPart(b, 0, endIndex);
	}

	public static boolean[] getArrayPart(boolean[] b, int startIndex, int endIndex) {
		boolean[] a = new boolean[endIndex - startIndex + 1];

		for (int i = 0; i < a.length; i++) {
			a[i] = b[i + startIndex];
		}

		return a;
	}

	public static boolean[] getArrayPart(boolean[] b, int endIndex) {
		return getArrayPart(b, 0, endIndex);
	}

	public static byte[] concatenateTwoArrays(byte[] array1, byte[] array2) {
		int length = array1.length + array2.length;

		byte[] c = new byte[length];
		int pos = 0;
		for (byte element : array1) {
			c[pos] = element;
			pos++;
		}

		for (byte element : array2) {
			c[pos] = element;
			pos++;
		}

		return c;
	}

	public static byte[] concat(byte[] array1, byte[] array2) {
		return concatenateTwoArrays(array1, array2);
	}

	public static boolean[] concatenateTwoArrays(boolean[] array1, boolean[] array2) {
		int length = array1.length + array2.length;

		boolean[] c = new boolean[length];
		int pos = 0;
		for (boolean element : array1) {
			c[pos] = element;
			pos++;
		}

		for (boolean element : array2) {
			c[pos] = element;
			pos++;
		}

		return c;
	}

	public static boolean[] concat(boolean[] array1, boolean[] array2) {
		return concatenateTwoArrays(array1, array2);
	}

	public static String addChar(String str, char ch, int position) {
		int len = str.length();
		char[] updatedArr = new char[len + 1];
		str.getChars(0, position, updatedArr, 0);
		updatedArr[position] = ch;
		str.getChars(position, len, updatedArr, position + 1);
		return new String(updatedArr);
	}

	public static String addChar(int str, char ch, int position) {
		return addChar(String.valueOf(str), ch, position);
	}

	/**
	 * Puts a dot after the first digit of an integer, given as string.<br>
	 * Example:<br>
	 * <ul>
	 * <li>123456 -> 1.23456</li>
	 * <li>352 -> 0.00352</li>
	 * </ul>
	 * But:
	 * <ul>
	 * <li>000000012 -> 0.00000012, not 0.00012</li>
	 * </ul>
	 * 
	 * @param str the integer as string to format
	 * @return the formatted string
	 */
	public static String format(String str) {
		while (str.length() < 6) {
			str = addChar(str, '0', 0);
		}

		return addChar(str, '.', 1);
	}

	/**
	 * Puts a dot after the first digit of an integer.<br>
	 * Example:<br>
	 * <ul>
	 * <li>123456 -> 1.23456</li>
	 * <li>352 -> 0.00352</li>
	 * </ul>
	 * 
	 * @param str the integer to format
	 * @return the formatted string
	 */
	public static String format(int str) {
		return format(String.valueOf(str));
	}

	public static Chunk[] alsToArray(ArrayList<Chunk> als) {
		Chunk[] c = new Chunk[als.size()];
		for (int i = 0; i < c.length; i++) {
			c[i] = als.get(i);
		}

		return c;
	}

	public static IDAT[] idatAlsToArray(ArrayList<IDAT> als) {
		IDAT[] c = new IDAT[als.size()];
		for (int i = 0; i < c.length; i++) {
			c[i] = als.get(i);
		}

		return c;
	}

	public static byte[] byteAlsToArray(ArrayList<Byte> als) {
		byte[] b = new byte[als.size()];

		for (int i = 0; i < b.length; i++) {
			b[i] = als.get(i).byteValue();
		}

		return b;
	}

	public static boolean isValidColorAndBitDepth(int[] b) {
		if (b.length != 2)
			throw new IllegalArgumentException("Egy pár hossza csak 2 lehet! (" + b.length + ")");

		for (int i = 0; i < SUPPORTED_COLOR_AND_BIT_DEPTHS.length; i++) {
			if (SUPPORTED_COLOR_AND_BIT_DEPTHS[i][0] == b[0] && SUPPORTED_COLOR_AND_BIT_DEPTHS[i][1] == b[1]) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Byte To Positive (unsigned) Int (short name for easy use, try:
	 * {@code import static
	 * hu.hvj.marci.pngviewer.PNGHelper.btpi;})
	 * 
	 * @param b the input byte
	 * @return the positive int
	 */
	public static int btpi(byte b) {
		return (int) b & 0xFF;
	}

	/**
	 * Creates a two dimensional {@code String} array from a {@code Color} array.<br>
	 * A row:<br>
	 * <ul>
	 * <li>0: index</li>
	 * <li>1: red</li>
	 * <li>2: green</li>
	 * <li>3: blue</li>
	 * <li>4: nothing (empty cell, background color goes here)</li>
	 * </ul>
	 * 
	 * @param r the {@code RGB} array with {@code n} elements
	 */
	public static String[][] colorArrayToString(Color[] r) {
		String[][] s = new String[r.length][5];

		for (int i = 0; i < s.length; i++) {
			s[i] = new String[] { String.valueOf(i), String.valueOf(r[i].getRed()),
					String.valueOf(r[i].getGreen()), String.valueOf(r[i].getBlue()), "" };
		}

		return s;
	}

	/**
	 * Formats a timestamp to:<br>
	 * day monthAsString year, hour:minute:second timezone<br>
	 * Hour, minute and second are always minimum 2 digits.<br>
	 * Example:<br>
	 * 1965, 3, 10, 3, 23, 6, UTC -> 10 Mar 1965, 03:23:06 UTC
	 * 
	 * @return Formatted date
	 */
	public static String formatDate(int year, byte month, byte day, byte hour, byte minute, byte second,
			String timeZone) {
		return day + " " + getMonthAsString(month, true) + " " + year + ", " + byteToTwoDigits(hour) + ":"
				+ byteToTwoDigits(minute) + ":" + byteToTwoDigits(second) + " " + timeZone;
	}

	/**
	 * Formats a {@code byte} to two digits.<br>
	 * Example:<br>
	 * 1 -> "01"<br>
	 * If {@code b} > 10, than it leaves the number untouched:<b> 34 -> "34"<br>
	 * 634 -> 634
	 * 
	 * @param b
	 * @return
	 */
	public static String byteToTwoDigits(byte b) {
		if (b < 10) {
			return "0" + b;
		} else {
			return String.valueOf(b);
		}
	}

	/**
	 * Returns a month's name.<br>
	 * If {@code shortVersion = true}, than you will get a 3 letter version.<br>
	 * Table of return values:
	 * <table border="1" style="border-collapse: collapse;">
	 * <tr>
	 * <th>Month</th>
	 * <th>{@code shortVersion = true}</th>
	 * <th>{@code shortVersion = false}</th>
	 * </tr>
	 * <tr>
	 * <td>1</td>
	 * <td>Jan</td>
	 * <td>January</td>
	 * </tr>
	 * <tr>
	 * <td>2</td>
	 * <td>Feb</td>
	 * <td>February</td>
	 * </tr>
	 * <tr>
	 * <td>3</td>
	 * <td>Mar</td>
	 * <td>March</td>
	 * </tr>
	 * <tr>
	 * <td>4</td>
	 * <td>Apr</td>
	 * <td>April</td>
	 * </tr>
	 * <tr>
	 * <td>5</td>
	 * <td>May</td>
	 * <td>May</td>
	 * </tr>
	 * <tr>
	 * <td>6</td>
	 * <td>Jun</td>
	 * <td>June</td>
	 * </tr>
	 * <tr>
	 * <td>7</td>
	 * <td>Jul</td>
	 * <td>July</td>
	 * </tr>
	 * <tr>
	 * <td>8</td>
	 * <td>Aug</td>
	 * <td>August</td>
	 * </tr>
	 * <tr>
	 * <td>9</td>
	 * <td>Sep</td>
	 * <td>September</td>
	 * </tr>
	 * <tr>
	 * <td>10</td>
	 * <td>Oct</td>
	 * <td>October</td>
	 * </tr>
	 * <tr>
	 * <td>11</td>
	 * <td>Nov</td>
	 * <td>November</td>
	 * </tr>
	 * <tr>
	 * <td>12</td>
	 * <td>Dec</td>
	 * <td>December</td>
	 * </tr>
	 * </table>
	 * 
	 * @param month        the month's number (1-12)
	 * @param shortVersion Do you want the 3 letter version?
	 * @return The formatted string
	 */
	public static String getMonthAsString(byte month, boolean shortVersion) {
		String text;
		switch (month) {
		case 1:
			text = "January";
			break;
		case 2:
			text = "February";
			break;
		case 3:
			text = "March";
			break;
		case 4:
			text = "April";
			break;
		case 5:
			text = "May";
			break;
		case 6:
			text = "June";
			break;
		case 7:
			text = "July";
			break;
		case 8:
			text = "August";
			break;
		case 9:
			text = "September";
			break;
		case 10:
			text = "October";
			break;
		case 11:
			text = "November";
			break;
		case 12:
			text = "December";
			break;
		default:
			text = "0" + byteToTwoDigits(month);
			break;
		}

		if (shortVersion) {
			return text.substring(0, 3);
		} else {
			return text;
		}
	}

	/**
	 * Casts all values in an {@code int} array to {@code byte}, and returns them as
	 * a {@code byte} array.
	 * 
	 * @param a the {@code int} array
	 * @return The {@code byte} array
	 */
	public static byte[] intArrayToByteArray(int[] a) {
		byte[] b = new byte[a.length];

		for (int i = 0; i < b.length; i++) {
			b[i] = (byte) a[i];
		}

		return b;
	}

	/**
	 * Return a value from an {@code int} array, using index.<br>
	 * If {@code x < 0}, than the return vaule is {@code 0}.<br>
	 * {@code ai} means Array Index (just for short name).
	 * 
	 * @param a the array
	 * @param x the index
	 * @return The element or 0
	 */
	public static int ai(int[] a, int x) {
		if (x < 0)
			return 0;
		return a[x];
	}

	public static Color getColorFromTwoByteRGB(byte[] b) {
		if (b.length != 6)
			throw new IllegalArgumentException("A b tömb hossza csak 6 lehet! (" + b.length + ")");
		return new Color(twoBytesToIntMSBFirst(getArrayPart(b, 1)) / 65535.0f,
				twoBytesToIntMSBFirst(getArrayPart(b, 2, 3)) / 65535.0f,
				twoBytesToIntMSBFirst(getArrayPart(b, 4, 5)) / 65535.0f);
	}

	public static Color getColorFromTwoByteRGBA(byte[] b) {
		if (b.length != 8)
			throw new IllegalArgumentException("A b tömb hossza csak 8 lehet! (" + b.length + ")");
		return new Color(twoBytesToIntMSBFirst(getArrayPart(b, 1)) / 65535.0f,
				twoBytesToIntMSBFirst(getArrayPart(b, 2, 3)) / 65535.0f,
				twoBytesToIntMSBFirst(getArrayPart(b, 4, 5)) / 65535.0f,
				twoBytesToIntMSBFirst(getArrayPart(b, 6, 7)) / 65535.0f);
	}

	public static Color getColorFromGrayscaleAndAlpha(byte gray, byte alpha) {
		return new Color(btpi(gray), btpi(gray), btpi(gray), btpi(alpha));
	}

	public static Color getColorFromTwoByteGrayscaleAndAlpha(byte[] b) {
		if (b.length != 4)
			throw new IllegalArgumentException("A b tömb hossza csak 4 lehet! (" + b.length + ")");
		float gray = twoBytesToIntMSBFirst(getArrayPart(b, 1)) / 65535.0f;
		float alpha = twoBytesToIntMSBFirst(getArrayPart(b, 2, 3)) / 65535.0f;
		return new Color(gray, gray, gray, alpha);
	}

	public static byte[] castIntArrayToByteArray(int... i) {
		byte[] b = new byte[i.length];

		for (int j = 0; j < b.length; j++) {
			b[j] = (byte) i[j];
		}

		return b;
	}

	public static int twoBitsToInt(boolean... bits) {
		if (bits.length != 2)
			throw new IllegalArgumentException("A bits tömb hossza csak 2 lehet! (" + bits.length + ")");

		return (bti(bits[0]) << 1) | bti(bits[1]);
	}

	/**
	 * Bit To Int
	 * 
	 * @param bit
	 * @return {@code 0} when the bit is {@code false}, {@code 1} when the bit is
	 *         {@code true}
	 */
	public static int bti(boolean bit) {
		return bit ? 1 : 0;
	}

	public static byte[] rotateBytes(byte[] bytes) {
		byte[] rotated = new byte[bytes.length];

		for (int i = 0; i < rotated.length; i++) {
			rotated[i] = bytes[bytes.length - i - 1];
		}

		return rotated;
	}

}
