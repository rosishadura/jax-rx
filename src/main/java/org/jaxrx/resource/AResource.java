package org.jaxrx.resource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriInfo;
import org.jaxrx.JaxRx;
import org.jaxrx.core.JaxRxException;
import org.jaxrx.core.QueryParameter;
import org.jaxrx.core.ResourcePath;
import org.jaxrx.core.SchemaChecker;
import org.jaxrx.core.Systems;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This is an abstract resource class, which assembles common methods from
 * resource implementations.
 *
 * @author Sebastian Graf, Christian Gruen, Lukas Lewandowski, University of
 *         Konstanz
 */
abstract class AResource {
	/**
	 * Content type for query expressions.
	 */
	protected static final String APPLICATION_QUERY_XML = "application/query+xml";

	/**
	 * Returns a stream output, depending on the query parameters.
	 *
	 * @param impl
	 *            implementation
	 * @param path
	 *            path info
	 *
	 * @return parameter map
	 */
	private StreamingOutput createOutput(final JaxRx impl,
			final ResourcePath path) {

		// check for command parameter
		String qu = path.getValue(QueryParameter.COMMAND);
		if (qu != null) {
			return impl.command(qu, path);
		}

		// check for run parameter
		qu = path.getValue(QueryParameter.RUN);
		if (qu != null) {
			return impl.run(qu, path);
		}

		// check for query parameter
		qu = path.getValue(QueryParameter.QUERY);
		if (qu != null) {
			return impl.query(qu, path);
		}

		// no parameter found
		return impl.get(path);
	}

	/**
	 * Returns a result, depending on the query parameters.
	 *
	 * @param impl
	 *            implementation
	 * @param path
	 *            path info
	 *
	 * @return parameter map
	 */
	Response createResponse(final JaxRx impl, final ResourcePath path) {
		final StreamingOutput out = createOutput(impl, path);

		final boolean wrap = path.getValue(QueryParameter.WRAP) == null
				|| path.getValue(QueryParameter.WRAP).equals("yes");

		final String type = wrap ? MediaType.TEXT_XML : MediaType.TEXT_PLAIN;
		return Response.ok(out, type).build();

	}

	/**
	 * Extracts and returns query parameters from the specified map.
	 * If a parameter is specified multiple times, its values will be
	 * separated with tab characters.
	 *
	 * @param uri
	 *            uri info with query parameters
	 * @param jaxrx
	 *            JAX-RX implementation
	 * @return The parameters as {@link Map}.
	 */
	protected Map<QueryParameter, String> getParameters(final UriInfo uri,
			final JaxRx jaxrx) {

		final MultivaluedMap<String, String> params = uri.getQueryParameters();
		final Map<QueryParameter, String> newParam = createMap();
		final Set<QueryParameter> impl = jaxrx.getParameters();

		for (final String key : params.keySet()) {
		  for(final String s : params.get(key)) {
	      addParameter(key, s, newParam, impl);
		  }
		}
		return newParam;
	}

	/**
	 * Extracts and returns query parameters, variables, and output options
	 * from the specified document instance. The names, values, and optional
	 * data types of variables are separated with the special character code
	 * {@code '\2'}. 
	 *
	 * @param doc
	 *            The XML {@link Document} containing the XQuery XML post
	 *            request.
	 * @param jaxrx
	 *            current implementation
	 * @return The parameters as {@link Map}.
	 */
	protected Map<QueryParameter, String> getParameters(final Document doc,
			final JaxRx jaxrx) {

		final Map<QueryParameter, String> newParams = createMap();
		final Set<QueryParameter> impl = jaxrx.getParameters();

		// store name of root element and contents of text node
		final String root = doc.getDocumentElement().getNodeName();
		final QueryParameter ep = QueryParameter.valueOf(root.toUpperCase());
		newParams.put(ep, doc.getElementsByTagName("text").item(0)
				.getTextContent());

    // add additional parameters
    NodeList props = doc.getElementsByTagName("parameter");
    for (int i = 0; i < props.getLength(); i++) {
      final NamedNodeMap nnm = props.item(i).getAttributes();
      addParameter(nnm.getNamedItem("name").getNodeValue(), nnm
          .getNamedItem("value").getNodeValue(), newParams, impl);
    }
    // add additional variables; tab characters are used as delimiters
    props = doc.getElementsByTagName("variable");
    for (int i = 0; i < props.getLength(); i++) {
      final NamedNodeMap nnm = props.item(i).getAttributes();
      // use \2 as delimiter for keys, values, and optional data types
      String val = nnm.getNamedItem("name").getNodeValue() + '\2' +
                   nnm.getNamedItem("value").getNodeValue();
      final Node type = nnm.getNamedItem("type");
      if(type != null) val += '\2' + type.getNodeValue();
      addParameter("var", val, newParams, impl);
    }
    // add additional variables; tab characters are used as delimiters
    props = doc.getElementsByTagName("output");
    for (int i = 0; i < props.getLength(); i++) {
      final NamedNodeMap nnm = props.item(i).getAttributes();
      // use \2 as delimiter for keys, values, and optional data types
      String val = nnm.getNamedItem("name").getNodeValue() + '=' +
                   nnm.getNamedItem("value").getNodeValue();
      addParameter("output", val, newParams, impl);
    }
		return newParams;
	}

