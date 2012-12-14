package org.concord.mwbackend.util;

public class MwConstants {

	// I use Windows to develop the application and Linux to deploy it, so this is
	// used as a flag to distinguish development and deployment
	public final static boolean LOCAL = System.getProperty("os.name").startsWith("Windows");

	public final static String HOST = LOCAL ? "http://localhost/" : "http://mw2.concord.org/";
	public final static String PUBLIC_ROOT = HOST + "public/";
	public final static String HOME_DIRECTORY = LOCAL ? "C:/home/" : "/home/";

	public final static String BANNER_COLOR = "#3D77CB";
	public final static String SIDEBAR_COLOR = "#C1D3FB";
	public final static String TOOLBAR_COLOR = "#95B3DE";
	public final static String COLUMN_BAR_COLOR = "#D6DEEC";
	public final static String ROW_COLOR = "#FFF0F0";

	public final static long MILLISECONDS_OF_A_DAY = 24 * 60 * 60 * 1000;
	public final static long MILLISECONDS_OF_A_WEEK = 7 * MILLISECONDS_OF_A_DAY;
	public final static long MILLISECONDS_OF_A_QUARTER = 90 * MILLISECONDS_OF_A_DAY;

}
