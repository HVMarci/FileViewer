package hu.hvj.marci.icoviewer;

import static hu.hvj.marci.fileviewer.Forditas.DEFAULT;

import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import hu.hvj.marci.fileviewer.FileViewerMain;

public class ExportGui extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4744340250065574273L;

	private final IcoImage ii;
	private final File f;

	private class ExportListener implements ActionListener {
		private final JTextField filenameField, indexField;
		private final Window parent;

		public ExportListener(Window parent, JTextField indexField, JTextField filenameField) {
			this.parent = parent;
			this.indexField = indexField;
			this.filenameField = filenameField;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle(DEFAULT.getText("ico.exportgui.chooser_title"));
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int response = jfc.showSaveDialog(parent);
			if (response == JFileChooser.CANCEL_OPTION) {
				System.out.println("canceled");
				return;
			} else if (response == JFileChooser.ERROR_OPTION) {
				System.err.println("ERROR");
				JOptionPane.showMessageDialog(parent, DEFAULT.getText("ico.exportgui.error"), DEFAULT.getText("error"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			String filename = jfc.getSelectedFile() + System.getProperty("file.separator") + filenameField.getText();
			if (!filename.contains("%w") && !filename.contains("%h")) {
				filename = filename.concat("%wx%h");
			}

			String[] numbers = indexField.getText().replaceAll(" ", "").split(",");
			ArrayList<Integer> indexes = new ArrayList<>();
			for (String s : numbers) {
				if (s.contains("-")) {
					String[] split = s.split("-");
					int f = Integer.parseInt(split[0]), l = Integer.parseInt(split[1]);
					for (int i = f - 1; i < l; i++) {
						indexes.add(i);
					}
				} else {
					indexes.add(Integer.parseInt(s) - 1);
				}
			}

			for (int j = 0; j < indexes.size(); j++) {
				int i = indexes.get(j).intValue();
				int w = ii.getIconDirEntries()[i].getWidth(), h = ii.getIconDirEntries()[i].getHeight();

				String fn = filename.replaceAll("%w", String.valueOf(w)).replaceAll("%h", String.valueOf(h))
						.concat(ii.getIconDirEntries()[i].getFormat() == IconDirEntry.BMP ? ".bmp" : ".png");
				File outputFile = new File(fn);

				try {
					Exporter.export(outputFile, ii.getIconDirEntries()[i], new FileInputStream(f));
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(parent, e1.getClass().getName(), DEFAULT.getText("error"), JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			filenameField.setText("");
			indexField.setText("");
			JOptionPane.showMessageDialog(parent, DEFAULT.getText("ico.exportgui.successful"));
			parent.dispose();
		}
	}

	public ExportGui(Window parent, IcoImage icon, File f) {
		super(parent, DEFAULT.getText("ico.exportgui"));
		setSize(500, 500);
		this.ii = icon;
		this.f = f;
		getContentPane().setLayout(null);

		Font font = FileViewerMain.font;
		JLabel indexLabel = new JLabel(DEFAULT.getText("ico.exportgui.indexes"));
		indexLabel.setFont(font);
		indexLabel.setBounds(12, 13, 458, 24);
		getContentPane().add(indexLabel);

		JTextField indexField = new JTextField();
		indexField.setBounds(12, 41, 157, 30);
		indexField.setFont(font);
		getContentPane().add(indexField);
		indexField.setColumns(10);

		JLabel indexSyntax = new JLabel(DEFAULT.getText("ico.exportgui.index_syntax"));
		indexSyntax.setBounds(184, 41, 286, 30);
		indexSyntax.setFont(font);
		getContentPane().add(indexSyntax);

		JLabel filenameLabel = new JLabel(DEFAULT.getText("ico.exportgui.filename"));
		filenameLabel.setFont(font);
		filenameLabel.setBounds(12, 84, 458, 24);
		getContentPane().add(filenameLabel);

		JTextField filenameField = new JTextField();
		filenameField.setBounds(12, 118, 157, 30);
		filenameField.setFont(font);
		getContentPane().add(filenameField);
		filenameField.setColumns(10);

		JLabel filenameExample = new JLabel(DEFAULT.getText("ico.exportgui.filename.example"));
		filenameExample.setBounds(184, 118, 286, 24);
		filenameExample.setFont(font);
		getContentPane().add(filenameExample);

		JButton exportButton = new JButton(DEFAULT.getText("ico.exportgui"));
		exportButton.setBounds(12, 190, 157, 30);
		exportButton.setFont(font);

		exportButton.addActionListener(new ExportListener(this, indexField, filenameField));

		getContentPane().add(exportButton);

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
}
