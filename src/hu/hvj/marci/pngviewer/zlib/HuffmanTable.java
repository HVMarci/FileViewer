package hu.hvj.marci.pngviewer.zlib;

import java.util.HashMap;

import hu.hvj.marci.pngviewer.Logger;

import static hu.hvj.marci.pngviewer.BitHelper.binaryToDecimal;
import static hu.hvj.marci.pngviewer.BitHelper.rb;
import static hu.hvj.marci.pngviewer.PNGHelper.getArrayPart;

public class HuffmanTable {

	private final HashMap<Integer, Integer> table;

	public HuffmanTable(boolean[] huffmanTable) {
		huffmanTable = rb(huffmanTable);
		int hlit = binaryToDecimal(rb(getArrayPart(huffmanTable, 4))) + 257;
		int hdist = binaryToDecimal(rb(getArrayPart(huffmanTable, 5, 9))) + 1;
		int hclen = binaryToDecimal(rb(getArrayPart(huffmanTable, 10, 13))) + 4;

		int[] clfclaa = new int[hclen];
		for (int i = 0; i < clfclaa.length; i++) {
			clfclaa[i] = binaryToDecimal(rb(getArrayPart(huffmanTable, 14 + i * 3, 16 + i * 3)));
		}
		CodeLengthsForCodeLengthAlphabet clfcla = new CodeLengthsForCodeLengthAlphabet(clfclaa);
		System.out.printf("Code lengths for length alpabet: %n[%d", clfcla.getLength(0));
		for (int i = 1; i < 19; i++) {
			System.out.printf(", %d", clfcla.getLength(i));
		}
		System.out.println("]");

		Logger.debug("HuffmanTable", String.format("%nHLIT: %d%nHDIST: %d%nHCLEN: %d%n", hlit, hdist, hclen), 1);
		table = new HashMap<>();
	}

	public int getValue(int huffman) {
		return 0;
	}

}
