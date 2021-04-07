package hu.hvj.marci.pngviewer.chunks.ancillary;

import java.util.HashMap;

import hu.hvj.marci.pngviewer.PNGHelper;
import hu.hvj.marci.pngviewer.chunks.Chunk;
import hu.hvj.marci.pngviewer.exceptions.InvalidChunkLengthException;

public class HIST extends Chunk {

	private final HashMap<Integer, Integer> frequency;
	private int mostCommonIndex = -1;

	public HIST(byte[] content, byte[] crc) {
		super(content.length, "hIST", content, crc);
		if (content.length % 2 != 0)
			throw new InvalidChunkLengthException("hIST", content.length, "length % 2 == 0");

		this.frequency = new HashMap<Integer, Integer>();
		for (int i = 0; i < content.length; i += 2) {
			this.frequency.put(i / 2, PNGHelper.twoBytesToIntMSBFirst(content[i], content[i + 1]));
		}
	}

	public int getMostCommonIndex() {
		if (this.mostCommonIndex == -1) {
			int mostCommon = 0, mostCommonIndex = 0;
			for (int i = 0; i < this.frequency.size(); i++) {
				if (this.frequency.get(i) > mostCommon) {
					mostCommon = this.frequency.get(i);
					mostCommonIndex = i;
				}
			}
			this.mostCommonIndex = mostCommonIndex;
		}

		return this.mostCommonIndex;
	}

	@Override
	public String getInfo() {
		return String.format("Image histogram for %d palette entries", this.content.length / 2);
	}

}
