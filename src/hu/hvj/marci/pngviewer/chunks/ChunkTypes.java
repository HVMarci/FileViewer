package hu.hvj.marci.pngviewer.chunks;

import hu.hvj.marci.pngviewer.chunks.ancillary.BKGD;
import hu.hvj.marci.pngviewer.chunks.ancillary.CHRM;
import hu.hvj.marci.pngviewer.chunks.ancillary.GAMA;
import hu.hvj.marci.pngviewer.chunks.ancillary.HIST;
import hu.hvj.marci.pngviewer.chunks.ancillary.ICCP;
import hu.hvj.marci.pngviewer.chunks.ancillary.PHYS;
import hu.hvj.marci.pngviewer.chunks.ancillary.SRGB;
import hu.hvj.marci.pngviewer.chunks.ancillary.TEXT;
import hu.hvj.marci.pngviewer.chunks.ancillary.TIME;
import hu.hvj.marci.pngviewer.chunks.ancillary.ZTXT;
import hu.hvj.marci.pngviewer.chunks.critical.IDAT;
import hu.hvj.marci.pngviewer.chunks.critical.IEND;
import hu.hvj.marci.pngviewer.chunks.critical.IHDR;
import hu.hvj.marci.pngviewer.chunks.critical.PLTE;

public enum ChunkTypes {
	IHDR("IHDR", IHDR.class), IDAT("IDAT", IDAT.class), PLTE("PLTE", PLTE.class), IEND("IEND", IEND.class),
	GAMA("gAMA", GAMA.class), CHRM("cHRM", CHRM.class), BKGD("bKGD", BKGD.class), SRGB("sRGB", SRGB.class),
	ICCP("iCCP", ICCP.class), TEXT("tEXt", TEXT.class), ZTXT("zTXt", ZTXT.class), PHYS("pHYs", PHYS.class),
	TIME("tIME", TIME.class), HIST("hIST", HIST.class);

	public final String name;
	public final Class<?> c;

	ChunkTypes(String name, Class<?> c) {
		this.name = name;
		this.c = c;
	}
}
