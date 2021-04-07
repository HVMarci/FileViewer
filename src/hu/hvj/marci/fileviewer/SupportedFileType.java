package hu.hvj.marci.fileviewer;

import hu.hvj.marci.gzreader.GZReader;
import hu.hvj.marci.pngviewer.PNGViewer;

public enum SupportedFileType {
	PNG(".png", PNGViewer.class), GZIP(".gz", GZReader.class);

	private final String ext;
	private final Class<? extends Main> main;

	private SupportedFileType(String extension, Class<? extends Main> main) {
		this.ext = extension;
		this.main = main;
	}

	public String getExtension() {
		return this.ext;
	}

	public Class<? extends Main> getMainClass() {
		return this.main;
	}

}
