package org.jaxrx.core;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class validates XML documents against a specified XML schema.
 *
 * @author Lukas Lewandowski, University of Konstanz
 *
 */
public final class SchemaChecker {
	/**
	 * The validation schema.
	 */
	private final String xslSchema;

	/**
	 * Constructor.
	 *
	 * @param schema
	 *            schema to check
	 */
	public SchemaChecker(final String schema) {
		xslSchema = "/" + schema + ".xsd";
	}

	/**
	 * This method parses an XML input with a W3C DOM implementation and
	 * validates it then with the available XML schema.
	 *
	 * @param input
	 *            The input stream containing the XML query.
	 * @return The parsed XML source as {@link Document}.
	 */
	public Document check(final InputStream input) {
		Document document;
		try {
			final DocumentBuilder docBuilder = DocumentBuilderFactory
					.newInstance().newDocumentBuilder();
			document = docBuilder.parse(input);

			final InputStream is = getClass().getResourceAsStream(xslSchema);
			final Source source = new SAXSource(new InputSource(is));
			checkIsValid(document, source);
		} catch (final SAXException exce) {
			throw new JaxRxException(400, exce.getMessage());
		} catch (final ParserConfigurationException exce) {
			throw new JaxRxException(exce);
		} catch (final IOException exce) {
			throw new JaxRxException(exce);
		}
		return document;
	}

	/**
	 * This method checks the parsed document if it is valid to a given XML
	 * schema. If not, an exception is thrown
	 *
	 * @param document
	 *            The parsed document.
	 * @param source
	 *            The {@link String} representation of the XML schema file.
	 * @throws SAXException
	 *             if the document is invalid
	 * @throws IOException
	 *             if the input cannot be read
	 */
	private void checkIsValid(final Document document, final Source source)
			throws SAXException, IOException {

		final SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		final Schema schema = schemaFactory.newSchema(source);
		final Validator validator = schema.newValidator();
		validator.validate(new DOMSource(document));
	}
}
