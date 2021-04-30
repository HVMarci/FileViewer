package hu.hvj.marci.global;

import java.io.IOException;
import java.io.InputStream;

public class Reader {

	private final byte[] data = new byte[32768];
	private final InputStream is;
	private int pointer = 0;

	public Reader(InputStream is) throws IOException {
		this.is = is;
		is.read(data);
	}

	public byte read() throws IOException {
		byte b = data[pointer];
		if (++pointer == data.length) {
			is.read(data);
			pointer = 0;
		}
		return b;
	}

	public void read(byte[] buf) throws IOException {
		if (buf.length + pointer <= data.length) {
			System.arraycopy(data, pointer, buf, 0, buf.length);
			pointer += buf.length;
			if (pointer == data.length) {
				pointer = 0;
				is.read(data);
			}
		} else {
			int bentvan = data.length - pointer;
			System.arraycopy(data, pointer, buf, 0, bentvan);
			pointer = 0;
			int nemkellBerakni = (buf.length - bentvan) / data.length;
			for (int i = 0; i < nemkellBerakni; i++) {
				is.read(buf, i * data.length + bentvan, data.length);
			}
			int elejere = (buf.length - bentvan) % data.length;
			try {
				is.read(data);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.arraycopy(data, pointer, buf, bentvan + nemkellBerakni * data.length, elejere);
			pointer = elejere;
		}
	}

	public int available() throws IOException {
		return is.available();
	}

}
