package hu.hvj.marci.gifviewer;

import java.io.File;
import java.io.FileInputStream;

import hu.hvj.marci.fileviewer.Main;

public class GIFViewer extends Main {

	/**
	 * A GIF három kezdőbájtja.<br>
	 * ASCII: 'GIF'<br>
	 * Tízes számrendszer: 71, 73, 70<br>
	 * Tizenhatos számrendszer: 0x47, 0x49, 0x46
	 */
	public static final byte[] GIF_SIGNATURE = { 'G', 'I', 'F' };
	
	/**
	 * A támogatott verziókból egy tömb. Értéke jelenleg: 87a és 89a.
	 */
	public static final int[] SUPPORTED_VERIONS = { Header.VERSION_87A, Header.VERSION_89A };

	@Override
	public void main(File file) throws Exception {
		FileInputStream is = new FileInputStream(file);
		GIFDataStream ds = new GIFDataStream(is);
		is.close();

		int version = ds.getVersion();
		boolean isVersionSupported = false;
		for (int i = 0; i < SUPPORTED_VERIONS.length; i++) {
			if (version == SUPPORTED_VERIONS[i]) {
				isVersionSupported = true;
				break;
			}
		}
		System.out.println(
				"GIF version: " + ds.getVersionName() + (!isVersionSupported ? " (unsupported)" : " (supported)"));
		
		LogicalScreenDescriptor lsd = ds.getLogicalScreen().getLogicalScreenDescriptor();
		System.out.println("Logical Screen Width: " + lsd.getLogicalScreenWidth());
		System.out.println("Logical Screen Height: " + lsd.getLogicalScreenHeight());
		System.out.println("Has global color table: " + lsd.getGlobalColorTableFlag());
		System.out.println("Color resolution: " + lsd.getColorResolution());
		System.out.println("Is the global color table ordered: " + lsd.getSortFlag());
		System.out.println("Size of global color table: " + lsd.getSizeOfGlobalColorTable());
		System.out.println("Background color index: " + lsd.getBackgroundIndex());
		System.out.println("Pixel aspect ratio: " + lsd.getPixelAspectRatio());
	}

}
