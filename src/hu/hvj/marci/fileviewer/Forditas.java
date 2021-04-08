package hu.hvj.marci.fileviewer;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import org.json.JSONObject;

public class Forditas {
	
	private static final Forditas hu_HU;
	public static final Forditas DEFAULT;
	
	private final HashMap<String, String> values;
	
	static {
		InputStream is = Forditas.class.getResourceAsStream("/hu/hvj/marci/fileviewer/hu_hu.json");
		Scanner sc = new Scanner(is).useDelimiter("\\A");
		String source = sc.next();
		sc.close();
		JSONObject json = new JSONObject(source);
		hu_HU = new Forditas(json);
		
		DEFAULT = hu_HU;
	}
	
	private Forditas(JSONObject json) {
		this.values = new HashMap<>();
		for (String key : JSONObject.getNames(json)) {
			values.put(key, json.getString(key));
		}
	}
	
	public String getText(String key) {
		return values.get(key);
	}

}
