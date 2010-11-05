package org.jaxrx.core;

/**
 * This class contains constants, which are used throughout the JAX-RX interface
 * and implementations.
 *
 * @author Sebastian Graf, Christian Gruen, Lukas Lewandowski, University of
 *         Konstanz
 *
 */
public final class URLConstants {
	/**
	 * The path of the underlying system.
	 */
	public static final String SYSTEM = "system";

	/**
	 * Name of JAX-RX resource.
	 */
	public static final String JAXRX = "jax-rx";

	/**
	 * Name of resource itself.
	 */
	public static final String RESOURCE = "resource";

	/**
	 * The interface URL.
	 */
	public static final String URL = "http://jax-rx.sourceforge.net";

	/**
	 * The root path.
	 */
	public static final String ROOTPATH = "/";

	/**
	 * The path of the underlying system.
	 */
	public static final String SYSTEMPATH = ROOTPATH + "{" + SYSTEM + "}";

	/**
	 * The path of the rest path within JAX-RX.
	 */
	public static final String JAXRXPATH = SYSTEMPATH + "/" + JAXRX;

	/**
	 * The path of the variable resource path, depending on the available
	 * resources.
	 */
	public static final String RESOURCEPATH = JAXRXPATH + "/{" + RESOURCE
			+ ":.+}";

	/**
	 * Private empty constructor.
	 */
	private URLConstants() {
	}
}
