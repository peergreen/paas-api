/**
 *
 */
package org.ow2.jonas.jpaas.core.server.resources.manager.environment;

import org.ow2.jonas.jpaas.api.rest.EnvironmentRest;
import org.ow2.jonas.jpaas.api.task.Task;
import org.ow2.jonas.jpaas.api.task.TaskException;
import org.ow2.jonas.jpaas.api.xml.*;
import org.ow2.jonas.jpaas.core.ejb.client.EnvironmentManagerClient;
import org.ow2.jonas.jpaas.core.server.resources.exception.NotImplementedException;
import org.ow2.jonas.jpaas.core.server.task.CreateEnvironmentTask;
import org.ow2.jonas.jpaas.api.xml.ErrorXML;
import org.ow2.jonas.jpaas.core.server.task.TaskManager;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManager;
import org.ow2.jonas.jpaas.environment.manager.api.EnvironmentManagerBeanException;
import org.ow2.jonas.jpaas.manager.api.*;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * REST resource of type EnvironmentManager
 *
 * @author Mohamed Sellami (Telecom SudParis)
 */
public class EnvironmentManagerResource implements EnvironmentRest {

    private Log logger = LogFactory.getLog(EnvironmentManagerResource.class);

    @Context
    private UriInfo uriInfo;

    private EnvironmentManager envManager;


    public EnvironmentManagerResource() {
        envManager = EnvironmentManagerClient.getProxy();
    }

    @Override
    public Response createEnvironment(String environmentTemplateDescriptor) {

        logger.info("desc=" + environmentTemplateDescriptor);

        Future<Environment> env = null;

        try {
            env = envManager.createEnvironment(environmentTemplateDescriptor);
        } catch (EnvironmentManagerBeanException e1) {
            logger.error("Cannot create environment: " + e1.getMessage());
            ErrorXML error = new ErrorXML();
            error.setValue("Cannot create environment:" + e1);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        Task task = new CreateEnvironmentTask(environmentTemplateDescriptor, env, uriInfo.getBaseUri().toString());

        TaskXML xmlTask = TaskManager.getSingleton().buildTaskXml(task);

        return Response.status(Response.Status.ACCEPTED).entity(xmlTask)
                .type(MediaType.APPLICATION_XML_TYPE).build();
    }

    /**
     * {@inheritDoc}
     *
     * @throws EnvironmentManagerBeanException
     *
     */
    @Override
    public Response deleteEnvironment(String envid) {
        logger.info("[CO-PaaS-API]: Call deleteEnvironment(" + envid
                + ") on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the deleteEnvironment operation from the EJB */
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
        logger.info("[CO-PaaS-API]: Call findEnvironments() on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the findEnvironments operation from the EJB */

        List<EnvironmentXML> listEnvsXML = new ArrayList<EnvironmentXML>();
        List<org.ow2.jonas.jpaas.manager.api.Environment> listEnvs = envManager.findEnvironments();

        if (listEnvs != null) {
            for (org.ow2.jonas.jpaas.manager.api.Environment listEnv : listEnvs) {
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
        logger.info("[CO-PaaS-API]: Call startsEnvironment(" + envid
                + ") on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the startsEnvironment operation from the EJB */
        Future<org.ow2.jonas.jpaas.manager.api.Environment> f = envManager.startEnvironment(envid);
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
        logger.info("[CO-PaaS-API]: Call stopsEnvironment(" + envid
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
        logger.info("[CO-PaaS-API]: Call deployApplication(" + envid
                + "," + appid + "," + versionid + "," + instanceid
                + ") on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the deployApplication operation from the EJB */
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
        logger.info("[CO-PaaS-API]: Call undeployApplication(" + envid
                + "," + appid + "," + versionid + "," + instanceid
                + ") on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the undeployApplication operation from the EJB */
        envManager.undeployApplication(envid, appid, versionid, instanceid);
        return Response.status(Response.Status.OK).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getEnvironment(String envid) {
        logger.info("Get env: " + envid);
        Environment env = envManager.getEnvironment(envid);
        EnvironmentXML envXML = buildEnvironment(env);
        return Response.status(Response.Status.OK)
                .entity(envXML).type(MediaType.APPLICATION_XML_TYPE).build();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getDeployedApplicationVersionInstance(
            @PathParam("envId") String envid) {
        logger.info("[CO-PaaS-API]: Call getDeployedApplicationVersionInstance("
                + envid + ") on the JPAAS-ENVIRONMENT-MANAGER");
    /* call the getDeployedApplicationVersionInstance operation from the EJB */
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
     * @param env The {@link org.ow2.jonas.jpaas.manager.api.Environment} object
     * @return an {@link org.ow2.jonas.jpaas.manager.api.Environment} XML representation
     */
    private EnvironmentXML buildEnvironment(final org.ow2.jonas.jpaas.manager.api.Environment env) {
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
            org.ow2.jonas.jpaas.manager.api.Environment env) {
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
    private TopologyXML buildTopology(final org.ow2.jonas.jpaas.manager.api.Environment env) {
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
}