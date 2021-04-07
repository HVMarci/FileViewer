package hu.hvj.marci.pngviewer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import hu.hvj.marci.pngviewer.chunks.Chunk;
import hu.hvj.marci.pngviewer.exceptions.InvalidFileExcepton;
import hu.hvj.marci.pngviewer.gui.ImageInfo;

public class ImageViewer {

	private Image img;
	private ChunkViewer cv;

	public ImageViewer(InputStream is) throws Exception {
		this.changeImage(is);
	}

	public ImageViewer(Image img) {
		this.img = img;
	}

	public Image changeImage(InputStream is) throws Exception {
		if (!PNGChecker.isValid(is)) {
			throw new InvalidFileExcepton("A fájl nem helyes!");
		} else {
			Logger.message("A fájl helyes!");

			ArrayList<Chunk> chunks = new ArrayList<>();
			while (is.available() > 0) {
				chunks.add(PNGReader.readNextChunk(is));
			}

			Chunk[] c = PNGHelper.alsToArray(chunks);

			this.img = new Image(c);
			return this.img;
		}
	}

	public void openImage() {
		long startTime = System.currentTimeMillis();
		JFrame jf = new JFrame("PNGViewer");
		Dimension d = resize(this.img.getIHDR().getWidth(), this.img.getIHDR().getHeight());
		this.cv = new ChunkViewer(this.img, jf);

		JMenuBar jmb = new JMenuBar();
		JMenu jm = new JMenu("File");
		jm.setMnemonic(KeyEvent.VK_F);

		ImageInfo ii = new ImageInfo(jf, this.img.getIHDR());

		JMenuItem info = new JMenuItem("Image info");
		info.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ii.setVisible(true);
			}
		});
		info.setMnemonic(KeyEvent.VK_I);
		jm.add(info);

		JMenuItem switchFilter = new JMenuItem("Switch filter");
		switchFilter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				img.switchFilter();
				img.repaint();
			}
		});
		switchFilter.setMnemonic(KeyEvent.VK_S);
		jm.add(switchFilter);

		JMenuItem viewChunks = new JMenuItem("View chunks");
		viewChunks.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cv.setVisible(true);
			}
		});
		viewChunks.setMnemonic(KeyEvent.VK_V);
		jm.add(viewChunks);

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				jf.dispose();
			}
		});
		exit.setMnemonic(KeyEvent.VK_E);
		jm.add(exit);
		jmb.add(jm);

		jf.getContentPane().setPreferredSize(d);
		jf.setResizable(false);
		jf.setJMenuBar(jmb);
		jf.pack();

		this.img.setScale((double) jf.getContentPane().getPreferredSize().getWidth() / this.img.getIHDR().getWidth());
		jf.getContentPane().add(this.img);

		jf.setVisible(true);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Logger.debug(String.format("ImageViewer létrehozása kész %.2f másodperc alatt.%n", (System.currentTimeMillis() - startTime) / 1000.0), 2);
	}

	public Dimension resize(Dimension d) {
		double wperh = d.getWidth() / d.getHeight();
		double nw = d.getWidth(), nh = d.getHeight();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int minSize = (int) (screenSize.getWidth() * 0.1);

		if (d.getWidth() < minSize) {
			nw = minSize;
			nh = nw / wperh;
		}

		if (nh < minSize) {
			nh = minSize;
			nw = nh * wperh;
		}

		if (nw > screenSize.getWidth()) {
			nw = screenSize.getWidth() - 50;
			nh = nw / wperh;
		}

		if (nh > screenSize.getHeight()) {
			nh = screenSize.getHeight() - 120;
			nw = nh * wperh;
		}

		return new Dimension((int) Math.floor(nw), (int) Math.floor(nh));
	}

	public Dimension resize(int w, int h) {
		return resize(new Dimension(w, h));
	}
}
