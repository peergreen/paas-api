/**
 *
 */
package org.ow2.jonas.jpaas.core.server.resources.manager.environment;

import org.ow2.jonas.jpaas.api.resources.manager.environment.RestEnvironmentManager;
import org.ow2.jonas.jpaas.api.task.ITask;
import org.ow2.jonas.jpaas.api.task.IWorkTask;
import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.task.TaskException;
import org.ow2.jonas.jpaas.api.task.TaskPool;
import org.ow2.jonas.jpaas.core.ejb.client.EnvironmentManagerClient;
import org.ow2.jonas.jpaas.core.server.resources.exception.NotImplementedException;
import org.ow2.jonas.jpaas.core.server.task.CreateEnvironmentTask;
import org.ow2.jonas.jpaas.core.server.task.WorkTask;
import org.ow2.jonas.jpaas.core.server.xml.ApplicationVersionInstanceXML;
import org.ow2.jonas.jpaas.core.server.xml.DeployableXML;
import org.ow2.jonas.jpaas.core.server.xml.EnvironmentXML;
import org.ow2.jonas.jpaas.core.server.xml.Error;
import org.ow2.jonas.jpaas.core.server.xml.Link;
import org.ow2.jonas.jpaas.core.server.xml.NodeXML;
import org.ow2.jonas.jpaas.core.server.xml.RelationshipXML;
import org.ow2.jonas.jpaas.core.server.xml.TaskXML;
import org.ow2.jonas.jpaas.core.server.xml.TopologyXML;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManager;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManagerBeanException;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.manager.api.Connector;
import org.ow2.jonas.jpaas.manager.api.Datasource;
import org.ow2.jonas.jpaas.manager.api.Deployable;
import org.ow2.jonas.jpaas.manager.api.Environment;
import org.ow2.jonas.jpaas.manager.api.ExternalDatabase;
import org.ow2.jonas.jpaas.manager.api.JkRouter;
import org.ow2.jonas.jpaas.manager.api.JonasContainer;
import org.ow2.jonas.jpaas.manager.api.Node;
import org.ow2.jonas.jpaas.manager.api.Relationship;
import org.ow2.jonas.jpaas.manager.api.Topology;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * REST resource of type EnvironmentManager
 *
 * @author Mohamed Sellami (Telecom SudParis)
 */
@Path("environment")
public class EnvironmentManagerResource implements RestEnvironmentManager {

  /**
   * The used HTTP Port
   */
  private final String DEFAULT_HTTP_PORT = "9000";

  /**
   * The IP address or the named of the Host server
   */
  private final String DEFAULT_HOST = "127.0.0.1";

  /**
   * Cancel task name
   */
  public static final String CANCEL_TASK_NAME = "task:cancel";

  /**
   * Get task name
   */
  public static final String GET_TASK_NAME = "task:get";

  /**
   * The task pool
   */
  private TaskPool taskPool;

  // Environment management REST Operations

