package hu.hvj.marci.pngviewer.exceptions;

public class InvalidChunkLengthException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7498219362863815144L;

	public InvalidChunkLengthException(String name, int length, String validLength) {
		super(String.format("a %s chunk hossza csak %s lehet! (%d)", name, length, validLength));
	}

}
