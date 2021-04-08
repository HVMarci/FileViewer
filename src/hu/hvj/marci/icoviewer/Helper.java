package hu.hvj.marci.icoviewer;

public class Helper {

	public static int twoBytesToIntLSBFirst(byte... b) {
		if (b.length != 2) {
			throw new IllegalArgumentException("A tömb hossza csak 2 lehet, ez pedig: " + b.length);
		}

		return (b[1] & 0xFF) << 8 | (b[0] & 0xFF);
	}

	public static int btpi(byte b) {
		return (int) b & 0xFF;
	}
	
	public static int fourBytesToIntLSBFirst(byte... b) {
		if (b.length != 4) {
			throw new IllegalArgumentException("A tömb hossza csak 4 lehet, ez pedig: " + b.length);
		}

		return (b[3] & 0xFF) << 24 | (b[2] & 0xFF) << 16 | (b[1] & 0xFF) << 8 | (b[0] & 0xFF);
	}
	
	public static byte[] intToByteArrayLSBFirst(int i) {
		byte[] b = new byte[4];
		b[0] = (byte) (i & 0xFF);
		b[1] = (byte) ((i >> 8) & 0xFF);
		b[2] = (byte) ((i >> 16) & 0xFF);
		b[3] = (byte) ((i >> 24) & 0xFF);
		
		return b;
	}

}
