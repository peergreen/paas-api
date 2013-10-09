/**
 * 
 */
package org.ow2.jonas.jpaas.core.server.ressources.exception;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
public class NotImplementedException extends WebApplicationException {

	private static final long serialVersionUID = 1L;

	public NotImplementedException() {
		super(Response.status(404).entity("Not implemented yet")
				.type(MediaType.TEXT_PLAIN).build());
	}
}
