package hu.hvj.marci.pngviewer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import hu.hvj.marci.pngviewer.chunks.ancillary.TIME;
import hu.hvj.marci.pngviewer.gui.AnalyseName;
import hu.hvj.marci.pngviewer.gui.IDATGui;
import hu.hvj.marci.pngviewer.gui.IHDRGui;
import hu.hvj.marci.pngviewer.gui.PLTEGui;
import hu.hvj.marci.pngviewer.gui.TIMEGui;

public class Table extends JTable {

	private static final long serialVersionUID = -5619673581207296433L;
	private final Window parent;
	private final Image image;

	public Table(String[][] data, String[] rownames, Window parent, Image image) {
		super(data, rownames);
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
					int column = getSelectedColumn();
					openGui(row, column);
				}
			}
		});
		this.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Enter");
		this.getActionMap().put("Enter", new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 2938377015321086000L;

			@Override
			public void actionPerformed(ActionEvent ae) {
				int row = getSelectedRow();
				int column = getSelectedColumn();
				openGui(row, column);
			}
		});

		this.parent = parent;
		this.image = image;
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

	public String getRowName(int row) {
		return this.getModel().getValueAt(row, 0).toString();
	}

	private void openGui(int row, int column) {
		Logger.debug("Table.mouseListener.mouseClicked", getRowName(row), 2);
		if (column == 1) {
			new AnalyseName(getRowName(row), this.parent).setVisible(true);
		} else {
			switch (getRowName(row)) {
			case "IHDR":
				new IHDRGui(parent, image.getIHDR()).setVisible(true);
				break;
			case "PLTE":
				new PLTEGui(parent, image.getPLTE()).setVisible(true);
				break;
			case "tIME":
				new TIMEGui(parent, (TIME) image.getChunks()[row]).setVisible(true);
				break;
			case "IDAT":
				new IDATGui(parent, image).setVisible(true);
				break;
			}
		}
	}
}
