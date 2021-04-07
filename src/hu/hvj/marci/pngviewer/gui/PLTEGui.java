package hu.hvj.marci.pngviewer.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import hu.hvj.marci.pngviewer.PNGHelper;
import hu.hvj.marci.pngviewer.chunks.critical.PLTE;

public class PLTEGui extends JDialog {

	/**
	 * Random generált UID
	 */
	private static final long serialVersionUID = -1231083572232834571L;
	private static final String[] columnName = { "Index", "Red", "Green", "Blue", "Color" };

	public PLTEGui(Window parent, PLTE plte) {
		super(parent, "PLTE chunk data");
		this.setSize(450, 350);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setResizable(false);

		Table t = new Table(PNGHelper.colorArrayToString(plte.getPalette()), columnName);

		JScrollPane jsp = new JScrollPane(t);
		getContentPane().add(jsp);
	}

	private class Table extends JTable {
		/**
		 * Random generált UID
		 */
		private static final long serialVersionUID = -3208234109893527580L;

		public Table(Object[][] rowData, String[] columnNames) {
			super(rowData, columnNames);
		}

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}

		@Override
		public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {

			Component c = super.prepareRenderer(renderer, row, column);
			if (column == 4) {
				c.setBackground(this.getColorFromRow(row));
			} else {
				if (row % 2 == 0) {
					c.setBackground(Color.LIGHT_GRAY);
				} else {
					c.setBackground(Color.WHITE);
				}
			}

			int rendererWidth = c.getPreferredSize().width;
			TableColumn tableColumn = getColumnModel().getColumn(column);
			tableColumn.setPreferredWidth(
					Math.max(rendererWidth + getIntercellSpacing().width + 10, tableColumn.getPreferredWidth()));

			return c;
		}

		public Color getColorFromRow(int row) {
			TableModel tm = this.getModel();
			return new Color(Integer.parseInt(String.valueOf(tm.getValueAt(row, 1))),
					Integer.parseInt(String.valueOf(tm.getValueAt(row, 2))),
					Integer.parseInt(String.valueOf(tm.getValueAt(row, 3))));
		}
	}

}
