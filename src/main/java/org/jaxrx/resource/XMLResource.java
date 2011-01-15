package org.jaxrx.resource;

import java.io.InputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jaxrx.JaxRx;
import org.jaxrx.core.ResourcePath;
import org.jaxrx.core.Systems;
import org.jaxrx.core.URLConstants;

/**
 * This class match HTTP requests for the {@link URLConstants#RESOURCEPATH}.
 * This means that JAX-RX returns the available resources which the underlying
 * implementation provide. Available resources could be collections or
 * particular XML resources.
 *
 * @author Sebastian Graf, Christian Gruen, Lukas Lewandowski, University of
 *         Konstanz
 *
 */
@Path(URLConstants.RESOURCEPATH)
public final class XMLResource extends AResource {


  /**
   * This method returns a collection of available resources. An available
   * resource can be either a particular XML resource or a collection
   * containing further XML resources.
   *
   * @param system
   *          The associated system with this request.
   * @param resource
   *          The name of the requested resource.
   * @param uri
   *          The context information due to the requested URI.
   * @return A collection of available resources.
   */
  @GET
  public Response getResource(
      @PathParam(URLConstants.SYSTEM) final String system,
      @PathParam(URLConstants.RESOURCE) final String resource,
      @Context final UriInfo uri) {
    return getResource(system, uri, resource);
  }

  /**
   * This method returns a collection of available resources. An available
   * resource can be either a particular XML resource or a collection
   * containing further XML resources.
   *
   * @param system
   *          The associated system with this request.
   * @param resource
   *          The name of the requested resource.
   * @param uri
   *          The context information due to the requested URI.
   *
   * @param sec The security context used for the request.
   * @return A collection of available resources.
   *//*
  @GET
  public Response getResource(
      @PathParam(URLConstants.SYSTEM) final String system,
      @PathParam(URLConstants.RESOURCE) final String resource,
      @Context final UriInfo uri, @Context final SecurityContext sec) {

    if(sec != null) {

      // the used security scheme (Basic, Digest, etc)
      final String authChannel = sec.getAuthenticationScheme();
      System.out.println("Authentication Scheme: " + authChannel);

      // the authenticated user (authentication has been performed by server)
      System.out.println("User principal: " + sec.getUserPrincipal().getName());

      // this checks if the user is in a specific role, BUT unfortuntelly it is
      // not working with Jetty, until now I have not found any workaround...
      if(sec.isUserInRole("user")) {
        System.out.println("User: " + sec.getUserPrincipal().getName()
            + ", Go Baby Go!!! ");
      }
    }
    return getResource(system, uri, resource);
  }
  */

  /**
   * This method will be called when a HTTP client sends a POST request to an
   * existing resource with 'application/query+xml' as Content-Type.
   *
   * @param system
   *          The implementation system.
   * @param resource
   *          The resource name.
   * @param input
   *          The input stream.
   * @return The {@link Response} which can be empty when no response is
   *         expected. Otherwise it holds the response XML file.
   */
  @POST
  @Consumes(APPLICATION_QUERY_XML)
  public Response postQuery(
      @PathParam(URLConstants.SYSTEM) final String system,
      @PathParam(URLConstants.RESOURCE) final String resource,
      final InputStream input) {

    return postQuery(system, input, resource);
  }

  /**
   * This method will be called when an HTTP client sends a POST request to an
   * existing resource to add a resource. Content-Type must be 'text/xml'.
   *
   * @param system
   *          The implementation system.
   * @param resource
   *          The resource name.
   * @param input
   *          The input stream.
   * @return The {@link Response} which can be empty when no response is
   *         expected. Otherwise it holds the response XML file.
   */
  @POST
  @Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  public Response postResource(
      @PathParam(URLConstants.SYSTEM) final String system,
      @PathParam(URLConstants.RESOURCE) final String resource,
      final InputStream input) {

    final JaxRx impl = Systems.getInstance(system);
    final String info = impl.add(input, new ResourcePath(resource));
    return Response.created(null).entity(info).build();
  }

  /**
   * This method will be called when a new XML file has to be stored within
   * the database. The user request will be forwarded to this method.
   * Afterwards it creates a response message with the 'created' HTTP status
   * code, if the storing has been successful.
   *
   * @param system
   *          The associated system with this request.
   * @param resource
   *          The name of the new resource.
   * @param xml
   *          The XML file as {@link InputStream} that will be stored.
   * @return The HTTP status code as response.
   */
  @PUT
  @Consumes({ MediaType.TEXT_XML, MediaType.APPLICATION_XML})
  public Response putResource(
      @PathParam(URLConstants.SYSTEM) final String system,
      @PathParam(URLConstants.RESOURCE) final String resource,
      final InputStream xml) {

    final JaxRx impl = Systems.getInstance(system);
    final String info = impl.update(xml, new ResourcePath(resource));
    return Response.created(null).entity(info).build();
  }

  /**
   * This method will be called when an HTTP client sends a DELETE request to
   * delete an existing resource.
   *
   * @param system
   *          The associated system with this request.
   * @param resource
   *          The name of the existing resource that has to be deleted.
   * @return The HTTP response code for this call.
   */
  @DELETE
  public Response deleteResource(
      @PathParam(URLConstants.SYSTEM) final String system,
      @PathParam(URLConstants.RESOURCE) final String resource) {

    final JaxRx impl = Systems.getInstance(system);
    final String info = impl.delete(new ResourcePath(resource));
    return Response.ok().entity(info).build();
  }
}
