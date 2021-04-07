package hu.hvj.marci.fileviewer;

import java.io.File;

public class FileviewerMain {

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Kevés az argumentum!");
			return;
		}

//		File file = new File(args[0]);
		File file = new File("C:\\Users\\marci\\java_erdekessegek\\GZReader\\gunzip.c.gz");
		if (!file.exists()) {
			System.err.println("Ez a fájl nem létezik!");
			return;
		}
		if (file.isDirectory()) {
			System.err.println("Ez egy könyvtár!");
			return;
		}

		String filename = file.getName();
		for (SupportedFileType t : SupportedFileType.values()) {
			if (filename.endsWith(t.getExtension())) {
				Class<? extends Main> main = t.getMainClass();
				Main m = main.newInstance();
				m.main(file);
				return;
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
