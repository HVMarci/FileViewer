package hu.hvj.marci.pngviewer.gui;

import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import hu.hvj.marci.pngviewer.BitHelper;
import hu.hvj.marci.pngviewer.ISO88591;

public class AnalyseName extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1946303574037412351L;

	public AnalyseName(String name, Window parent) {
		super(parent, name);
		setSize(350, 350);
		setResizable(false);
		getContentPane().setLayout(null);

		JLabel chunkName = new JLabel(name);
		chunkName.setFont(new Font("Consolas", Font.BOLD, 60));
		chunkName.setBounds(30, 30, 146, 57);
		getContentPane().add(chunkName);

		JLabel arrowHead = new JLabel(" /\\  /\\  /\\  /\\");
		arrowHead.setFont(new Font("Consolas", Font.PLAIN, 15));
		arrowHead.setBounds(30, 85, 134, 25);
		getContentPane().add(arrowHead);

		byte[] nameAsBytes = name.getBytes(ISO88591.iso88591charset);
		boolean isSafeToCopy = BitHelper.byteToBooleans(nameAsBytes[3])[2],
				reservedBit = BitHelper.byteToBooleans(nameAsBytes[2])[2],
				isPrivate = BitHelper.byteToBooleans(nameAsBytes[1])[2],
				isAncillary = BitHelper.byteToBooleans(nameAsBytes[0])[2];
		JLabel text = new JLabel("<html>&nbsp;|&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;|<br>"
				+ "&nbsp;|&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;Chunk is "
				+ (isSafeToCopy ? "safe" : "unsafe") + " to copy<br>"
				+ "&nbsp;|&nbsp;&nbsp;&nbsp;|&nbsp;&nbsp;Reserved bit is " + (reservedBit ? 1 : 0) + "<br>"
				+ "&nbsp;|&nbsp;&nbsp;Chunk is " + (isPrivate ? "private" : "public") + "<br>" + "Chunk is "
				+ (isAncillary ? "ancillary" : "critical") + "</html>");
		text.setHorizontalAlignment(SwingConstants.LEFT);
		text.setVerticalAlignment(SwingConstants.TOP);
		text.setFont(new Font("Consolas", Font.PLAIN, 15));
		text.setBounds(34, 102, 319, 102);
		getContentPane().add(text);
		
		OKButton ok = new OKButton(this, this.getWidth(), this.getHeight());
		getContentPane().add(ok);
	}
}
