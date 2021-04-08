package hu.hvj.marci.pngviewer;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import hu.hvj.marci.pngviewer.chunks.Chunk;

public class ChunkViewer extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6806755844859914499L;

	public final int WIDTH = 1200, HEIGHT = 400;

	private final String[] ROWNAMES = { "Name", "Attributes", "Length", "Info" };

	public ChunkViewer(Image image, JFrame parent) {
		super(parent, "Image data");
		Chunk[] c = image.getChunks();
		String[][] data = new String[c.length][3];
		for (int i = 0; i < data.length; i++) {
			data[i] = c[i].getList();
		}

		Table t = new Table(data, ROWNAMES, this, image);

		t.setFont(new Font("Arial", Font.PLAIN, 16));
		t.setPreferredScrollableViewportSize(new Dimension(WIDTH - 40, HEIGHT - 75));
		t.setFillsViewportHeight(true);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JMenuBar jmb = new JMenuBar();

		JMenu file = new JMenu("File");
		file.setMnemonic(KeyEvent.VK_F);

//		if (!isDialog) {
//			JMenuItem openImage = new JMenuItem("Open...");
//			openImage.setMnemonic(KeyEvent.VK_O);
//			openImage.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					try {
//						JFileChooser jfc = new JFileChooser(directory);
//						jfc.showOpenDialog(d);
//						changeImage(new FileInputStream(jfc.getSelectedFile()), jfc.getSelectedFile().getParent());
//					} catch (InstantiationException | IllegalAccessException | ClassNotFoundException
//							| IllegalArgumentException | InvocationTargetException | NoSuchMethodException
//							| SecurityException | InvalidFileExcepton | IOException exception) {
//						exception.printStackTrace();
//					}
//				}
//			});
//			file.add(openImage);
//
//			JMenuItem imageMenuItem = new JMenuItem("View image");
//			imageMenuItem.addActionListener(new OpenImage(image, d));
//			imageMenuItem.setMnemonic(KeyEvent.VK_V);
//
//			file.add(imageMenuItem);
//		}

		JMenuItem exit = new JMenuItem("Exit");
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		exit.setMnemonic(KeyEvent.VK_E);
		file.add(exit);

		jmb.add(file);

		JScrollPane jsp = new JScrollPane(t);
		jsp.addComponentListener(new ScrollingTableFix(t, jsp));

		JPanel jp = new JPanel();
		jp.add(jsp);
		jp.setLayout(new GridLayout(1, 1));

		this.add(jp);

		this.setSize(WIDTH, HEIGHT);
		this.setJMenuBar(jmb);

		this.setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
	}
}
