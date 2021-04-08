package hu.hvj.marci.icoviewer;

public class IcoImage {

	private final Header header;
	private final IconDirEntry[] ide;

	public IcoImage(Header header, IconDirEntry[] ide) {
		this.header = header;
		this.ide = ide;
	}

	public Header getHeader() {
		return header;
	}

	public IconDirEntry[] getIconDirEntries() {
		return ide;
	}

}
