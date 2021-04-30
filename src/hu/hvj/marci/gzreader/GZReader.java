package hu.hvj.marci.gzreader;

import java.awt.Font;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;

import javax.swing.JFrame;
import javax.swing.JLabel;

import hu.hvj.marci.fileviewer.Main;
import hu.hvj.marci.global.Reader;

import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;

public class GZReader extends Main {

	public static void main1(String[] args) throws Exception {
//		String filename = "C:\\HashiCorp\\Minecraft.tar.gz";
		String filename = "C:\\Users\\marci\\java_erdekessegek\\GZReader\\gunzip.c.gz";
//		String filename = "/home/marci/Letöltések/gunzip.c.gz";
//		String filename = "/home/marci/Dokumentumok/text.txt.gz";
		File f = new File(filename);
		FileInputStream fis = new FileInputStream(f);
		Reader r = new Reader(fis);
		JFrame loading = new JFrame("Kitömörítés folyamatban...");
		JLabel label = new JLabel("Kitömörítés folyamatban...");
		label.setFont(new Font("Arial", Font.PLAIN, 40));
		loading.getContentPane().add(label);
		loading.setSize(500, 200);
		loading.setVisible(true);
		GZip gz = new GZip(r, f);
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
//					int len = Inflater.lengthValue(lz77[i], lz77[i + 1]);
//					int dist = Inflater.distValue(lz77[i + 2], lz77[i + 3]);
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

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void main(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		JFrame loading = new JFrame("Kitömörítés folyamatban...");
		JLabel label = new JLabel("Kitömörítés folyamatban...");
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setFont(new Font("Arial", Font.PLAIN, 40));
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		loading.getContentPane().add(label, BorderLayout.NORTH);

		JLabel blockCount = new JLabel("A feldolgozás hamarosan megkezdődik");
		blockCount.setHorizontalAlignment(SwingConstants.CENTER);
		blockCount.setFont(new Font("Arial", Font.PLAIN, 26));
		blockCount.setBorder(new EmptyBorder(10, 10, 10, 10));
		loading.getContentPane().add(blockCount, BorderLayout.SOUTH);

		loading.setSize(500, 200);
		loading.setVisible(true);
		loading.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GZip gz = new GZip(new Reader(fis), file, blockCount);
		loading.dispose();
//		gz.writeData(f.getAbsolutePath().substring(0, f.getAbsolutePath().length() - 3));
//		gz.printData();
		GZGui gui = new GZGui(gz);
		gui.setVisible(true);

		fis.close();
	}
}
