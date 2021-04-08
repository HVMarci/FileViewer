package hu.hvj.marci.icoviewer;

import static hu.hvj.marci.fileviewer.Forditas.DEFAULT;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import hu.hvj.marci.fileviewer.FileViewerMain;
import hu.hvj.marci.fileviewer.Forditas;

public class IcoGui extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2142747165309090729L;

	private int yCounter = 25;

	private static class Table extends JTable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4291091585058907855L;

		public static final String[] ICO_HEADER = { DEFAULT.getText("ico.index"), DEFAULT.getText("ico.width"),
				DEFAULT.getText("ico.height"), DEFAULT.getText("ico.format"),
				DEFAULT.getText("ico.palette.entry_count"), DEFAULT.getText("ico.color_planes"),
				DEFAULT.getText("ico.bpp"), DEFAULT.getText("ico.image_size") };

		public static final String[] CUR_HEADER = { DEFAULT.getText("ico.index"), DEFAULT.getText("ico.width"),
				DEFAULT.getText("ico.height"), DEFAULT.getText("ico.format"),
				DEFAULT.getText("ico.palette.entry_count"), DEFAULT.getText("ico.horizontal_hotspot"),
				DEFAULT.getText("ico.vertical_hotspot"), DEFAULT.getText("ico.image_size") };

		public Table(Object[][] content, int iconType, int fontSize, IcoGui parent) {
			super(content, iconType == 1 ? ICO_HEADER : CUR_HEADER);
			setRowHeight(fontSize + 6);

			this.addMouseListener(new MouseListener() {
				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						int row = getSelectedRow();
						parent.openPreview(row);
					}
				}
			});

			this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
					.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
			this.getActionMap().put("Enter", new AbstractAction() {

				/**
				 * 
				 */
				private static final long serialVersionUID = -4644844773689293250L;

				@Override
				public void actionPerformed(ActionEvent ae) {
					int row = getSelectedRow();
					parent.openPreview(row);
				}
			});
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
			Component c = super.prepareRenderer(renderer, row, column);
			if (row % 2 == 0)
				c.setBackground(Color.LIGHT_GRAY);
			else
				c.setBackground(Color.WHITE);

			int rendererWidth = c.getPreferredSize().width;
			TableColumn tableColumn = getColumnModel().getColumn(column);
			tableColumn.setPreferredWidth(
					Math.max(rendererWidth + getIntercellSpacing().width + 10, tableColumn.getPreferredWidth()));

			return c;
		}
	}

	private final IcoImage img;
	private final File file;

	public IcoGui(File file, IcoImage img) {
		super(file.getName());
		this.img = img;
		this.file = file;

		int w = img.getHeader().getType() == 1 ? 1200 : 1500, h = 500;
		getContentPane().setPreferredSize(new Dimension(w, h));
		pack();
		getContentPane().setLayout(null);

		Font f = FileViewerMain.font;

		Forditas lang = DEFAULT;
		JLabel type = new JLabel(String.format("%s: %s (%d)", lang.getText("ico.type"), img.getHeader().getTypeName(),
				img.getHeader().getType()));
		type.setFont(f);
		resize(type);
		getContentPane().add(type);

		JLabel imageCount = new JLabel(
				String.format("%s: %d", lang.getText("ico.image_count"), img.getHeader().getNumberOfImages()));
		imageCount.setFont(f);
		resize(imageCount);
		getContentPane().add(imageCount);

		JLabel duplaKatt = new JLabel(DEFAULT.getText("ico.double_click"));
		resize(duplaKatt);
		duplaKatt.setFont(f);
		getContentPane().add(duplaKatt);

		String[][] tableData = new String[img.getHeader().getNumberOfImages()][5];
		for (int i = 0; i < img.getHeader().getNumberOfImages(); i++) {
			IconDirEntry ide = img.getIconDirEntries()[i];
			tableData[i] = new String[] { String.valueOf(i + 1),
					String.valueOf(ide.getWidth() + " " + DEFAULT.getText("ico.pixel")),
					String.valueOf(ide.getHeight()) + " " + DEFAULT.getText("ico.pixel"), ide.getFormatName(),
					ide.hasPalette()
							? String.format("%d %s", ide.getPaletteEntryCount(), DEFAULT.getText("ico.palette.color"))
							: DEFAULT.getText("ico.palette.no"),
					String.valueOf(ide.getColorPlane()),
					String.format(img.getHeader().getType() == 1 ? "%d %s" : "%d", ide.getBitsPerPixel(),
							DEFAULT.getText("ico.bit")),
					String.format("%d %s", ide.getImageSize(), DEFAULT.getText("ico.byte")) };
		}

		Table t = new Table(tableData, img.getHeader().getType(), 16, this);
		t.setFont(new Font("Arial", Font.PLAIN, 16));
		t.setFillsViewportHeight(true);
		t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		JScrollPane jsp = new JScrollPane(t);
		getContentPane().add(jsp);
		jsp.setBounds(26, yCounter + 10, w - 52, h - yCounter - 26);

		addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {
			}

			@Override
			public void componentResized(ComponentEvent e) {
				jsp.setBounds(26, yCounter + 10, getContentPane().getWidth() - 52,
						getContentPane().getHeight() - yCounter - 26);
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}
		});

		JMenuBar jmb = new JMenuBar();
		JMenu fileMenu = new JMenu(DEFAULT.getText("menu.file"));
		fileMenu.setMnemonic(DEFAULT.getText("menu.file").charAt(0));

		JMenuItem export = new JMenuItem(DEFAULT.getText("menu.file.export"));
		ExportGui egui = new ExportGui(this, img, file);
		export.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				egui.setVisible(true);
			}
		});
		fileMenu.add(export);

		JMenuItem exit = new JMenuItem(DEFAULT.getText("menu.file.exit"));
		exit.setMnemonic(DEFAULT.getText("menu.file.exit").charAt(0));
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		fileMenu.add(exit);
		jmb.add(fileMenu);

		setJMenuBar(jmb);

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void resize(JLabel label) {
		label.setBounds(26, yCounter, 733, 24);
		yCounter += 25;
	}

	public void openPreview(int index) {
		IconDirEntry ide = img.getIconDirEntries()[index];
		try {
			String filename = "tmp" + String.valueOf((int) (Math.random() * 10000000))
					+ (ide.getFormat() == IconDirEntry.BMP ? ".bmp" : ".png");
			File file = new File(System.getProperty("user.home") + System.getProperty("file.separator") + filename);
			file.createNewFile();

			Exporter.export(file, ide, new FileInputStream(this.file));

			Desktop.getDesktop().open(file);
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
