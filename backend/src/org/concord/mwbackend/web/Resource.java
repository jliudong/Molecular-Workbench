package org.concord.mwbackend.web;

import java.util.Locale;
import java.util.ResourceBundle;

public class Resource {

	private static Locale locale = new Locale("zh", "CN");
	private static ResourceBundle myResources = ResourceBundle.getBundle(
			"org.concord.mwbackend.web.properties.WebResources", locale);

	public static String get(String key) {
		return myResources.getString(key);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s = Resource.get("test");
		System.out.println("-->" + s);
	}

}
