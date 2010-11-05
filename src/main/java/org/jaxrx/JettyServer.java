package org.jaxrx;

import org.jaxrx.core.JaxRxException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * This is the main class to start the Jetty server to offer RESTful web
 * services support.
 *
 * @author Sebastian Graf, Christian Gruen, Patrick Lang, Lukas Lewandowski,
 *         University of Konstanz
 *
 */
public final class JettyServer {
	/**
	 * Server reference.
	 */
	private transient Server server;

	/**
	 * Constructor.
	 *
	 * @param port
	 *            web server port
	 * @throws Exception
	 *            exception
	 */
	public JettyServer(final int port) throws Exception {
    server = new Server(port);
    final ServletHolder servHolder = new ServletHolder(
        ServletContainer.class);
    servHolder.setInitParameter(
        "com.sun.jersey.config.property.resourceConfigClass",
        "com.sun.jersey.api.core.PackagesResourceConfig");
    servHolder.setInitParameter(
        "com.sun.jersey.config.property.packages",
        "org.jaxrx.resource");

    final Context context = new Context(server, "/", Context.SESSIONS);
    context.addServlet(servHolder, "/");
    server.start();
	}

	/**
   * Stops the server.
   */
	public void stop() {
	  try {
      server.stop();
    } catch (final Exception exce) {
      throw new JaxRxException(exce);
    }
	}
}
