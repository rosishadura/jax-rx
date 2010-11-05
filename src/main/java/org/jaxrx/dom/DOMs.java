package org.jaxrx.dom;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;

/* TODO comments missing. */

@SuppressWarnings("all")
final class DOMs {
	/**
	 * Private constructor.
	 */
	private DOMs() {
	}

	private static final Map<String, Document> DOMS = new HashMap<String, Document>();

	static void putDOM(final Document doc, final String name) {
		DOMS.put(name, doc);
	}

	static Document getDOM(final String name) {
		return DOMS.get(name);
	}

	static boolean deleteDOM(final String name) {
		return DOMS.remove(name) != null;
	}

	static Set<String> getAllDOMs() {
		return DOMS.keySet();
	}
}
