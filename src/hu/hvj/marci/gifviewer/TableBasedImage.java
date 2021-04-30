package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;

public class TableBasedImage extends GraphicRenderingBlock {

	private final ImageDescriptor imageDescriptor;
	private final ColorTable localColorTable;
	private final ImageData imageData;

	public TableBasedImage(InputStream is) throws IOException {
		this.imageDescriptor = new ImageDescriptor(is);
		if (this.imageDescriptor.getLocalColorTableFlag()) {
			this.localColorTable = new ColorTable(is, this.imageDescriptor.getSizeOfLocalColorTable());
		} else {
			this.localColorTable = null;
		}
		this.imageData = new ImageData(is);
	}

	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	public ColorTable getLocalColorTable() {
		return localColorTable;
	}

	public ImageData getImageData() {
		return imageData;
	}

}