  /**
   * Adds a key/value combination to the parameter map.
   * Multiple output parameters are separated with commas.
   *
   * @param key
   *            The parameter key
   * @param value
   *            The parameter value
   * @param newParams
   *            New query parameter map
   * @param impl
   *            Implementation parameters
   */
  private void addParameter(final String key, final String value,
      final Map<QueryParameter, String> newParams,
      final Set<QueryParameter> impl) {

    try {
      final QueryParameter ep = QueryParameter.valueOf(key.toUpperCase());
      if (!impl.contains(ep)) {
        throw new JaxRxException(400, "Parameter '" + key
            + "' is not supported by the implementation.");
      }

      // append multiple parameters
      final String old = newParams.get(ep);
      // skip multiple key/value combinations if different to OUTPUT
      if(old != null && ep != QueryParameter.OUTPUT) return;

      // use \1 as delimiter for multiple values
      newParams.put(ep, old == null ? value : old + ',' + value);
    } catch (final IllegalArgumentException ex) {
      throw new JaxRxException(400, "Parameter '" + key + "' is unknown.");
    }
  }

	/**
	 * Returns a fresh parameter map. This map contains all parameters as
	 * defaults which have been specified by the user via system properties with
	 * the pattern "org.jaxrx.parameter.KEY" as key.
   *
	 * @return parameter map
	 */
	private Map<QueryParameter, String> createMap() {
		final Map<QueryParameter, String> params = new HashMap<QueryParameter, String>();

		final Properties props = System.getProperties();
		for (final Map.Entry<Object, Object> set : props.entrySet()) {
			final String key = set.getKey().toString();
			final String up = key.replace("org.jaxrx.parameter.", "");
			if (key.equals(up))
				continue;
			try {
				params.put(QueryParameter.valueOf(up.toUpperCase()), set
						.getValue().toString());
			} catch (final IllegalArgumentException ex) { /* ignore */
			}
		}

		return params;
	}

  /**
   * This method will be called when a HTTP client sends a POST request to an
   * existing resource with 'application/query+xml' as Content-Type.
   *
   * @param system
   *            The implementation system.
   * @param input
   *            The input stream.
   * @param resource
   *            The resource
   * @return The {@link Response} which can be empty when no response is
   *         expected. Otherwise it holds the response XML file.
   */
  public Response postQuery(final String system, final InputStream input,
      final String resource) {

    final JaxRx impl = Systems.getInstance(system);
    final Document doc = new SchemaChecker("post").check(input);
    final Map<QueryParameter, String> param = getParameters(doc, impl);
    final ResourcePath path = new ResourcePath(resource, param);
    return createResponse(impl, path);
  }

  /**
   * This method will be called when a HTTP client sends a POST request to an
   * existing resource with 'application/query+xml' as Content-Type.
   *
   * @param system
   *            The implementation system.
   * @param uri
   *            The context information due to the requested URI.
   * @param resource
   *            The resource
   * @return The {@link Response} which can be empty when no response is
   *         expected. Otherwise it holds the response XML file.
   */
  public Response getResource(final String system, final UriInfo uri,
      final String resource) {

    final JaxRx impl = Systems.getInstance(system);
    final Map<QueryParameter, String> param = getParameters(uri, impl);
    final ResourcePath path = new ResourcePath(resource, param);
    return createResponse(impl, path);
  }
}
