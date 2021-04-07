package hu.hvj.marci.pngviewer.gui;

import java.awt.Font;
import java.awt.Window;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import hu.hvj.marci.pngviewer.Image;

public class IDATGui extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6813915301045338860L;

	public IDATGui(Window parent, Image img) {
		super(parent, "Filtering data");
		getContentPane().setFont(new Font("Arial", Font.PLAIN, 12));
		getContentPane().setLayout(null);
		setResizable(false);
		setSize(300, 300);

		JLabel title = new JLabel("Filtering data:");
		title.setFont(new Font("Arial", Font.BOLD, 30));
		title.setBounds(20, 20, 207, 44);
		getContentPane().add(title);

		int[] filterMethods = img.getFilterMethods();
		JLabel data = new JLabel("<html>None: " + filterMethods[0] + "<br>Sub: " + filterMethods[1] + "<br>Up: "
				+ filterMethods[2] + "<br>Average: " + filterMethods[3] + "<br>Paeth: " + filterMethods[4] + "</html>");
		data.setHorizontalAlignment(SwingConstants.LEFT);
		data.setVerticalAlignment(SwingConstants.TOP);
		data.setFont(new Font("Arial", Font.PLAIN, 16));
		data.setBounds(30, 70, 164, 104);
		getContentPane().add(data);

		OKButton ok = new OKButton(this, this.getWidth(), this.getHeight());
		getContentPane().add(ok);
	}

}
