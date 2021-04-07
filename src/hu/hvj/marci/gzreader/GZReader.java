package hu.hvj.marci.gzreader;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;

import javax.swing.JFrame;
import javax.swing.JLabel;

import hu.hvj.marci.fileviewer.Main;

public class GZReader extends Main {

	public static void main1(String[] args) throws Exception {
//		String filename = "C:\\HashiCorp\\Minecraft.tar.gz";
		String filename = "C:\\Users\\marci\\java_erdekessegek\\GZReader\\gunzip.c.gz";
//		String filename = "/home/marci/Letöltések/gunzip.c.gz";
//		String filename = "/home/marci/Dokumentumok/text.txt.gz";
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		JFrame loading = new JFrame("Kitömörítés folyamatban...");
		JLabel label = new JLabel("Kitömörítés folyamatban...");
		label.setFont(new Font("Arial", Font.PLAIN, 40));
		loading.add(label);
		loading.setSize(500, 200);
		loading.setVisible(true);
		GZip gz = new GZip(fis, f);
		loading.dispose();
//		gz.writeData(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 3));
//		gz.printData();
		GZGui gui = new GZGui(gz);
		gui.setVisible(true);
	}

	public static void main3(String[] args) throws Exception {
		String filename = "C:\\Users\\marci\\java_erdekessegek\\GZReader\\Minecraft.tar";
		File f = new File(filename);
		long fileSize = Files.size(f.toPath());

		FileInputStream fis = new FileInputStream(f);

		File of = new File(filename.concat(".lz77"));
		of.createNewFile();
		FileOutputStream fos = new FileOutputStream(of);

		while (fileSize > 0) {
			System.out.println(fileSize);
			byte[] b = new byte[fileSize > 32768 ? 32768 : (int) fileSize];
			fis.read(b);

			int[] lz77 = Deflater.deflateStyleLZ77(b);
			Deflater.createHuffmanTree(lz77);

			// betűvel kiírni az eredmény egy fájlba MEGŐRIZNI!!!
			byte[] ld = "LD".getBytes();
			for (int i = 0; i < lz77.length; i++) {
				if (lz77[i] >= 0 && lz77[i] <= 255) {
					fos.write(lz77[i]);
				} else if (lz77[i] == 256) {
					break;
				} else {
					int len = Inflater.lengthValue(lz77[i], lz77[i + 1]);
					int dist = Inflater.distValue(lz77[i + 2], lz77[i + 3]);
//					fos.write(String.format("[L=%d;D=%d]", len, dist).getBytes());
					fos.write(ld);
					i += 3;
				}
			}
			fileSize -= 32768;
		}
		fis.close();
		fos.close();
	}

	@Override
	public void main(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		JFrame loading = new JFrame("Kitömörítés folyamatban...");
		JLabel label = new JLabel("Kitömörítés folyamatban...");
		label.setFont(new Font("Arial", Font.PLAIN, 40));
		loading.add(label);
		loading.setSize(500, 200);
		loading.setVisible(true);
		GZip gz = new GZip(fis, file);
		loading.dispose();
//		gz.writeData(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 3));
//		gz.printData();
		GZGui gui = new GZGui(gz);
		gui.setVisible(true);

	}
}
