package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;

public class GraphicControlExtension {

	private final int disposalMethod;
	private final boolean userInputFlag, transparentColorFlag;
	private final int delayTime, transparentColorIndex;

	public GraphicControlExtension(InputStream is) throws IOException {
		int blockSize = is.read();
		if (blockSize != 4) {
			System.err.println("Invalid block size at GraphicControlExtension! " + blockSize);
		}

		boolean[] packedFields = Helper.byteToBooleanArray((byte) is.read());
		if (Helper.booleansToInt(packedFields, 0, 3) != 0) {
			System.err.println("Invalid reserved bits at GraphicControlExtension! {" + (packedFields[0] ? 1 : 0) + ", "
					+ (packedFields[1] ? 1 : 0) + ", " + (packedFields[2] ? 1 : 0) + "}");
		}

		disposalMethod = Helper.booleansToInt(packedFields, 3, 6);
		userInputFlag = packedFields[6];
		transparentColorFlag = packedFields[7];

		delayTime = is.read() | is.read() << 8;
		transparentColorIndex = is.read();
		
		is.skip(1); // block terminator
	}

	public int getDisposalMethod() {
		return disposalMethod;
	}

	public boolean getUserInputFlag() {
		return userInputFlag;
	}

	public boolean getTransparentColorFlag() {
		return transparentColorFlag;
	}

	public int getDelayTime() {
		return delayTime;
	}

	public int getTransparentColorIndex() {
		return transparentColorIndex;
	}

}
