/**
 * 
 */
package org.ow2.jonas.jpaas.server.ressources.manager.environment;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ow2.jonas.jpaas.server.xml.*;
import org.ow2.jonas.jpaas.server.xml.Error;

import org.ow2.jonas.jpaas.ejb.client.EnvironmentManagerClient;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManagerBeanException;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManager;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.manager.api.Deployable;
import org.ow2.jonas.jpaas.manager.api.Environment;
import org.ow2.jonas.jpaas.manager.api.Node;
import org.ow2.jonas.jpaas.manager.api.Relationship;
import org.ow2.jonas.jpaas.manager.api.Topology;

import org.ow2.jonas.jpaas.server.ressources.exception.NotImplementedException;
import org.ow2.jonas.jpaas.server.task.CreateEnvironmentTask;
import org.ow2.jonas.jpaas.server.task.ITask;
import org.ow2.jonas.jpaas.server.task.IWorkTask;
import org.ow2.jonas.jpaas.server.task.Status;
import org.ow2.jonas.jpaas.server.task.TaskException;
import org.ow2.jonas.jpaas.server.task.TaskPool;
import org.ow2.jonas.jpaas.server.task.WorkTask;
import org.ow2.jonas.jpaas.server.xml.EnvironmentXML;

/**
 * REST resource of type EnvironmentManager
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
@Path("environment")
public class EnvironmentManagerRessource {

	private TaskPool taskPool;

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
		System.out.println("Test-Display" + environmentTemplateDescriptor);
		
		/* call the createEnvironment operation from the EJB */
		
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
		try {
			Future<Environment> env = envManager.createEnvironment(environmentTemplateDescriptor);
		} catch (EnvironmentManagerBeanException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// Create the CreateEnvironment Task
		//TODO this is a test version
		ITask task = new CreateEnvironmentTask(environmentTemplateDescriptor);
		final Long idTask = getNextId();
		IWorkTask workTask;
		try {
			workTask = submitTask(task, idTask, environmentTemplateDescriptor);
		} catch (TaskException e) {
			System.out.println("Cannot submit task " + idTask);
			Error error = new Error();
			error.setValue("Cannot submit task " + idTask + ".\n" + e);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(error).build();
		}
		TaskXML xmlTask = buildCreateEnvironmentTask(idTask, workTask, CreateEnvironmentTask.TASK_NAME);

        return Response
                .status(Response.Status.ACCEPTED)
                .entity(xmlTask)
                .type(MediaType.APPLICATION_XML_TYPE)
                .build();
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
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
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
	@Produces(MediaType.APPLICATION_XML)
	public Response findEnvironments() {
		System.out
				.println("[CO-PaaS-API]: Call findEnvironments() on the JPAAS-ENVIRONMENT-MANAGER");
		/* call the findEnvironments operation from the EJB */
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();

		List<EnvironmentXML> listEnvsXML = new ArrayList<EnvironmentXML>();
		List<Environment> listEnvs = envManager.findEnvironments();

		if (listEnvs != null) {
			for (Environment listEnv : listEnvs) {
				EnvironmentXML env = buildEnvironment(listEnv);
				if (env != null) {
					listEnvsXML.add(env);
				}
			}
		}
		// throw new NotImplementedException();
		return Response.status(Response.Status.OK)
				.entity(new GenericEntity<List<EnvironmentXML>>(listEnvsXML) {
				}).type(MediaType.APPLICATION_XML_TYPE).build();
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
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
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
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
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
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
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
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
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
		EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
		List<ApplicationVersionInstance> listInstances = envManager
				.getDeployedApplicationVersionInstance(envid);
		throw new NotImplementedException();
		// return Response.status(Response.Status.OK).entity(new
		// GenericEntity<List<ApplicationVersionInstance>>(listInstances){}).build();
	}

	/**
	 * @param env
	 *            The {@link Environment} object
	 * @return an {@link Environment} XML representation
	 */
	private EnvironmentXML buildEnvironment(final Environment env) {
		EnvironmentXML xmlEnv = new EnvironmentXML();

		TopologyXML xmlTopo = buildTopology(env);
		List<ApplicationVersionInstanceXML> listApplicationVersionInstanceXML = buildlistApplicationVersionInstance(env);

		xmlEnv.setEnvDesc(env.getEnvDesc());
		xmlEnv.setEnvId(env.getEnvId());
		xmlEnv.setEnvListApplicationVersionInstance(listApplicationVersionInstanceXML);
		xmlEnv.setEnvName(env.getEnvName());
		xmlEnv.setEnvState(env.getState());
		xmlEnv.setEnvTopology(xmlTopo);

		return xmlEnv;
	}

	/**
	 *  builds the XML representation for listApplicationVersionInstance
	 * @param env
	 * @return List<ApplicationVersionInstanceXML>
	 */
	private List<ApplicationVersionInstanceXML> buildlistApplicationVersionInstance(
			Environment env) {
		List<ApplicationVersionInstanceXML> listApplicationVersionInstanceXML = new ArrayList<ApplicationVersionInstanceXML>();
		List<ApplicationVersionInstance> listApplicationVersionInstance = env
				.getListApplicationVersionInstance();

		if (listApplicationVersionInstance != null) {
			for (ApplicationVersionInstance applicationVersionInstance : listApplicationVersionInstance) {
				ApplicationVersionInstanceXML app = buildApplicationVersionInstance(applicationVersionInstance);
				if (app != null) {
					listApplicationVersionInstanceXML.add(app);
				}
			}
		}

		return listApplicationVersionInstanceXML;
	}

	/**
	 * builds the XML representation for applicationVersionInstance
	 * @param applicationVersionInstance
	 * @return ApplicationVersionInstanceXML
	 */
	private ApplicationVersionInstanceXML buildApplicationVersionInstance(
			ApplicationVersionInstance applicationVersionInstance) {
		ApplicationVersionInstanceXML app = new ApplicationVersionInstanceXML();
		Map<DeployableXML, NodeXML> deployableTopologyMap = buildDeployableTopologyMap(applicationVersionInstance);
		List<DeployableXML> sortedDeployabesLis = buildSortedDeployableList(applicationVersionInstance);

		app.setAppId(applicationVersionInstance.getAppId());
		app.setCapabilities(applicationVersionInstance.getCapabilities());
		app.setDeployableTopologyMapping(deployableTopologyMap);
		app.setInstanceId(applicationVersionInstance.getInstanceId());
		app.setInstanceName(applicationVersionInstance.getInstanceName());
		app.setRequirements(applicationVersionInstance.getRequirements());
		app.setSortedDeployabesLis(sortedDeployabesLis);
		app.setState(applicationVersionInstance.getState());
		app.setTargetEnvId(applicationVersionInstance.getTargetEnvId());
		app.setUrlList(applicationVersionInstance.getUrlList());
		app.setVersionID(applicationVersionInstance.getVersionId());

		return app;
	}

	/**
	 * builds the XML representation for SortedDeployableList
	 * @param applicationVersionInstance
	 * @return List<DeployableXML>
	 */
	private List<DeployableXML> buildSortedDeployableList(
			ApplicationVersionInstance applicationVersionInstance) {
		List<DeployableXML> depListXML = new ArrayList<DeployableXML>();

		if (applicationVersionInstance == null) {
			System.out.println("No applicationVersionInstance specified!");
			depListXML = null;
		} else {
			List<Deployable> depList = applicationVersionInstance
					.getSortedDeployablesList();

			if (depList != null) {
				for (Deployable dep : depList) {
					DeployableXML depXML = buildDeployable(dep);
					if (depXML != null) {
						depListXML.add(depXML);
					}
				}
			}
		}
		return depListXML;
	}

	/**
	 * builds the XML representation for  DeployableTopologyMap
	 * @param applicationVersionInstance
	 * @return Map<DeployableXML, NodeXML>
	 */
	private Map<DeployableXML, NodeXML> buildDeployableTopologyMap(
			ApplicationVersionInstance applicationVersionInstance) {
		// TODO Auto-generated method stub
		Map<DeployableXML, NodeXML> deployableTopologyMap = new HashMap<DeployableXML, NodeXML>();
		if (applicationVersionInstance == null) {
			System.out.println("No applicationVersionInstance specified!");
			deployableTopologyMap = null;
		} else {
			Map<Deployable, Node> topologyMap = applicationVersionInstance
					.getDeployableTopologyMapping();
			if (topologyMap != null) {
				for (Map.Entry<Deployable, Node> entry : topologyMap.entrySet()) {
					DeployableXML depXML = buildDeployable(entry.getKey());
					NodeXML nodeXML = buildNode(entry.getValue());
					if (depXML != null && nodeXML != null)
						deployableTopologyMap.put(depXML, nodeXML);
				}
			}
		}
		return deployableTopologyMap;
	}

	/**
	 * builds the XML representation for Deployable
	 * @param dep
	 * @return DeployableXML
	 */
	private DeployableXML buildDeployable(Deployable dep) {
		DeployableXML depXML = new DeployableXML();
		depXML.setDeployableId(dep.getDeployabledId());
		depXML.setDeployableName(dep.getDeployableName());
		depXML.setLocationURL(dep.getLocationUrl());
		depXML.setRequirements(dep.getRequirements());
		depXML.setSlaEnforcement(dep.getSlaEnforcement());
		depXML.setUploaded(dep.getUploaded());
		return depXML;
	}

	/**
	 * builds the XML representation for Topology
	 * @param env
	 * @return TopologyXML
	 */
	private TopologyXML buildTopology(final Environment env) {
		Topology topo = env.getTopology();
		TopologyXML xmlTopo = new TopologyXML();
		List<NodeXML> topoListNode = buildListNode(topo);
		List<RelationshipXML> topoRelationShipList = buildRelationShipList(topo);

		xmlTopo.setTopoListNode(topoListNode);
		xmlTopo.setTopoRelationShipList(topoRelationShipList);

		return xmlTopo;
	}

	/**
	 * builds the XML representation for RelationShipList
	 * @param topo
	 * @return List<RelationshipXML>
	 */
	private List<RelationshipXML> buildRelationShipList(final Topology topo) {
		List<RelationshipXML> topoRelationShipListXML = new ArrayList<RelationshipXML>();

		if (topo == null) {
			System.out
					.println("The topology element is not defined in the Environment!!");
			topoRelationShipListXML = null;
		} else {
			List<Relationship> topoRelationShipList = topo
					.getRelationShipList();

			if (topoRelationShipList != null) {
				for (Relationship relation : topoRelationShipList) {
					RelationshipXML relationXML = buildRelationShip(relation);
					if (relationXML != null) {
						topoRelationShipListXML.add(relationXML);
					}
				}
			}
		}
		return topoRelationShipListXML;
	}

	/**
	 * builds the XML representation for RelationShip
	 * @param relation
	 * @return RelationshipXML
	 */
	private RelationshipXML buildRelationShip(Relationship relation) {
		RelationshipXML relationXML = new RelationshipXML();
		relationXML.setRelationShipID(Integer.parseInt(relation.toString()));
		// TODO Voir quels sont les attributs d'une relationShip
		return relationXML;
	}

	/**
	 * builds the XML representation for ListNode
	 * @param topo
	 * @return List<NodeXML>
	 */
	private List<NodeXML> buildListNode(final Topology topo) {
		List<NodeXML> topoListNodeXML = new ArrayList<NodeXML>();
		if (topo == null) {
			System.out
					.println("The topology element is not defined in the Environment!!");
			topoListNodeXML = null;
		} else {
			List<Node> topoListNode = topo.getNodeList();

			if (topoListNode != null) {
				for (Node node : topoListNode) {
					NodeXML nodeXML = buildNode(node);
					if (nodeXML != null) {
						topoListNodeXML.add(nodeXML);
					}
				}
			}
		}
		return topoListNodeXML;
	}

	private NodeXML buildNode(Node node) {
		NodeXML nodeXML = new NodeXML();
		nodeXML.setNodeCureentSize(node.getCurrentSize());
		nodeXML.setNodeID(node.getId());
		nodeXML.setNodeMaxSize(node.getMaxSize());
		nodeXML.setNodeMinSize(node.getMinSize());
		nodeXML.setNodeName(node.getName());
		return nodeXML;
	}

	/**
	 * @return the next available id
	 */
	private synchronized Long getNextId() {
		taskPool=new TaskPool();
		//TODO changer taskPool comme singleton
		return new Long(taskPool.getWorkTasks().size() + 1);
	}
	
	private TaskXML buildCreateEnvironmentTask(Long idTask, IWorkTask workTask,
			String taskName) {
        //TODO add link to cancel task
       /*List<Link> links = new ArrayList<Link>();
       links.add(getCancelLink(idTask));*/
       //build the XML task
       return buildTask(idTask, workTask);
	}

	private TaskXML buildTask(Long idTask, IWorkTask workTask) {
		TaskXML xmlTask = new TaskXML();
        xmlTask.setId(idTask);
        
        if (workTask != null) {
            Status status = workTask.getStatus();
            if (status != null) {
                xmlTask.setStatus(String.valueOf(status));
            }
            Date startTime = workTask.getStartTime();
            if (startTime != null) {
                xmlTask.setStartTime(startTime);
            }
            Date endTime = workTask.getEndTime();
            if (endTime != null) {
                xmlTask.setEndTime(endTime);
            }
        }
        return xmlTask;
	}

	private IWorkTask submitTask(ITask task, Long idTask,
			String environmentTemplateDescriptor) throws TaskException{

			IWorkTask workTask = new WorkTask(task, idTask, environmentTemplateDescriptor);
			this.taskPool.add(workTask);
			return workTask;
		}

}