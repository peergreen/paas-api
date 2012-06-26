/**
 * 
 */
package org.ow2.jonas.jpaas.server.ressources.manager.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ow2.jonas.jpaas.ejb.client.EnvironmentManagerClient;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManagerBeanException;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManagerRemote;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.manager.api.Environment;
import org.ow2.jonas.jpaas.server.ressources.exception.NotImplementedException;

/**
 * REST resource of type EnvironmentManager
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
@Path("environment")
public class EnvironmentManagerRessource {

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
	public Response createEnvironment(String environmentTemplateDescriptor) {
		System.out
		.println("[CO-PaaS-API]: Call createEnvironment(environmentTemplateDescriptor) on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the createEnvironment operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient.getProxy();
		try {
			Environment env = envManager
					.createEnvironment(environmentTemplateDescriptor);
			return Response.status(Response.Status.OK).entity(env)
					.type(MediaType.APPLICATION_XML_TYPE).build();
		} catch (EnvironmentManagerBeanException e) {
			System.out.println("EnvironmentManagerBeanException " + e);
			return Response.status(Response.Status.SERVICE_UNAVAILABLE)
					.type(MediaType.APPLICATION_XML_TYPE).build();
		}
	}

	/**
	 * Deletes an environment <br>
	 * Command: DELETE /environment/{envId}
	 * 
	 * @param envid
	 *            The environment's ID.
	 */
	@DELETE
	@Path("{envId}")
	public Response deleteEnvironment(@PathParam("envId") String envid) {
		System.out.println("[CO-PaaS-API]: Call deleteEnvironment(" + envid
				+ ") on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the deleteEnvironment operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient
				.getProxy();
		envManager.deleteEnvironment(envid);
		return Response.status(Response.Status.OK).build();
	}

	/**
	 * Finds the list of the available environments <br>
	 * Command: GET /environment
	 * 
	 * @return a list of the available environment descriptions.
	 */
	@GET
	public Response findEnvironments() {
		System.out
		.println("[CO-PaaS-API]: Call findEnvironments() on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the findEnvironments operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient
				.getProxy();
		// TODO check the return type (Environment or String)
		List<Environment> listEnv = new ArrayList<Environment>();
		listEnv = envManager.findEnvironments();
		throw new NotImplementedException();
		// return Response.status(Response.Status.OK).entity(new
		// GenericEntity<List<Environment>>(listEnv){}).build();
	}

	/**
	 * Starts an environment <br>
	 * Command: POST /environment/{envId}/action/start
	 * 
	 * @param envid
	 *            The environment's ID.
	 */
	@POST
	@Path("{envId}/action/start")
	public Response startsEnvironment(@PathParam("envId") String envid) {
		System.out.println("[CO-PaaS-API]: Call startsEnvironment(" + envid
				+ ") on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the startsEnvironment operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient
				.getProxy();
		Future<Environment> f = envManager.startEnvironment(envid);
		// TODO ask Bull how to create Task from Future
		throw new NotImplementedException();
		// TODO This will return a Task associated to the returned object Future
		// TODO return Response.status(Response.Status.OK).build();
	}

	/**
	 * Stops an environment <br>
	 * Command: POST /environment/{envId}/action/stop
	 * 
	 * @param envid
	 *            The environment's ID.
	 */
	@POST
	@Path("{envId}/action/stop")
	public Response stopsEnvironment(@PathParam("envId") String envid) {
		System.out.println("[CO-PaaS-API]: Call stopsEnvironment(" + envid
				+ ") on the JPAAS-ENVIRONMENT-MANAGER");
		throw new NotImplementedException();
		// TODO same as startsEnvironment
	}

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
	public Response deployApplication(@PathParam("envId") String envid,
			@PathParam("appId") String appid,
			@PathParam("versionId") String versionid,
			@PathParam("instanceId") String instanceid) {
		System.out.println("[CO-PaaS-API]: Call deployApplication(" + envid
				+ "," + appid + "," + versionid + "," + instanceid
				+ ") on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the deployApplication operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient
				.getProxy();
		envManager.deployApplication(envid, appid, versionid, instanceid);
		return Response.status(Response.Status.OK).build();
	}

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
	public Response undeployApplication(@PathParam("envId") String envid,
			@PathParam("appId") String appid,
			@PathParam("versionId") String versionid,
			@PathParam("instanceId") String instanceid) {
		System.out.println("[CO-PaaS-API]: Call undeployApplication(" + envid
				+ "," + appid + "," + versionid + "," + instanceid
				+ ") on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the undeployApplication operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient
				.getProxy();
		envManager.undeployApplication(envid, appid, versionid, instanceid);
		return Response.status(Response.Status.OK).build();
	}

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
	public Response getEnvironment(@PathParam("envId") String envid) {
		System.out.println("[CO-PaaS-API]: Call getEnvironment(" + envid
				+ ") on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the getEnvironment operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient
				.getProxy();
		String envDesc = envManager.getEnvironment(envid).getEnvDesc();
		return Response.status(Response.Status.OK).entity(envDesc).build();
	}

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
	public Response getDeployedApplicationVersionInstance(
			@PathParam("envId") String envid) {
		System.out
		.println("[CO-PaaS-API]: Call getDeployedApplicationVersionInstance("
				+ envid + ") on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the getDeployedApplicationVersionInstance operation from the EJB */
		EnvironmentManagerRemote envManager = EnvironmentManagerClient
				.getProxy();
		List<ApplicationVersionInstance> listInstances = envManager
				.getDeployedApplicationVersionInstance(envid);
		throw new NotImplementedException();
		// return Response.status(Response.Status.OK).entity(new
		// GenericEntity<List<ApplicationVersionInstance>>(listInstances){}).build();
	}
}