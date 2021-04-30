package hu.hvj.marci.fileviewer;

import hu.hvj.marci.gzreader.GZReader;
import hu.hvj.marci.icoviewer.IcoViewer;
import hu.hvj.marci.pngviewer.PNGViewer;
import hu.hvj.marci.gifviewer.GIFViewer;

public enum SupportedFileType {
	PNG(PNGViewer.class, ".png"), GZIP(GZReader.class, ".gz", ".gzip"), ICO(IcoViewer.class, ".ico", ".cur"),
	GIF(GIFViewer.class, ".gif");

	private final String[] ext;
	private final Class<? extends Main> main;

	private SupportedFileType(Class<? extends Main> main, String... extensions) {
		this.ext = extensions;
		this.main = main;
	}

	public String[] getExtensions() {
		return this.ext;
	}

	public Class<? extends Main> getMainClass() {
		return this.main;
	}

}
