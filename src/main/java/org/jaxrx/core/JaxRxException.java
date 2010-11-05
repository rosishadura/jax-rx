package org.jaxrx.core;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * An exception that is either thrown by the JAX-RX interface or its underlying
 * implementation.
 *
 * @author Sebastian Graf, Christian Gruen, Lukas Lewandowski, University of
 *         Konstanz
 *
 */
public final class JaxRxException extends WebApplicationException {
	/**
	 * Constructs a new exception with the given HTTP status code and status
	 * message.
	 *
	 * @param status
	 *            HTTP status code
	 * @param message
	 *            status message
	 */
	public JaxRxException(final int status, final String message) {
		super(Response.status(status).entity(message).
		    type("text/plain").build());
	}

	/**
	 * Constructor, wrapping the specified exception and setting 500 as HTTP
	 * status code.
	 *
	 * @param exception
	 *            exception to be wrapped
	 */
	public JaxRxException(final Exception exception) {
		this(500, exception.getMessage());
	}
}
