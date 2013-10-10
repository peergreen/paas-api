package org.ow2.jonas.jpaas.api.resources.manager.environment;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("environment")
public interface RestEnvironmentManager {

	// Environment management REST Operations
	/**
	 * Creates a new environment <br>
	 * Command: POST /environment/
	 * 
	 * @param environmentTemplateDescriptor
	 *            An environment template descriptor must be provided.
	 * @return An enriched environment template descriptor. The envID and Link
	 *         element will be added to the descriptor
	 */
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	Response createEnvironment(String environmentTemplateDescriptor);

	/**
	 * Deletes an environment <br>
	 * Command: DELETE /environment/{envId}
	 * 
	 * @param envid
	 *            The environment's ID.
	 */
	@DELETE
	@Path("{envId}")
	public abstract Response deleteEnvironment(@PathParam("envId") String envid);

	/**
	 * Finds the list of the available environments <br>
	 * Command: GET /environment
	 * 
	 * @return a list of the available environment descriptions.
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	public abstract Response findEnvironments();

	/**
	 * Starts an environment <br>
	 * Command: POST /environment/{envId}/action/start
	 * 
	 * @param envid
	 *            The environment's ID.
	 */
	@POST
	@Path("{envId}/action/start")
	public abstract Response startEnvironment(@PathParam("envId") String envid);

	/**
	 * Stops an environment <br>
	 * Command: POST /environment/{envId}/action/stop
	 * 
	 * @param envid
	 *            The environment's ID.
	 */
	@POST
	@Path("{envId}/action/stop")
	public abstract Response stopEnvironment(@PathParam("envId") String envid);

	/**
	 * Deploys an application instance on an available environment <br>
	 * Command: POST
	 * /environment/{envId}/action/deploy/app/{appId}/version/{versionId
	 * }/instance/{instanceId}
	 * 
	 * @param envid
	 *            The environment's ID.
	 * @param appid
	 *            The application's ID.
	 * @param versionid
	 *            The application's version ID.
	 * @param instanceid
	 *            The application's instance ID.
	 */
	@POST
	@Path("{envId}/action/deploy/app/{appId}/version/{versionId}/instance/{instanceId}")
	public abstract Response deployApplication(
			@PathParam("envId") String envid, @PathParam("appId") String appid,
			@PathParam("versionId") String versionid,
			@PathParam("instanceId") String instanceid);

	/**
	 * Undeploys an application instance on an available environment <br>
	 * Command: DELETE
	 * /environment/{envId}/action/undeploy/app/{appId}/version/{
	 * versionId}/instance/{instanceId}
	 * 
	 * @param envid
	 *            The environment's ID.
	 * @param appid
	 *            The application's ID.
	 * @param versionid
	 *            The application's version ID.
	 * @param instanceid
	 *            The application's instance ID.
	 */
	@POST
	@Path("{envId}/action/undeploy/app/{appId}/version/{versionId}/instance/{instanceId}")
	public abstract Response undeployApplication(
			@PathParam("envId") String envid, @PathParam("appId") String appid,
			@PathParam("versionId") String versionid,
			@PathParam("instanceId") String instanceid);

	/**
	 * Get the description of an environment <br>
	 * Command: GET /environment/{envId}
	 * 
	 * @param envid
	 *            The environment's ID.
	 * @return the description of the environment envid
	 */
	@GET
	@Path("{envId}")
	public abstract Response getEnvironment(@PathParam("envId") String envid);

	/**
	 * List the deployed application instances in an environment <br>
	 * Command: GET /environment/{envId}/app/
	 * 
	 * @param envid
	 *            The environment's ID.
	 * @return a list of application version instances in the environment envid
	 */
	@GET
	@Path("{envId}/app/")
	public abstract Response getDeployedApplicationVersionInstance(
			@PathParam("envId") String envid);

}