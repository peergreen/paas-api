package org.ow2.jonas.jpaas.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
	@Consumes("application/xml")
	@Produces("application/xml")
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
	@Consumes("application/xml")
	@Produces("application/xml")
	Response createApplicationVersion(@PathParam("appId") String appid, String applicationVersionDescriptor);

	/**
	 * TODO <br>
	 * Command: POST /app/{appId}/version/{versionId}/instance
	 * 
	 * @param applicationVersionInstanceDescriptor
	 *            A Cloud Application Version Descriptor must be provided.
	 * @return XML file An: TODO
	 */
	
	
	@POST
	@Path("{appId}/version/{versionId}/instance")
	@Consumes("application/xml")
	@Produces("application/xml")
	Response createApplicationVersionInstance(@PathParam("appId") String appid, @PathParam("versionId") String versionid,
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
	 * @param appid
	 *            The application's ID
	 * @return A list of available application's versions
	 */
	@GET
	@Path("{appId}/version")
	Response findApplicationVersions(@PathParam("appId") String appid);

	/**
	 * List application version instances Command: GET
	 * /app/{appId}/version/{versionId}
	 * 
	 * @param appid
	 *            The application's ID
	 * @param versionid
	 *            The application version's ID
	 * @return A list of available application versions' instances
	 */

	@GET
	@Path("{appId}/version/{versionId}")
	Response findApplicationVersionInstances(@PathParam("appId") String appid,
			@PathParam("versionId") String versionid);
	
	/**
	 * Start an application version instance Command: POST
	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
	 * 
	 * @param appid
	 *            The application's ID
	 * @param versionid
	 *            The application version's ID
	 * @param instanceid
	 *            The application instance's ID	            
	 * @return XML Task with TaskId
	 */

	@POST
	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/start")
	Response startApplicationVersionInstance(@PathParam("appId") String appid,
			@PathParam("versionId") String versionid,@PathParam("instanceId") String instanceid);
	
	/**
	 * Stop an application version instance Command: POST
	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
	 * 
	 * @param appid
	 *            The application's ID
	 * @param versionid
	 *            The application version's ID
	 * @param instanceid
	 *            The application instance's ID	            
	 * @return \\TODO
	 */

	@POST
	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/stop")
	Response stopApplicationVersionInstance(@PathParam("appId") String appid,
			@PathParam("versionId") String versionid,@PathParam("instanceId") String instanceid);
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
	@Produces("application/xml")
	Response describeApplication(@PathParam("appId") String appId);
	
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
	@Produces("application/xml")
	Response deleteApplication(@PathParam("appId") String appId);

	/**
	 * Delete applications. Removes all existing applications and all their versions.
	 * Command: DELETE /app
	 * 
	 * @return HTTP status
	 */
		
	@DELETE
	@Produces("application/xml")
	Response deleteApplications();

    /**
   	 * Scale up an application version instance Command: POST
   	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
   	 *
   	 * @param appid
   	 *            The application's ID
   	 * @param versionid
   	 *            The application version's ID
   	 * @param instanceid
   	 *            The application instance's ID
   	 * @return XML Task with TaskId
   	 */

   	@POST
   	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/scaleup")
   	Response scaleUp(@PathParam("appId") String appid,
   			@PathParam("versionId") String versionid,@PathParam("instanceId") String instanceid);

    /**
   	 * Scale down an application version instance Command: POST
   	 * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
   	 *
   	 * @param appid
   	 *            The application's ID
   	 * @param versionid
   	 *            The application version's ID
   	 * @param instanceid
   	 *            The application instance's ID
   	 * @return XML Task with TaskId
   	 */

   	@POST
   	@Path("{appId}/version/{versionId}/instance/{instanceId}/action/scaledown")
   	Response scaleDown(@PathParam("appId") String appid,
   			@PathParam("versionId") String versionid,@PathParam("instanceId") String instanceid);
}