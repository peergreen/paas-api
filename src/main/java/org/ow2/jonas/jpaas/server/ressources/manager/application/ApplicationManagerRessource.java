/**
 * 
 */
package org.ow2.jonas.jpaas.server.ressources.manager.application;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.ow2.jonas.jpaas.application.ApplicationManagerRemote;
import org.ow2.jonas.jpaas.ejb.client.ApplicationManagerClient;
import org.ow2.jonas.jpaas.manager.api.Application;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersion;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.server.ressources.exception.NotImplementedException;

/**
 * REST resource of type ApplicationManager
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
@Path("app")
public class ApplicationManagerRessource {
	/**
	 * Creates a new application. If the application is multitenant, it will be
	 * accessible for all tenant Command: POST /app
	 * 
	 * @param cloudApplicationDescriptor
	 *            A Cloud Application Descriptor (manifest) must be provided.
	 * @return XML file An: enriched Cloud Application Descriptor (manifest).
	 *         The appID and Link element will be added to the Manifest.
	 */
	@POST
	@Consumes("application/xml")
	@Produces("application/xml")
	public Response createApplication(String cloudApplicationDescriptor) {
		System.out.println("[CO-PaaS-API]: Call createApplication("
				+ cloudApplicationDescriptor
				+ ") on the JPAAS-APPLICATION-MANAGER");
		/* call the createApplication operation from the EJB */
		ApplicationManagerRemote envManager = ApplicationManagerClient
				.getProxy();
		Application app = envManager
				.createApplication(cloudApplicationDescriptor);
		// TODO: Retrieve the object returned by the JPAAS-APPLICATION-MANAGER
		// add the appID and Link element to the Manifest
		// TODO: This will be a file created based on the new manifest
		throw new NotImplementedException();
		// TODO Transform the app returned by createApplication to an XML File
		// return
		// Response.status(Response.Status.OK).entity(app).type(MediaType.APPLICATION_XML_TYPE).build();
	}

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
	 *         appID and Link element will be added to the Manifest.
	 */
	@POST
	@Path("{appId}/version")
	@Consumes("application/xml")
	@Produces("application/xml")
	public Response createApplicationVersion(String applicationVersionDescriptor) {
		System.out.println("[CO-PaaS-API]: Call createApplication("
				+ applicationVersionDescriptor
				+ ") on the JPAAS-APPLICATION-MANAGER");
		/* call the createApplicationVersion operation from the EJB */
		ApplicationManagerRemote envManager = ApplicationManagerClient
				.getProxy();
		ApplicationVersion appVer = envManager
				.createApplicationVersion(applicationVersionDescriptor);
		throw new NotImplementedException();
		// TODO Transform the appVer returned by createApplicationVersion to an
		// XML File
		// return
		// Response.status(Response.Status.OK).entity(appVer).type(MediaType.APPLICATION_XML_TYPE).build();
	}

	/**
	 * List applications Command: GET /app/
	 * 
	 * @return A List of available applications
	 */
	@GET
	public Response findApplications() {
		System.out
		.println("[CO-PaaS-API]: Call findApplications() on the JPAAS-APPLICATION-MANAGER");
		List<Application> listApp = new ArrayList<Application>();
		/* call the findApplications operation from the EJB */
		ApplicationManagerRemote envManager = ApplicationManagerClient
				.getProxy();
		listApp = envManager.findApplications();
		throw new NotImplementedException();
		// TODO return Response.status(Response.Status.OK).entity(new
		// GenericEntity<List<Application>>(listApp){}).build();
	}

	/**
	 * List application versions Command: GET /app/{appId}/version
	 * 
	 * @param appid
	 *            The application's ID
	 * @return A list of available application's versions
	 */
	@GET
	@Path("{appId}/version")
	public Response findApplicationVersions(@PathParam("appId") String appid) {
		System.out.println("[CO-PaaS-API]: Call findApplicationVersions("
				+ appid + ") on the JPAAS-APPLICATION-MANAGER");
		List<ApplicationVersion> listAppVer = new ArrayList<ApplicationVersion>();
		/* call the findApplicationVersions(appid) operation from the EJB */
		ApplicationManagerRemote envManager = ApplicationManagerClient
				.getProxy();
		listAppVer = envManager.findApplicationVersion(appid);
		throw new NotImplementedException();
		// TODO return Response.status(Response.Status.OK).entity(new
		// GenericEntity<List<ApplicationVersion>>(listAppVer){}).build();
	}

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
	public Response findApplicationVersionInstances(
			@PathParam("appId") String appid,
			@PathParam("versionId") String versionid) {
		System.out.println("[CO-PaaS-API]: Call findApplicationVersions("
				+ appid + "," + versionid
				+ ") on the JPAAS-APPLICATION-MANAGER");
		List<ApplicationVersionInstance> listAppVerInstances = new ArrayList<ApplicationVersionInstance>();
		/* call the findApplicationVersionInstances operation from the EJB */
		ApplicationManagerRemote envManager = ApplicationManagerClient
				.getProxy();
		listAppVerInstances = envManager.findApplicationVersionsInstances(
				appid, versionid);
		throw new NotImplementedException();
		// TODO return Response.status(Response.Status.OK).entity(new
		// GenericEntity<List<ApplicationVersionInstance>>(listAppVerInstances){}).build();
	}

}
