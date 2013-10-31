package org.ow2.jonas.jpaas.api.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

@Path("upload")
public interface DeployableRest {

	@PUT
    @Path("app/{appId}/version/{versionId}/deployable/{deployableId}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Produces(MediaType.APPLICATION_XML)
	Response uploadDeployable(@PathParam("appId") String appId,
                              @PathParam("versionId") String versionId,
                              @PathParam("deployableId") String deployableId,
                              InputStream uploadedInputStream);


}