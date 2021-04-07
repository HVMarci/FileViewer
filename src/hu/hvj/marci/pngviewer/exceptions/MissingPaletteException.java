package hu.hvj.marci.pngviewer.exceptions;

public class MissingPaletteException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7129653216665346946L;

	public MissingPaletteException() {
		super("missing palette");
	}

}
