package hu.hvj.marci.fileviewer;

import hu.hvj.marci.gzreader.GZReader;
import hu.hvj.marci.icoviewer.IcoViewer;
import hu.hvj.marci.pngviewer.PNGViewer;

public enum SupportedFileType {
	PNG(PNGViewer.class, ".png"), GZIP(GZReader.class, ".gz"), ICO(IcoViewer.class, ".ico", ".cur");

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