  /**
   * {@inheritDoc}
   */
  @Override
  public Response createEnvironment(String environmentTemplateDescriptor) {
    System.out
        .println("[CO-PaaS-API]: Call createEnvironment(environmentTemplateDescriptor) on the JPAAS-ENVIRONMENT-MANAGER");
    System.out.println("Test-Display" + environmentTemplateDescriptor);

    /* call the createEnvironment operation from the EJB */
    Future<Environment> env = null;
    EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
    try {
      env = envManager.createEnvironment(environmentTemplateDescriptor);
      // env=envEJB.createEnvironment(environmentTemplateDescriptor);
    } catch (EnvironmentManagerBeanException e1) {
      e1.printStackTrace();
    }

    // Create the CreateEnvironment Task
    if (env != null) {
      ITask task = new CreateEnvironmentTask(
          environmentTemplateDescriptor, env);
      final Long idTask = getNextId();
      IWorkTask workTask;
      try {
        workTask = submitCreateEnvironmentTask(task, idTask,
            environmentTemplateDescriptor);
      } catch (TaskException e) {
        System.out.println("Cannot submit task " + idTask);
        Error error = new Error();
        error.setValue("Cannot submit task " + idTask + ".\n" + e);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(error).build();
      }
      TaskXML xmlTask = buildCreateEnvironmentTask(idTask, workTask,
          CreateEnvironmentTask.TASK_NAME);

      return Response.status(Response.Status.ACCEPTED).entity(xmlTask)
          .type(MediaType.APPLICATION_XML_TYPE).build();
    } else {
      System.out
          .println("The EJB operation (createEnvironment) was not succefully invoked");
      Error error = new Error();
      error.setValue("Cannot invoke createEnvironment.");
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(error).build();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws EnvironmentManagerBeanException
   *
   */
  @Override
  public Response deleteEnvironment(String envid) {
    System.out.println("[CO-PaaS-API]: Call deleteEnvironment(" + envid
        + ") on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the deleteEnvironment operation from the EJB */
    EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
    try {
      envManager.deleteEnvironment(envid);
    } catch (EnvironmentManagerBeanException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    return Response.status(Response.Status.OK).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
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
    return Response.status(Response.Status.OK)
        .entity(new GenericEntity<List<EnvironmentXML>>(listEnvsXML) {
        }).type(MediaType.APPLICATION_XML_TYPE).build();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Response startEnvironment(String envid) {
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
   * {@inheritDoc}
   */
  @Override
  public Response stopEnvironment(String envid) {
    System.out.println("[CO-PaaS-API]: Call stopsEnvironment(" + envid
        + ") on the JPAAS-ENVIRONMENT-MANAGER");
    throw new NotImplementedException();
    // TODO same as startsEnvironment
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Response deployApplication(String envid,
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
   * {@inheritDoc}
   */
  @Override
  public Response undeployApplication(String envid,
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
   * {@inheritDoc}
   */
  @Override
  public Response getEnvironment(String envid) {
    System.out.println("[CO-PaaS-API]: Call getEnvironment(" + envid
        + ") on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the getEnvironment operation from the EJB */
    EnvironmentManager envManager = EnvironmentManagerClient.getProxy();
    Environment env = envManager.getEnvironment(envid);
    if (env != null) {
      String envDesc = env.getEnvDesc();
      return Response.status(Response.Status.OK).entity(envDesc).build();
    } else {
      System.out.println("Cannot find an environment with ID: " + envid);
      Error error = new Error();
      error.setValue("Cannot find an environment with ID: " + envid + ".");
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
          .entity(error).build();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
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

  // XML elements related methods

  /**
   * builds the XML representation for an Environment
   *
   * @param env The {@link Environment} object
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
   * builds the XML representation for listApplicationVersionInstance
   *
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
   *
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
    app.setState(Integer.toString(applicationVersionInstance.getState()));
    app.setTargetEnvId(applicationVersionInstance.getTargetEnvId());
    app.setUrlList(applicationVersionInstance.getUrlList());
    app.setVersionID(applicationVersionInstance.getVersionId());

    return app;
  }

  /**
   * builds the XML representation for SortedDeployableList
   *
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
   * builds the XML representation for DeployableTopologyMap
   *
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
   *
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
   *
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
   *
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
   *
   * @param relation
   * @return RelationshipXML
   */
  private RelationshipXML buildRelationShip(Relationship relation) {
    RelationshipXML relationXML = new RelationshipXML();
    if (relation instanceof Connector)
      relationXML.setRelationShipType("Connector");
    else if (relation instanceof Datasource)
      relationXML.setRelationShipType("Datasource");
    // TODO Voir quels sont les attributs d'une relationShip
    return relationXML;
  }

  /**
   * builds the XML representation for ListNode
   *
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

  /**
   * builds the XML representation for a Node
   *
   * @param node
   * @return NodeXML
   */
  private NodeXML buildNode(Node node) {
    NodeXML nodeXML = new NodeXML();
    nodeXML.setNodeCureentSize(node.getCurrentSize());
    nodeXML.setNodeID(node.getId());
    nodeXML.setNodeMaxSize(node.getMaxSize());
    nodeXML.setNodeMinSize(node.getMinSize());
    nodeXML.setNodeName(node.getName());
    if (node instanceof JkRouter)
      nodeXML.setNodeType("Router");
    else if (node instanceof JonasContainer)
      nodeXML.setNodeType("Jonas");
    else if (node instanceof ExternalDatabase)
      nodeXML.setNodeType("Database");
    return nodeXML;
  }

  // Task Pool related methods

  /**
   * Automatically generates an id for a new Task
   *
   * @return the next available id
   */
  private synchronized Long getNextId() {
    return new Long(taskPool.INSTANCE.getWorkTasks().size() + 1);
  }

  /**
   * @param relativePath The relative path
   * @param host         The hostname of the target url
   * @param port         The port of the target url
   * @return the associated HTTP url
   */
  private static String getUrl(final String relativePath, final String host,
                               final String port) {
    return "http://" + host + ":" + port + "/jpaas-api" + relativePath;
  }

  /**
   * @param relativePath The relative path
   * @return the HTTP url
   */
  private String getUrl(final String relativePath) {
    return getUrl(relativePath, DEFAULT_HOST, DEFAULT_HTTP_PORT);
  }

  /**
   * @param idTask The id of the task to cancel
   * @return the cancel task url
   */
  private String getCancelTaskUrl(final Long idTask) {
    return getUrl("/task/" + idTask + "/action/cancel");
  }

  /**
   * @param idTask The id of the task to cancel
   * @return the {@link Link} of the task to cancel
   */
  private Link getCancelLink(final Long idTask) {
    Link link = new Link();
    link.setHref(getCancelTaskUrl(idTask));
    link.setRel(CANCEL_TASK_NAME);
    link.setType(MediaType.APPLICATION_XML);
    return link;
  }

  /**
   * @param idTask The id of the Environment task to get
   * @return the get Environment task url
   */
  private String getEnvironmentTaskUrl(final Long idTask) {
    return getUrl("/task/" + idTask);
  }

  /**
   * @param idTask The id of the Environment task to get
   * @return the {@link Link} of the Environment Task
   */
  private Link getEnvironmentTaskLink(final Long idTask) {
    Link link = new Link();
    link.setHref(getEnvironmentTaskUrl(idTask));
    link.setRel(GET_TASK_NAME);
    link.setType(MediaType.APPLICATION_XML);
    return link;
  }

  /**
   * builds a CreateEnvironment Task
   *
   * @param idTask
   * @param workTask
   * @param taskName
   * @return TaskXML
   */
  private TaskXML buildCreateEnvironmentTask(Long idTask, IWorkTask workTask,
                                             String taskName) {
    // TODO add link to cancel task
    // List<Link> links = new ArrayList<Link>();
    // links.add(getCancelLink(idTask));
    // build the XML task
    return buildTask(idTask, workTask);
  }

  /**
   * builds a generic Task
   *
   * @param idTask
   * @param workTask
   * @return TaskXML
   */
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
      xmlTask.setLink(workTask.getLinkList());
    }
    return xmlTask;
  }

  /**
   * submits a CreateEnvironment Task
   *
   * @param task
   * @param idTask
   * @param environmentTemplateDescriptor
   * @return TaskXML
   */
  private IWorkTask submitCreateEnvironmentTask(ITask task, Long idTask,
                                                String environmentTemplateDescriptor) throws TaskException {

    /* add the cancel task link */
    List<Link> links = new ArrayList<Link>();
    links.add(getCancelLink(idTask));
    /* add the get task link */
    links.add(getEnvironmentTaskLink(idTask));

    IWorkTask workTask = new WorkTask(task, idTask,
        environmentTemplateDescriptor, links);
    taskPool.INSTANCE.add(workTask);
    return workTask;
  }

}