package hu.hvj.marci.fileviewer;

import java.awt.Font;
import java.io.File;

public class FileViewerMain {

	public static final Font font = new Font("Times New Roman", Font.PLAIN, 20);

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Kevés az argumentum!");
			return;
		}

		File file = new File(args[0]);
//		File file = new File("C:\\Users\\marci\\java_erdekessegek\\ICOViewer\\aero_nesw.cur");
//		File file = new File("C:\\Users\\marci\\java_erdekessegek\\ICOViewer\\sample_5184×3456.ico");
//		File file = new File("C:\\Users\\marci\\keve\\marciReadme\\master\\LOCAL\\titk\\cf.ico");
		if (!file.exists()) {
			System.err.println("Ez a fájl nem létezik!");
			return;
		}
		if (file.isDirectory()) {
			System.err.println("Ez egy könyvtár!");
			return;
		}

		String filename = file.getName().toLowerCase();
		for (SupportedFileType t : SupportedFileType.values()) {
			String[] extensions = t.getExtensions();

			for (String s : extensions) {
				if (filename.endsWith(s)) {
					Class<? extends Main> main = t.getMainClass();
					Main m = main.newInstance();
					m.main(file);
					return;
				}
			}
		}

		String[] ext = filename.split("\\.");
		if (ext.length > 1) {
			System.err.println("Nem támogatott fájlkiterjesztés! (." + ext[ext.length - 1] + ")");
		} else {
			System.err.println("A kiterjesztés nélküli fájlok nem támogatottak!");
		}
	}

}
