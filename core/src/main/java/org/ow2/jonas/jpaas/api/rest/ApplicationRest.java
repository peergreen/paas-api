package org.ow2.jonas.jpaas.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("app")
public interface ApplicationRest {

	/**
	 * Creates a new application. If the application is multitenant, it will be
	 * accessible for all tenant Command: POST /app
	 * 
	 * @param cloudApplicationDescriptor
	 *            A Cloud Application Descriptor (manifest) must be provided.
	 * @return XML file An: enriched Cloud Application Descriptor (manifest).
	 *         The appID and LinkXML element will be added to the Manifest.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	Response createApplication(String cloudApplicationDescriptor);

	/**
	 * Creates a new version with either a file in attachment (Content-Type:
	 * multipart/form-data). or a url. The supported artefacts are ear, bundle,
	 * war, ejbjar or a zip (war dir). A Cloud Application Version Descriptor
	 * must be provided. <br>
	 * Command: POST /app/{appId}/version
	 * 
	 * @param applicationVersionDescriptor
	 *            A Cloud Application Version Descriptor must be provided.
	 * @return XML file An: enriched Cloud Application Version Descriptor. The
	 *         appID and LinkXML element will be added to the Manifest.
	 */
	@POST
	@Path("{appId}/version")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	Response createApplicationVersion(@PathParam("appId") String appId, String applicationVersionDescriptor);

	/**
	 * Command: POST /app/{appId}/version/{versionId}/instance
	 * 
	 * @param applicationVersionInstanceDescriptor
	 *            A Cloud Application Version Descriptor must be provided.
	 * @return XML file An: TODO
	 */
	
	
	@POST
	@Path("{appId}/version/{versionId}/instance")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	Response createApplicationVersionInstance(@PathParam("appId") String appId, @PathParam("versionId") String versionId,
            String applicationVersionInstanceDescriptor);
	
	
	/**
	 * List applications Command: GET /app/
	 * 
	 * @return A List of available applications
	 */
	@GET
	Response findApplications();

	/**
	 * List application versions Command: GET /app/{appId}/version
	 * 
	 * @param appId
	 *            The application's ID
	 * @return A list of available application's versions
	 */
	@GET
	@Path("{appId}/version")
	Response findApplicationVersions(@PathParam("appId") String appId);

	/**
	 * List application version instances Command: GET
	 * /app/{appId}/version/{versionId}/instance
	 * 
	 * @param appId
	 *            The application's ID
	 * @param versionId
	 *            The application version's ID
	 * @return A list of available application versions' instances
	 */

	@GET
	@Path("{appId}/version/{versionId}/instance")
	Response findApplicationVersionInstances(@PathParam("appId") String appId,
			@PathParam("versionId") String versionId);
	
	/**
	 * Start an application version instance Command: POST
	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
	 * 
	 * @param appId
	 *            The application's ID
	 * @param versionId
	 *            The application version's ID
	 * @param instanceId
	 *            The application instance's ID	            
	 * @return XML Task with TaskId
	 */

	@POST
	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/start")
	Response startApplicationVersionInstance(@PathParam("appId") String appId,
			@PathParam("versionId") String versionId,@PathParam("instanceId") String instanceId);
	
	/**
	 * Stop an application version instance Command: POST
	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
	 * 
	 * @param appId
	 *            The application's ID
	 * @param versionId
	 *            The application version's ID
	 * @param instanceId
	 *            The application instance's ID	            
	 * @return \\TODO
	 */

	@POST
	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/stop")
	Response stopApplicationVersionInstance(@PathParam("appId") String appId,
			@PathParam("versionId") String versionId,@PathParam("instanceId") String instanceId);

	/**
	 * Describe application.
	 * Command: GET /app/{appId}
	 * 
	 * @param appId
	 *           The application's ID
	 * @return XML file An: The Cloud Application Descriptor
	 */
		
	@GET
	@Path("{appId}")
	@Produces(MediaType.APPLICATION_XML)
	Response describeApplication(@PathParam("appId") String appId);

    /**
     * Describe application version
     * Command: GET /app/{appId}/version/{versionId}
     *
     * @param appId
     *           The application's ID
     * @param versionId
     *           The application's version ID
     * @return XML file An: The Cloud Application Descriptor
     */
    @GET
    @Path("{appId}/version/{versionId}")
    @Produces(MediaType.APPLICATION_XML)
    Response describeApplicationVersion(@PathParam("appId") String appId, @PathParam("versionId") String versionId);

    /**
     * Describe application version instance
     * Command: GET /app/{appId}/version/{versionId}/instance/{instanceId}
     *
     * @param appId
     *           The application's ID
     * @param versionId
     *           The application's version ID
     * @param instanceId
     *           The application's version instance ID
     * @return XML file An: The Cloud Application Descriptor
     */
    @GET
    @Path("{appId}/version/{versionId}/instance/{instanceId}")
    @Produces(MediaType.APPLICATION_XML)
    Response describeApplicationVersionInstance(@PathParam("appId") String appId, @PathParam("versionId") String versionId, @PathParam("instanceId") String instanceId);

    /**
	 * Delete application. Removes all existing versions.
	 * Command: DELETE /app/{appId}
	 * 
	 * @param appId
	 *           The application's ID
	 * @return HTTP status
	 */
	@DELETE
	@Path("{appId}")
	@Produces(MediaType.APPLICATION_XML)
	Response deleteApplication(@PathParam("appId") String appId);

    /**
     * Delete application version. Removes all existing instances.
     * Command: DELETE /app/{appId}
     *
     * @param appId
     *           The application's ID
     * @param versionId
     *           The application's version ID
     * @return HTTP status
     */
    @DELETE
    @Path("{appId}/version/{versionId}")
    @Produces(MediaType.APPLICATION_XML)
    Response deleteApplicationVersion(@PathParam("appId") String appId, @PathParam("versionId") String versionId);

    /**
     * Delete application version instance.
     * Command: DELETE /app/{appId}
     *
     * @param appId
     *           The application's ID
     * @param versionId
     *           The application's version ID
     * @param instanceId
     *           The application's version instance ID
     * @return HTTP status
     */
    @DELETE
    @Path("{appId}/version/{versionId}/instance/{instanceId}")
    @Produces(MediaType.APPLICATION_XML)
    Response deleteApplicationVersionInstance(@PathParam("appId") String appId, @PathParam("versionId") String versionId, @PathParam("instanceId") String instanceId);

    /**
   	 * Scale up an application version instance Command: POST
   	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
   	 *
   	 * @param appId
   	 *            The application's ID
   	 * @param versionId
   	 *            The application version's ID
   	 * @param instanceId
   	 *            The application instance's ID
   	 * @return XML Task with TaskId
   	 */

   	@POST
   	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/scaleup")
   	Response scaleUp(@PathParam("appId") String appId,
   			@PathParam("versionId") String versionId,@PathParam("instanceId") String instanceId);

    /**
   	 * Scale down an application version instance Command: POST
   	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
   	 *
   	 * @param appId
   	 *            The application's ID
   	 * @param versionId
   	 *            The application version's ID
   	 * @param instanceId
   	 *            The application instance's ID
   	 * @return XML Task with TaskId
   	 */

   	@POST
   	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/scaledown")
   	Response scaleDown(@PathParam("appId") String appId,
   			@PathParam("versionId") String versionId,@PathParam("instanceId") String instanceId);
}