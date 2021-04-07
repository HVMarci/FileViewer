package hu.hvj.marci.pngviewer;

import java.sql.Time;

public class Logger {

	public static final int DEBUGLEVEL = 1;

	public static String debug(String sender, String message, int level) {
		String s = String.format("[%1$tH:%1$tM:%1$tS] %2$s: %3$s", new Time(System.currentTimeMillis()), sender, message);
		if (level <= DEBUGLEVEL) {
			System.out.println(s);
		}
		return s;
	}
	
	public static String debug(String message, int level) {
		String s = String.format("[%1$tH:%1$tM:%1$tS] %2$s", new Time(System.currentTimeMillis()), message);
		if (level <= DEBUGLEVEL) {
			System.out.println(s);
		}
		return s;
	}

	public static String info(String sender, String message) {
		return message(String.format("%s: %s", sender, message));
	}

	public static String message(String message) {
		String s = String.format("[%1$tH:%1$tM:%1$tS] %2$s", new Time(System.currentTimeMillis()), message);
		System.out.println(s);
		return s;
	}

	public static String error(String sender, String message) {
		String s = String.format("[%1$tH:%1$tM:%1$tS] %2$s: %3$s", new Time(System.currentTimeMillis()), sender, message);
		System.err.println(s);
		return s;
	}

}
