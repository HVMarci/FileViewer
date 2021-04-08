package hu.hvj.marci.pngviewer;

import static hu.hvj.marci.pngviewer.PNGHelper.fourBytesToIntMSBFirst;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import hu.hvj.marci.pngviewer.chunks.Chunk;
import hu.hvj.marci.pngviewer.chunks.ChunkTypes;
import hu.hvj.marci.pngviewer.chunks.UnknownChunk;

public class PNGReader {

	public static byte[] readHeader(InputStream is) throws IOException {
		byte[] buffer = new byte[8];

		is.read(buffer);

		return buffer;
	}

	public static Chunk readNextChunk(InputStream is)
			throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		byte[] lengthBuffer = new byte[4];
		is.read(lengthBuffer);
		int length = fourBytesToIntMSBFirst(lengthBuffer);

		byte[] nameBuffer = new byte[4];
		is.read(nameBuffer);

		byte[] contentBuffer = new byte[length];
		is.read(contentBuffer);

		byte[] crcBuffer = new byte[4];
		is.read(crcBuffer);

		for (ChunkTypes ct : ChunkTypes.values()) {
			if (ct.name.equals(ISO88591.toISO88591(nameBuffer)))
				return (Chunk) Class.forName(ct.c.getName()).getDeclaredConstructor(byte[].class, byte[].class)
						.newInstance(contentBuffer, crcBuffer);
		}

		return new UnknownChunk(lengthBuffer, nameBuffer, contentBuffer, crcBuffer);
	}
}
