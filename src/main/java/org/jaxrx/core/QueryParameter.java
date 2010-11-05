package org.jaxrx.core;

/**
 * This class lists all available JAX-RX query parameters.
 *
 * @author Sebastian Graf, Christian Gruen, Lukas Lewandowski, University of
 *         Konstanz
 *
 */
public enum QueryParameter {
	/** Query to process. */
	QUERY,
	/** Database command to process. */
	COMMAND,
	/** Query file to process. */
	RUN,

	/** Choose revision to work on. */
	REVISION,
	/** Serialization parameters. */
	OUTPUT,
	/** XSL style sheet to apply to the result. */
	XSL,
	/** Flag for wrapping results in XML elements. */
	WRAP,
	/** First result to print. */
	START,
	/** Number of results to print. */
	COUNT,
  /** External variable. */
  VAR;
}
