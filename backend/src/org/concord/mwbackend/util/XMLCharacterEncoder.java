package org.concord.mwbackend.util;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * replace illegal characters in the text by entity references. Right now it legalizes only the five entity
 * references used by the XML syntax itself. No Unicode support is provided.
 * 
 * <pre>
 *  &lt; &lt; less than
 *  &gt; &gt; greater than
 *  &amp; &amp; ampersand 
 *  &amp;apos ' apostrophe 
 *  &quot; &quot; quotation mark
 * </pre>
 */

public final class XMLCharacterEncoder {

	private final static char LESS_THAN = '<';

	private final static char GREATER_THAN = '>';

	private final static char AMPERSAND = '&';

	private final static char APOSTROPHE = '\'';

	private final static char QUOTATION = '"';

	private final static String LESS_THAN_ER = "&lt;";

	private final static String GREATER_THAN_ER = "&gt;";

	private final static String AMPERSAND_ER = "&amp;";

	private final static String APOSTROPHE_ER = "&apos;";

	private final static String QUOTATION_ER = "&quot;";

	private static Map<Integer, Character> store;

	private XMLCharacterEncoder() {
	}

	public static String encode(String text) {

		if (text == null)
			return null;

		if (store == null) {
			store = new LinkedHashMap<Integer, Character>();
		} else {
			store.clear();
		}

		for (int i = 0; i < text.length(); i++) {

			switch (text.charAt(i)) {
			case LESS_THAN:
				store.put(new Integer(i), new Character(LESS_THAN));
				break;
			case GREATER_THAN:
				store.put(new Integer(i), new Character(GREATER_THAN));
				break;
			case AMPERSAND:
				store.put(new Integer(i), new Character(AMPERSAND));
				break;
			case APOSTROPHE:
				store.put(new Integer(i), new Character(APOSTROPHE));
				break;
			case QUOTATION:
				store.put(new Integer(i), new Character(QUOTATION));
				break;
			}
		}

		if (store.isEmpty())
			return text;

		StringBuffer sb = new StringBuffer(text);
		Integer index = null;
		Character character = null;
		int cumu = 0, del = 0;

		for (Iterator it = store.keySet().iterator(); it.hasNext();) {
			index = (Integer) it.next();
			character = (Character) store.get(index);
			switch (character.charValue()) {
			case LESS_THAN:
				del = index.intValue() + cumu;
				sb.deleteCharAt(del);
				sb.insert(del, LESS_THAN_ER);
				cumu += 3;
				break;
			case GREATER_THAN:
				del = index.intValue() + cumu;
				sb.deleteCharAt(del);
				sb.insert(del, GREATER_THAN_ER);
				cumu += 3;
				break;
			case AMPERSAND:
				del = index.intValue() + cumu;
				sb.deleteCharAt(del);
				sb.insert(del, AMPERSAND_ER);
				cumu += 4;
				break;
			case APOSTROPHE:
				del = index.intValue() + cumu;
				sb.deleteCharAt(del);
				sb.insert(del, APOSTROPHE_ER);
				cumu += 5;
				break;
			case QUOTATION:
				del = index.intValue() + cumu;
				sb.deleteCharAt(del);
				sb.insert(del, QUOTATION_ER);
				cumu += 5;
				break;
			}
		}

		return sb.toString();

	}

}