package org.jaxrx.resource;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import org.jaxrx.core.ResponseBuilder;
import org.jaxrx.core.Systems;
import org.jaxrx.core.JaxRxConstants;

/**
 * This class processes HTTP requests for the JAX-RX general URL part:
 * <code>/{system}/jax-rx/}</code>. Depending on which part of the URL a request
 * occurs, it creates a HTTP response containing the available resources
 * according to the URL path.
 *
 * @author Sebastian Graf, Christian Gruen, Lukas Lewandowski, University of
 *         Konstanz
 *
 */
@Path(JaxRxConstants.ROOTPATH)
public final class JaxRxResource extends AResource {
	/**
	 * This method waits for calls to the above specified URL
	 * {@link JaxRxConstants#ROOTPATH} and creates a response XML file containing
	 * the available further resources. In our case it is just the
	 * {@link JaxRxConstants#SYSTEMPATH} resource.
	 *
	 * @return The available resources according to the URL path.
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public StreamingOutput getRoot() {
		final List<String> resources = new ArrayList<String>();
		resources.addAll(Systems.getSystems().keySet());
		return ResponseBuilder.buildDOMResponse(resources);
	}

	/**
	 * This method waits for calls to the specified URL
	 * {@link JaxRxConstants#SYSTEMPATH} and creates a response XML file
	 * containing the available further resources. In our case it just the
	 * {@link JaxRxConstants#JAXRXPATH} resource.
	 *
	 * @param system
	 *            The associated system with this request.
	 * @return The available resources resources according to the URL path.
	 */
	@Path(JaxRxConstants.SYSTEMPATH)
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public StreamingOutput getSystem(
			@PathParam(JaxRxConstants.SYSTEM) final String system) {

		Systems.getInstance(system);
		final List<String> resources = new ArrayList<String>();
		resources.add(JaxRxConstants.JAXRX);
		return ResponseBuilder.buildDOMResponse(resources);
	}

	/**
	 * This method returns a collection of available resources. An available
	 * resource can be either a particular XML resource or a collection
	 * containing further XML resources.
	 *
	 * @param system
	 *            The associated system with this request.
	 * @param uri
	 *            The context information due to the requested URI.
	 * @return The available resources resources according to the URL path.
	 */
	@Path(JaxRxConstants.JAXRXPATH)
	@GET
	public Response getResource(
			@PathParam(JaxRxConstants.SYSTEM) final String system,
			@Context final UriInfo uri) {

    return getResource(system, uri, "");
	}

	/**
	 * This method will be called when a HTTP client sends a POST request to an
	 * existing resource with 'application/query+xml' as Content-Type.
	 *
	 * @param system
	 *            The implementation system.
	 * @param input
	 *            The input stream.
	 * @return The {@link Response} which can be empty when no response is
	 *         expected. Otherwise it holds the response XML file.
	 */
	@Path(JaxRxConstants.JAXRXPATH)
	@POST
	@Consumes(APPLICATION_QUERY_XML)
	public Response postQuery(
			@PathParam(JaxRxConstants.SYSTEM) final String system,
			final InputStream input) {

	  return postQuery(system, input, "");
	}
}
