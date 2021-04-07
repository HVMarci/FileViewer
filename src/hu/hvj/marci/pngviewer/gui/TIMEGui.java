package hu.hvj.marci.pngviewer.gui;

import javax.swing.JDialog;
import javax.swing.JLabel;

import hu.hvj.marci.pngviewer.PNGHelper;
import hu.hvj.marci.pngviewer.chunks.ancillary.TIME;
import java.awt.Font;
import java.awt.Window;

public class TIMEGui extends JDialog {

	/**
	 * Random generált UID
	 */
	private static final long serialVersionUID = -2538812666092450374L;
	private static final String N = "<br/>";

	public TIMEGui(Window parent, TIME time) {
		super(parent, "tIME chunk data");
		this.setSize(350, 300);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		getContentPane().setLayout(null);

		JLabel title = new JLabel("Last modification:");
		title.setBounds(20, 20, 265, 35);
		title.setFont(new Font("Arial", Font.BOLD, 30));
		getContentPane().add(title);

		JLabel text = new JLabel("<html>Year: " + (time.getYear() < 100 ? time.getYear() + 1900 : time.getYear()) + N
				+ "Month: " + PNGHelper.getMonthAsString(time.getMonth(), false) + N + "Day: " + time.getDay() + N
				+ "Hour: " + time.getHour() + N + "Minute: " + time.getMinute() + N + "Second: " + time.getSecond()
				+ "</html>");
		text.setBounds(34, 60, 214, 115);
		text.setFont(new Font("Arial", Font.PLAIN, 15));
		getContentPane().add(text);

		OKButton okButton = new OKButton(this, 350, 300);
		getContentPane().add(okButton);
	}

}
