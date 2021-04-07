package hu.hvj.marci.pngviewer;

import java.io.File;
import java.io.FileInputStream;

import hu.hvj.marci.fileviewer.Main;

public class PNGViewer extends Main {

	public static final String LS = System.lineSeparator();

	@Override
	public void main(File file) throws Exception {
//		String fileName = "C:\\Users\\marci\\java_erdekessegek\\PNGViewer\\iso_8859-1.png";
//		String fileName = "C:\\Users\\marci\\java_erdekessegek\\PNGViewer\\PNGSuite\\basi2c08.png";
//		String fileName = "C:\\ProgramData\\BlueStacks\\Engine\\UserData\\com.android.chrome.com.google.android.apps.chrome.Main.png";
//		String fileName = "C:\\Users\\marci\\java_erdekessegek\\PNGViewer\\Animated_PNG_example_bouncing_beach_ball.apng";ű
//		String fileName = "C:\\Users\\marci\\OneDrive\\Pictures\\Screenshots\\2021-03-08.png";
//		Logger.info("Main", "A fájl neve: " + fileName);
//		if (args.length > 0)
//			fileName = args[0];
//		File f = new File(fileName);
		FileInputStream is = new FileInputStream(file);

		ImageViewer m = new ImageViewer(is);
		m.openImage();
	}

}
