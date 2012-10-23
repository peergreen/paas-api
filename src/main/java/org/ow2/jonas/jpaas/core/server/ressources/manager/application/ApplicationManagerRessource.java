/**
 * 
 */
package org.ow2.jonas.jpaas.core.server.ressources.manager.application;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ow2.jonas.jpaas.api.ressources.manager.application.RestApplicationManager;
import org.ow2.jonas.jpaas.application.api.ApplicationManager;
import org.ow2.jonas.jpaas.application.api.ApplicationManagerBeanException;
import org.ow2.jonas.jpaas.core.ejb.client.ApplicationManagerClient;
import org.ow2.jonas.jpaas.core.server.xml.ApplicationVersionInstanceXML;
import org.ow2.jonas.jpaas.core.server.xml.ApplicationVersionXML;
import org.ow2.jonas.jpaas.core.server.xml.ApplicationXML;
import org.ow2.jonas.jpaas.manager.api.Application;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersion;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.core.server.xml.Error;



/**
 * REST resource of type ApplicationManager
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
@Path("app")
public class ApplicationManagerRessource implements RestApplicationManager {
	
	/**
	 * An element to display Errors
	 */
	private Error error = new Error();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response createApplication(String cloudApplicationDescriptor) {
		System.out.println("[CO-PaaS-API]: Call createApplication("
				+ cloudApplicationDescriptor
				+ ") on the JPAAS-APPLICATION-MANAGER");
		/* call the createApplication operation from the EJB */
		ApplicationManager envManager = ApplicationManagerClient.getProxy();
		Application app = null;
		try {
			app = envManager.createApplication(cloudApplicationDescriptor);

		} catch (ApplicationManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO add the other application attributes
		if (app != null) {
			ApplicationXML appXML = new ApplicationXML();

			appXML.setAppId(app.getAppId());
			appXML.setAppName(app.getName());
			return Response.status(Response.Status.OK)
					.entity(new GenericEntity<ApplicationXML>(appXML) {
					}).type(MediaType.APPLICATION_XML_TYPE).build();
		} else {
			error.setValue("Failed to create the Application!");
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(error).build();
		}
		// TODO: Retrieve the object returned by the JPAAS-APPLICATION-MANAGER
		// add the appID and Link element to the Manifest
		// TODO: This will be a file created based on the new manifest
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response createApplicationVersion(String applicationVersionDescriptor) {
		System.out.println("[CO-PaaS-API]: Call createApplication("
				+ applicationVersionDescriptor
				+ ") on the JPAAS-APPLICATION-MANAGER");
		/* call the createApplicationVersion operation from the EJB */
		ApplicationManager envManager = ApplicationManagerClient.getProxy();
		ApplicationVersion appVer=null;
		try {
			appVer = envManager
					.createApplicationVersion(applicationVersionDescriptor);
		} catch (ApplicationManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO add the other application attributes
				if (appVer != null) {
					ApplicationVersionXML appVerXML = new ApplicationVersionXML();

					appVerXML.setAppVerId(appVer.getAppId());
					appVerXML.setAppVerLabel(appVer.getVersionLabel());
					return Response.status(Response.Status.OK)
							.entity(new GenericEntity<ApplicationVersionXML>(appVerXML) {
							}).type(MediaType.APPLICATION_XML_TYPE).build();
				} else {
					error.setValue("Failed to create the Application Version!");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(error).build();
				}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response findApplications() {
		System.out
				.println("[CO-PaaS-API]: Call findApplications() on the JPAAS-APPLICATION-MANAGER");
		List<Application> listApp = new ArrayList<Application>();
		/* call the findApplications operation from the EJB */
		ApplicationManager appManager = ApplicationManagerClient.getProxy();
		listApp = appManager.findApplications();
		
		
		List<ApplicationXML> listAppsXML = new ArrayList<ApplicationXML>();

		if (listApp != null) {
			for (Application app : listApp) {
				ApplicationXML appXML = buildApplication(app);
				if (appXML != null) {
					listAppsXML.add(appXML);
				}
			}
		}
		return Response.status(Response.Status.OK)
				.entity(new GenericEntity<List<ApplicationXML>>(listAppsXML) {
				}).type(MediaType.APPLICATION_XML_TYPE).build();
		
	}

	@Override
	public Response findApplicationVersions(@PathParam("appId") String appid) {
		System.out.println("[CO-PaaS-API]: Call findApplicationVersions("
				+ appid + ") on the JPAAS-APPLICATION-MANAGER");
		List<ApplicationVersion> listAppVer = new ArrayList<ApplicationVersion>();
		/* call the findApplicationVersions(appid) operation from the EJB */
		ApplicationManager appManager = ApplicationManagerClient.getProxy();
		listAppVer = appManager.findApplicationVersion(appid);
		List<ApplicationVersionXML> listAppVersionsXML = new ArrayList<ApplicationVersionXML>();

		if (listAppVer != null) {
			for (ApplicationVersion appVersion : listAppVer) {
				ApplicationVersionXML appVersionXML = buildApplicationVersion(appVersion);
				if (appVersionXML != null) {
					listAppVersionsXML.add(appVersionXML);
				}
			}
		}
		return Response.status(Response.Status.OK)
				.entity(new GenericEntity<List<ApplicationVersionXML>>(listAppVersionsXML) {
				}).type(MediaType.APPLICATION_XML_TYPE).build();
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response findApplicationVersionInstances(
			@PathParam("appId") String appid,
			@PathParam("versionId") String versionid) {
		System.out.println("[CO-PaaS-API]: Call findApplicationVersions("
				+ appid + "," + versionid
				+ ") on the JPAAS-APPLICATION-MANAGER");
		List<ApplicationVersionInstance> listAppVerInstances = new ArrayList<ApplicationVersionInstance>();
		/* call the findApplicationVersionInstances operation from the EJB */
		ApplicationManager appManager = ApplicationManagerClient.getProxy();
		listAppVerInstances = appManager.findApplicationVersionsInstances(
				appid, versionid);
		List<ApplicationVersionInstanceXML> listAppVersionInstancesXML = new ArrayList<ApplicationVersionInstanceXML>();

		if (listAppVersionInstancesXML != null) {
			for (ApplicationVersionInstance appVersionInstance : listAppVerInstances) {
				ApplicationVersionInstanceXML appVersionInstanceXML = buildApplicationVersionInstance(appVersionInstance);
				if (appVersionInstanceXML != null) {
					listAppVersionInstancesXML.add(appVersionInstanceXML);
				}
			}
		}
		return Response.status(Response.Status.OK)
				.entity(new GenericEntity<List<ApplicationVersionInstanceXML>>(listAppVersionInstancesXML) {
				}).type(MediaType.APPLICATION_XML_TYPE).build();
		
	}

	@Override
	public Response startApplicationVersionInstance(String appid,
			String versionId, String instanceId) {
		
		ApplicationManager appManager = ApplicationManagerClient.getProxy();

		Future<ApplicationVersionInstance> instance = null;

		try {
			 instance = appManager.startApplicationVersionInstance(appid, versionId, instanceId);
		} catch (ApplicationManagerBeanException e1) {
			e1.printStackTrace();
		}

		ApplicationVersionInstanceXML appVersionInstanceXML=null;
        try {
	        appVersionInstanceXML = buildApplicationVersionInstance(instance.get());
        } catch (InterruptedException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        } catch (ExecutionException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
        }
		
		return Response.status(Response.Status.OK)
				.entity(appVersionInstanceXML).type(MediaType.APPLICATION_XML_TYPE).build();
		}

	@Override
	public Response describeApplication(String appId) {
		ApplicationManager appManager = ApplicationManagerClient.getProxy();

		Application app = null;
		app = appManager.getApplication(appId);

		ApplicationXML appXML = buildApplication(app);
		
		return Response.status(Response.Status.OK)
				.entity(appXML).type(MediaType.APPLICATION_XML_TYPE).build();
	}

	@Override
	public Response deleteApplication(String appId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response deleteApplications() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response stopApplicationVersionInstance(String appid,
			String versionid, String instanceid) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response createApplicationVersionInstance(String appId,
			String applicationVersionInstanceDescriptor) {
		System.out.println("[CO-PaaS-API]: Call createApplicationVersionInstance("
				+ applicationVersionInstanceDescriptor
				+ ") on the JPAAS-APPLICATION-MANAGER");
		/* call the createApplicationVersionInstance operation from the EJB */
		ApplicationManager envManager = ApplicationManagerClient.getProxy();
		ApplicationVersionInstance appVerIns=null;
		try {
			appVerIns = envManager.createApplicationVersionInstance(appId, applicationVersionInstanceDescriptor);
		} catch (ApplicationManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO add the other application attributes
				if (appVerIns != null) {
					ApplicationVersionInstanceXML appVerInsXML = new ApplicationVersionInstanceXML();

					appVerInsXML.setAppId(appVerIns.getAppId());
					appVerInsXML.setInstanceId(appVerIns.getInstanceId());
					appVerInsXML.setInstanceName(appVerIns.getInstanceName());
					return Response.status(Response.Status.OK)
							.entity(new GenericEntity<ApplicationVersionInstanceXML>(appVerInsXML) {
							}).type(MediaType.APPLICATION_XML_TYPE).build();
				} else {
					error.setValue("Failed to create the Application Version Instance!");
					return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
							.entity(error).build();
				}
	}
	
	/**
	 * builds the XML representation for an Application
	 * 
	 * @param env
	 *            The {@link Application} object
	 * @return an {@link Application} XML representation
	 */
	private ApplicationXML buildApplication(final Application app) {
		ApplicationXML xmlApp = new ApplicationXML();
		
		xmlApp.setAppId(app.getAppId());
		xmlApp.setAppName(app.getName());

		return xmlApp;
	}
	
	/**
	 * builds the XML representation for an ApplicationVersion
	 * 
	 * @param env
	 *            The {@link Application} object
	 * @return an {@link Application} XML representation
	 */
	private ApplicationVersionXML buildApplicationVersion(final ApplicationVersion appVersion) {
		ApplicationVersionXML xmlVersionApp = new ApplicationVersionXML();
		
		xmlVersionApp.setAppId(appVersion.getAppId());
		xmlVersionApp.setAppVerId(appVersion.getVersionId());
		xmlVersionApp.setAppVerLabel(appVersion.getVersionId());

		return xmlVersionApp;
	}

	
	/**
	 * builds the XML representation for an ApplicationVersionInstance
	 * 
	 * @param env
	 *            The {@link Application} object
	 * @return an {@link Application} XML representation
	 */
	private ApplicationVersionInstanceXML buildApplicationVersionInstance(final ApplicationVersionInstance appVersionInstance) {
		ApplicationVersionInstanceXML xmlInstanceVersionApp = new ApplicationVersionInstanceXML();
		
		xmlInstanceVersionApp.setAppId(appVersionInstance.getAppId());
		xmlInstanceVersionApp.setVersionID(appVersionInstance.getVersionId());
		xmlInstanceVersionApp.setInstanceId(appVersionInstance.getInstanceId());
		xmlInstanceVersionApp.setInstanceName(appVersionInstance.getInstanceName());
		xmlInstanceVersionApp.setState(appVersionInstance.getStateStr());

		
		return xmlInstanceVersionApp;
	}

}
