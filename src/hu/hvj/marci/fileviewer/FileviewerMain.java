package hu.hvj.marci.fileviewer;

import static hu.hvj.marci.fileviewer.Forditas.DEFAULT;
import java.awt.Font;
import java.io.File;

import javax.swing.JOptionPane;

public class FileViewerMain {

	public static final Font font = new Font("Times New Roman", Font.PLAIN, 20);

	public static void main1(String[] args) {
		System.out.printf("0x%04X%n0x%04X%n", ~0x352B & 0xFFFF, ~0x8001 & 0xFFFF);
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Kevés az argumentum!");
			return;
		}

//		File file = new File(String.join(" ", args));
//		File file = new File("C:\\Users\\marci\\java_erdekessegek\\ICOViewer\\aero_nesw.cur");
//		File file = new File("C:\\Users\\marci\\OneDrive\\Pictures\\j-s billentyűzet másolata.png");
//		File file = new File("C:\\Users\\marci\\keve\\marciReadme\\master\\LOCAL\\titk\\cf.ico");
//		File file = new File("C:\\Users\\marci\\java_erdekessegek\\PNGViewer\\PNGSuite\\basi0g04.png");
//		File file = new File("C:\\Users\\marci\\java_erdekessegek\\PNGViewer\\hell.png");
//		File file = new File("C:\\Users\\marci\\java_erdekessegek\\SzenHidrogen\\gifek\\1.gif");
//		File file = new File("C:\\Users\\marci\\Downloads\\anvil-parser-0.9.0.tar.gz");
//		File file = new File("C:\\HashiCorp\\256x256.png.gz");
//		File file = new File("C:\\HashiCorp\\eclipse-inst-win64.exe.gz");
		File file = new File("C:\\HashiCorp\\gzip\\arduino-1.8.13-windows.exe.gz");
		if (!file.exists()) {
			System.err.println("Ez a fájl nem létezik! " + file.getAbsolutePath());
			JOptionPane.showMessageDialog(null, "Ez a fájl nem létezik! " + file.getAbsolutePath(),
					DEFAULT.getText("error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (file.isDirectory()) {
			System.err.println("Ez egy könyvtár!");
			JOptionPane.showMessageDialog(null, "Ez egy könyvtár!", DEFAULT.getText("error"),
					JOptionPane.ERROR_MESSAGE);
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
			JOptionPane.showMessageDialog(null, "Nem támogatott fájlkiterjesztés! (." + ext[ext.length - 1] + ")",
					DEFAULT.getText("error"), JOptionPane.ERROR_MESSAGE);
		} else {
			System.err.println("A kiterjesztés nélküli fájlok nem támogatottak!");
			JOptionPane.showMessageDialog(null, "A kiterjesztés nélküli fájlok nem támogatottak!",
					DEFAULT.getText("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
}
