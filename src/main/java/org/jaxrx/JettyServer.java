package org.jaxrx;

import org.jaxrx.core.JaxRxException;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.security.Constraint;
import org.mortbay.jetty.security.ConstraintMapping;
import org.mortbay.jetty.security.HashUserRealm;
import org.mortbay.jetty.security.SecurityHandler;
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
   *          web server port
   * @throws Exception
   *           exception
   */
  public JettyServer(final int port) throws Exception {
    server = new Server(port);

    final ServletHolder servHolder = new ServletHolder(ServletContainer.class);
    servHolder.setInitParameter(
        "com.sun.jersey.config.property.resourceConfigClass",
        "com.sun.jersey.api.core.PackagesResourceConfig");
    servHolder.setInitParameter("com.sun.jersey.config.property.packages",
        "org.jaxrx.resource");

    final Context context = new Context(server, "/", Context.SESSIONS);
    context.addServlet(servHolder, "/");
    server.start();
  }

  /**
   * Constructor.
   * 
   * @param port
   *          web server port
   * @param authentication <code>true</code> if server has to authenticate,
   *          <code>false</code> otherwise.
   * @throws Exception
   *           exception
   */
  public JettyServer(final int port, final boolean authentication)
      throws Exception {
    server = new Server(port);
      
    // create a new constraint for authentication
    Constraint constraint = new Constraint();
    // choose possible authentication, e.g., Basic, Digest, Form, etc
    constraint.setName(Constraint.__BASIC_AUTH);
    // set allowed roles
    constraint.setRoles(new String[] { "user", "admin", "read-only"});
    // set authentication as "must have", not authenticated user will not be
    // accepted
    constraint.setAuthenticate(authentication);

    // mapping of constraint to resource
    ConstraintMapping cm = new ConstraintMapping();
    cm.setConstraint(constraint);
    // users have to authenticate on all available resources
    cm.setPathSpec("/*");

    // choosing of security handler
    SecurityHandler sh = new SecurityHandler();

    // possible realms are HashUserRealm, JDBCUserRealm and JAASUserRealm
    sh.setUserRealm(new HashUserRealm("MyRealm",
        System.getProperty("user.home") + "/realm.properties"));
    // add constraintmapping
    sh.setConstraintMappings(new ConstraintMapping[] { cm});

    // add security handler to server instance
    server.addHandler(sh);

    final ServletHolder servHolder = new ServletHolder(ServletContainer.class);
    servHolder.setInitParameter(
        "com.sun.jersey.config.property.resourceConfigClass",
        "com.sun.jersey.api.core.PackagesResourceConfig");
    servHolder.setInitParameter("com.sun.jersey.config.property.packages",
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
    } catch(final Exception exce) {
      throw new JaxRxException(exce);
    }
  }
}
