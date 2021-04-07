package hu.hvj.marci.pngviewer.chunks;

import static hu.hvj.marci.pngviewer.PNGViewer.LS;

import hu.hvj.marci.pngviewer.BitHelper;
import hu.hvj.marci.pngviewer.CRC32;
import hu.hvj.marci.pngviewer.ISO88591;
import hu.hvj.marci.pngviewer.PNGHelper;

public abstract class Chunk {

	protected final int length;
	protected final String name;
	// TODO NE tároljuk el a tartalmat, helyette mindent számoljunk ki a konstruktorban (ezáltal feleslegesen foglalná a memóriát)
	protected final byte[] content;
	protected final byte[] crc;

	protected Chunk(int length, String name, byte[] content, byte[] crc) {
		this.length = length;
		this.name = name;
		this.content = content;
		this.crc = crc;
		
		if (!this.checkCRC()) {
			System.err.println("Invalid CRC in " + name + " chunk");
		}
	}

	protected Chunk(byte[] length, byte[] name, byte[] content, byte[] crc) {
		this(PNGHelper.fourBytesToIntMSBFirst(length), ISO88591.toISO88591(name), content, crc);
	}

	public boolean isCritical() {
		byte firstByte = this.getNameByte(0);
		boolean criticalBit = BitHelper.byteToBooleans(firstByte)[2];
		return !criticalBit;
	}

	public boolean isPrivate() {
		byte secondByte = this.getNameByte(1);
		boolean privateBit = BitHelper.byteToBooleans(secondByte)[2];
		return privateBit;
	}

	public boolean isReserved() {
		byte thirdByte = this.getNameByte(2);
		boolean reversedBit = BitHelper.byteToBooleans(thirdByte)[2];
		return reversedBit;
	}

	public boolean isSafeToCopy() {
		byte fourthByte = this.getNameByte(3);
		boolean safeToCopyBit = BitHelper.byteToBooleans(fourthByte)[2];
		return safeToCopyBit;
	}

	public String getAttributes() {
		return (this.isCritical() ? "Critical" : "Ancillary") + "; "
				+ (this.isSafeToCopy() ? "Safe to copy" : "Unsafe to copy");
	}

	private byte getNameByte(int index) {
		if (index < 0 || index > 4)
			throw new IllegalArgumentException("Az index 0 és 4 között lehet! (" + index + ")");
		return this.name.getBytes(ISO88591.iso88591charset)[index];
	}

	public String[] getList() {
		return new String[] { this.getName(), this.getAttributes(),
				String.valueOf(this.getLength()) + (this.getLength() == 1 ? " byte" : " bytes"), this.getInfo() };
	}

	@Override
	public String toString() {
		return this.name + LS + "Hossz: " + this.length;
	}

	public int getLength() {
		return length;
	}

	public String getName() {
		return name;
	}

	public byte[] getContent() {
		return content;
	}

	public byte[] getCRC() {
		return crc;
	}

	public boolean checkCRC() {
		byte[] name = this.getName().getBytes(ISO88591.iso88591charset);
		byte[] content = this.getContent();

		byte[] data = PNGHelper.concatenateTwoArrays(name, content);

		int crc = PNGHelper.fourBytesToIntMSBFirst(this.getCRC());
		int validCRC = CRC32.crc(data);

		return crc == validCRC;
	}

	public abstract String getInfo();
}
