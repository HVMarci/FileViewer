package hu.hvj.marci.pngviewer.chunks.ancillary;

import hu.hvj.marci.pngviewer.PNGHelper;
import hu.hvj.marci.pngviewer.chunks.Chunk;

public class TIME extends Chunk {

	public TIME(byte[] content, byte[] crc) {
		super(7, "tIME", content, crc);
		if (content.length != 7)
			throw new IllegalArgumentException("A tIME chunk hossza csak 7 lehet! (" + content.length + ")");

		if (this.getMonth() < 1 || this.getMonth() > 12)
			throw new IllegalArgumentException("A hónap " + this.getMonth() + "! (csak 1-12 lehet)");
		if (this.getDay() < 1 || this.getDay() > 31)
			throw new IllegalArgumentException("A nap " + this.getDay() + "! (csak 1-31 lehet)");
		if (this.getHour() < 0 || this.getHour() > 23)
			throw new IllegalArgumentException("A hét " + this.getHour() + "! (csak 0-23 lehet)");
		if (this.getMinute() < 0 || this.getMinute() > 59)
			throw new IllegalArgumentException("A perc " + this.getMinute() + "! (csak 0-59 lehet)");
		if (this.getSecond() < 0 || this.getSecond() > 60)
			throw new IllegalArgumentException("A másodperc " + this.getSecond() + "! (csak 0-60 lehet)");
	}

	@Override
	public String getInfo() {
		return "Last modification: " + PNGHelper.formatDate(this.getYear(), this.getMonth(), this.getDay(),
				this.getHour(), this.getMinute(), this.getSecond(), "UTC");
	}

	public int getYear() {
		int year = PNGHelper.twoBytesToIntMSBFirst(PNGHelper.getArrayPart(this.content, 1));
		return year;
	}

	public byte getMonth() {
		byte month = this.content[2];
		return month;
	}

	public byte getDay() {
		byte day = this.content[3];
		return day;
	}

	public byte getHour() {
		byte hour = this.content[4];
		return hour;
	}

	public byte getMinute() {
		byte minute = this.content[5];
		return minute;
	}

	public byte getSecond() {
		byte second = this.content[6];
		return second;
	}

}
