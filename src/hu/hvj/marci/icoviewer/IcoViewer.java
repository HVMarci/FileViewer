package hu.hvj.marci.icoviewer;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

import hu.hvj.marci.fileviewer.Main;

public class IcoViewer extends Main {

	@Override
	public void main(File file) throws Exception {
//		String fileName = "C:\\Users\\marci\\keve\\marciReadme\\master\\LOCAL\\titk\\cf.ico";
		FileInputStream fis = new FileInputStream(file);
		Header h = IcoReader.readHeader(fis);
		System.out.printf("Type: %s (%d)%nNumber of images: %d%n", h.getTypeName(), h.getType(), h.getNumberOfImages());
		IconDirEntry[] ides = new IconDirEntry[h.getNumberOfImages()];
		for (int i = 0; i < ides.length; i++) {
			FileInputStream tmp = new FileInputStream(file);
			ides[i] = IcoReader.readIconDirEntry(fis);

			byte[] sig = new byte[8];
			tmp.skip(ides[i].getImageOffset());
			tmp.read(sig);
			tmp.close();

			if (Arrays.equals(sig, Exporter.PNG_SIGNATURE)) {
				ides[i].setFormat(IconDirEntry.PNG);
			} else {
				ides[i].setFormat(IconDirEntry.BMP);
			}
		}
		IcoImage ii = new IcoImage(h, ides);

		IcoGui gui = new IcoGui(file, ii);
		gui.setVisible(true);

	}

}
