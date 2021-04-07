package hu.hvj.marci.pngviewer.gui;

import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import hu.hvj.marci.pngviewer.chunks.critical.IHDR;

public class ImageInfo extends JDialog {

	private static final long serialVersionUID = -3746945781022194167L;

	private static final String N = "<br/>";

	public ImageInfo(Window parent, IHDR ihdr) {
		super(parent, "Image info");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setSize(350, 350);
		this.setResizable(false);

		JPanel jp = new JPanel();

		JLabel title = new JLabel("Information:");
		title.setBounds(27, 26, 173, 35);
		title.setFont(new Font("Arial", Font.BOLD, 30));

		JLabel text = new JLabel("<html>Image width: " + ihdr.getWidth() + N + "Image height: " + ihdr.getHeight() + N
				+ "Color mode: " + ihdr.getColorTypeAsString(false) + N + "Bit depth: " + ihdr.getBitDepth() + N
				+ "Interlacing: " + ihdr.getInterlaceMethodAsString() + "</html>");
		text.setBounds(27, 74, 270, 90);
		text.setFont(new Font("Arial", Font.PLAIN, 15));
		jp.setLayout(null);

		jp.add(title);
		jp.add(text);

		getContentPane().add(jp);

		OKButton okButton = new OKButton(this, 350, 350);
		jp.add(okButton);
	}
}
