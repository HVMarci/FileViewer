package hu.hvj.marci.gifviewer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageData {

	private final int lzwMinimumCodeSize;

	public ImageData(InputStream is) throws IOException {
		this.lzwMinimumCodeSize = is.read();
		// TODO megcsinálni!!!
		HashMap<Integer, Integer> dictionary = new HashMap<>();
		ArrayList<Integer> output = new ArrayList<>();
		int size = is.read();
		int and = 0;
		for (int i = 0; i < lzwMinimumCodeSize; i++) {
			and |= 1 << i;
		}
		int bitekSzama = 0, a = 0;
		ArrayList<Integer> w = new ArrayList<>();
		
		boolean first = true;
		while (size != 0) {
			int i = 0;
			if (first) {
				i = 1;
				first = false;
				int k = is.read();
				output.add(k);
				w.add(k);
			}
			for (; i < size; i++) {
				a |= is.read() << bitekSzama;
				bitekSzama += 8;
				
				while (bitekSzama >= lzwMinimumCodeSize) {
					bitekSzama -= lzwMinimumCodeSize;
					int k = a & and;
					if (dictionary.containsKey(k)) {
						int entry = dictionary.get(k);
						output.add(entry);
					} else {
					}
				}
			}
			size = is.read();
		}
	}

	public int getLzwMinimumCodeSize() {
		return lzwMinimumCodeSize;
	}

}
