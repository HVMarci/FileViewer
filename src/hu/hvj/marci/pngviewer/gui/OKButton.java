package hu.hvj.marci.pngviewer.gui;

import javax.swing.JButton;
import javax.swing.JDialog;

public class OKButton extends JButton {

	/**
	 * Random generált UID
	 */
	private static final long serialVersionUID = 3532786373033303994L;

	public OKButton(JDialog dialog, int w, int h) {
		super("OK");
		addActionListener(i -> {
			dialog.dispose();
		});
		setBounds(w - 150, h - 100, 97, 25);
	}

}
