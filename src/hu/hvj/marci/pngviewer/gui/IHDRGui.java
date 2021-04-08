package hu.hvj.marci.pngviewer.gui;

import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;

import hu.hvj.marci.pngviewer.chunks.critical.IHDR;

public class IHDRGui extends JDialog {

	/**
	 * Random generált UID
	 */
	private static final long serialVersionUID = 3542771134118075131L;
	private static final String N = "<br/>";

	public IHDRGui(Window parent, IHDR ihdr) {
		super(parent, "IHDR chunk data");
		this.setResizable(false);
		setSize(300, 300);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);

		JLabel title = new JLabel("IHDR data:");
		title.setFont(new Font("Arial", Font.BOLD, 30));
		title.setBounds(29, 13, 161, 44);
		getContentPane().add(title);

		JLabel text = new JLabel("<html>Image width: " + ihdr.getWidth() + N + "Image height: " + ihdr.getHeight() + N
				+ "Color mode: " + ihdr.getColorTypeAsString(false) + N + "Bit depth: " + ihdr.getBitDepth() + N
				+ "Interlacing: " + ihdr.getInterlaceMethodAsString() + "</html>");
		text.setBounds(32, 74, 215, 90);
		text.setFont(new Font("Arial", Font.PLAIN, 15));
		getContentPane().add(text);

		OKButton okButton = new OKButton(this, 300, 300);
		getContentPane().add(okButton);
	}
}
