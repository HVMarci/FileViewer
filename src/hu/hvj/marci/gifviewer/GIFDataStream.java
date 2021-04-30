package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import hu.hvj.marci.fileviewer.Forditas;

public class GIFDataStream {

	private final Header header;
	private final LogicalScreen logicalScreen;
	private final Data[] data;

	public static final byte EXTENSION_INTRODUCER = 0x21;
	public static final byte IMAGE_SEPARATOR = 0x2C;

	public static final byte PLAIN_TEXT_EXTENSION_LABEL = 0x01;
	public static final byte GRAPHICS_CONTROL_EXTENSION_LABEL = (byte) 0xF9;
	public static final byte COMMENT_EXTENSION_LABEL = (byte) 0xFE;
	public static final byte APPLICATION_EXTENSION_LABEL = (byte) 0xFF;

	public GIFDataStream(InputStream is) throws IOException {
		this.header = new Header(is);
		this.logicalScreen = new LogicalScreen(is);

		ArrayList<Data> als = new ArrayList<Data>();
		byte intro = (byte) is.read();
		GraphicControlExtension gce = null;
		while (intro != Trailer.LABEL) {
			if (intro == EXTENSION_INTRODUCER) {
				byte label = (byte) is.read();
				if (label == APPLICATION_EXTENSION_LABEL) {
					ApplicationExtension ae = new ApplicationExtension(is);
					als.add(ae);
//					System.out.println(
//							"Application extension, " + new String(ae.getAppID()) + new String(ae.getAppAuth()));
//					System.out.println("size: " + ae.getData().length);
				} else if (label == COMMENT_EXTENSION_LABEL) {
					CommentExtension ce = new CommentExtension(is);
					als.add(ce);
//					System.out.println("Comment extension, " + new String(ce.getData()));
				} else if (label == GRAPHICS_CONTROL_EXTENSION_LABEL) {
					gce = new GraphicControlExtension(is);
//					System.out.printf("Graphic Control Extension 0x%02X 0x%02X%n", intro, label);
//					System.out.printf(
//							"\tDisposal method = %d%n\tNeeds user input? %b%n\tHas transparent color? %b%n"
//							+ "\tDelay time = %d%n\tTransparent color index = %d%n",
//							gce.getDisposalMethod(), gce.getUserInputFlag(), gce.getTransparentColorFlag(),
//							gce.getDelayTime(), gce.getTransparentColorIndex());
				} else if (label == PLAIN_TEXT_EXTENSION_LABEL) {
					PlainTextExtension pte = new PlainTextExtension(is);
					als.add(new GraphicBlock(gce, pte));
					gce = null;
//					System.out.println("Plain text extension");
				} else {
					JOptionPane.showMessageDialog(null, "Érdekes label byte: " + (label & 0xFF),
							Forditas.DEFAULT.getText("error"), JOptionPane.ERROR_MESSAGE);
					System.err.println("Érdekes label byte: " + (label & 0xFF));
				}
			} else if (intro == IMAGE_SEPARATOR) {
//				System.out.println("Image");
				TableBasedImage tbi = new TableBasedImage(is);
				als.add(new GraphicBlock(gce, tbi));
				gce = null;
				System.out.println(tbi.getImageData().getLzwMinimumCodeSize());
			} else {
				JOptionPane.showMessageDialog(null, "Érdekes introducer byte: " + (intro & 0xFF),
						Forditas.DEFAULT.getText("error"), JOptionPane.ERROR_MESSAGE);
				System.err.println("Érdekes introducer byte: " + (intro & 0xFF));
			}
			
			intro = (byte) is.read();
		}

		this.data = new Data[als.size()];
		for (int i = 0; i < als.size(); i++) {
			this.data[i] = als.get(i);
		}
	}

	public Header getHeader() {
		return header;
	}

	public int getVersion() {
		return header.getVersion();
	}

	public String getVersionName() {
		return header.getVersionName();
	}

	public LogicalScreen getLogicalScreen() {
		return logicalScreen;
	}

	public Data[] getData() {
		return data;
	}

}
