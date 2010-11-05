package org.jaxrx.core;

import java.util.HashMap;
import java.util.Map;
import org.jaxrx.JaxRx;

/**
 * This class organizes all implementations of the JAX-RX interface.
 *
 * @author Sebastian Graf, University of Konstanz
 *
 */
public final class Systems {
	/**
	 * Private constructor.
	 */
	private Systems() {
	}

	/**
	 * System enumeration.
	 */
	private enum Sys {
		/**
		 * The implementation paths.
		 */
		IMPLEMENTATION("org.jaxrx.systemPath", "org.jaxrx.dom.DOMJaxRx"),

		/**
		 * Default package. This results in a look up related to the system
		 * properties for a org.jaxrx.systemPath-key
		 */
		SYSTEMNAME("org.jaxrx.systemName", "dom");

		/** System key. */
		final String key;
		/** System value. */
		final String value;

		/**
		 * Constructor.
		 *
		 * @param paramKey
		 *            key
		 * @param paramValue
		 *            value
		 */
		private Sys(final String paramKey, final String paramValue) {
			this.key = paramKey;
			this.value = paramValue;
		}
	}

	/**
	 * Delimiter for multiple values.
	 */
	private static final String VALDELIM = ";";

	/**
	 * Block to load systems preference once. Note that in case of multivalue
	 * properties, all properties have to have the same multiple values.
	 */
	private static final Map<String, String> SYSTEMSIMPLS = new HashMap<String, String>();

	/**
	 * The map holds the instances representing the classes of the
	 * implementation.
	 */
	private static final Map<String, JaxRx> INSTANCES = new HashMap<String, JaxRx>();

	static {
		final String systemVal = System.getProperty(Sys.SYSTEMNAME.key);
		final String implVal = System.getProperty(Sys.IMPLEMENTATION.key);

		if (systemVal != null && implVal != null) {
			final String[] systemValSplitted = systemVal.split(VALDELIM);
			final String[] implValSplitted = implVal.split(VALDELIM);
			for (int i = 0; i < systemVal.split(VALDELIM).length; i++) {
				SYSTEMSIMPLS.put(systemValSplitted[i], implValSplitted[i]);
			}
		} else if (systemVal == null && implVal == null) {
			SYSTEMSIMPLS.put(Sys.SYSTEMNAME.value, Sys.IMPLEMENTATION.value);
		} else {
			// TODO insert cool exception;
			throw new IllegalStateException();
		}
	}

	/**
	 * Getting all available systems as keys and implementing packages as
	 * values.
	 *
	 * @return the systems implementations
	 */
	public static Map<String, String> getSystems() {
		return SYSTEMSIMPLS;
	}

	/**
	 * Returns the instance for the specified implementation. If the system is
	 * unknown, throws an exception.
	 *
	 * @param impl
	 *            implementation to be checked.
	 * @return instances
	 */
	public static JaxRx getInstance(final String impl) {
		final String path = Systems.getSystems().get(impl);
		if (path == null) {
			throw new JaxRxException(404, "Unknown implementation: " + impl);
		}

		JaxRx jaxrx = INSTANCES.get(path);
		if (jaxrx == null) {
			try {
				jaxrx = (JaxRx) Class.forName(path).newInstance();
				INSTANCES.put(path, jaxrx);
			} catch (final Exception ex) {
				throw new JaxRxException(ex);
			}
		}
		return jaxrx;
	}
}
