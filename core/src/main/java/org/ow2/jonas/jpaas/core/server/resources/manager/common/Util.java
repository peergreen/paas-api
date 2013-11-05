/**
 *
 */
package org.ow2.jonas.jpaas.core.server.resources.manager.common;

import org.ow2.jonas.jpaas.api.rest.DeployableRest;
import org.ow2.jonas.jpaas.api.xml.*;
import org.ow2.jonas.jpaas.application.api.ApplicationManager;
import org.ow2.jonas.jpaas.core.ejb.client.ApplicationManagerClient;
import org.ow2.jonas.jpaas.manager.api.*;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    private static Log logger = LogFactory.getLog(Util.class);

    /**
     * builds the XML representation for an Application
     *
     * @param app
     *            The {@link org.ow2.jonas.jpaas.manager.api.Application} object
     * @return an {@link org.ow2.jonas.jpaas.manager.api.Application} XML representation
     */
    public static ApplicationXML buildApplication(final org.ow2.jonas.jpaas.manager.api.Application app, final String baseUrl) {
        ApplicationXML xmlApp = null;
        if (app != null) {
            xmlApp = new ApplicationXML();
            xmlApp.setType(MediaType.APPLICATION_XML);
            xmlApp.setAppId(app.getAppId());
            xmlApp.setAppName(app.getName());
            xmlApp.setHref(getApplicationHref(baseUrl,app.getAppId()));

            List<LinkXML> links = new ArrayList<LinkXML>();
            for (ApplicationVersion version : app.getListApplicationVersion()) {
                links.add(buildLink(LinkXML.DOWN, MediaType.APPLICATION_XML, getApplicationVersionHref(baseUrl, version.getAppId(), version.getVersionId())));
            }

            links.add(buildLink(LinkXML.ADD, MediaType.APPLICATION_XML, getApplicationHref(baseUrl, app.getAppId()) + "/version"));
            links.add(buildLink(LinkXML.REMOVE, MediaType.APPLICATION_XML, getApplicationHref(baseUrl, app.getAppId())));

            xmlApp.setLinks(links);
        }

        return xmlApp;
    }

    public static LinkXML buildLink(String rel, String type, String href) {
        LinkXML linkXML = new LinkXML();
        linkXML.setRel(rel);
        linkXML.setType(type);
        linkXML.setHref(href);

        return linkXML;
    }

    /**
     * builds the XML representation for an ApplicationVersion
     *
     * @param appVersion
     *            The {@link org.ow2.jonas.jpaas.manager.api.Application} object
     * @return an {@link org.ow2.jonas.jpaas.manager.api.Application} XML representation
     */
    public static ApplicationVersionXML buildApplicationVersion(final ApplicationVersion appVersion, final String baseUrl) {

        ApplicationVersionXML xmlVersionApp = null;
        if (appVersion != null) {
            xmlVersionApp = new ApplicationVersionXML();
            xmlVersionApp.setType(MediaType.APPLICATION_XML);
            xmlVersionApp.setHref(getApplicationVersionHref(baseUrl,appVersion.getAppId(), appVersion.getVersionId()));
            xmlVersionApp.setAppId(appVersion.getAppId());
            xmlVersionApp.setVersionId(appVersion.getVersionId());
            xmlVersionApp.setVersionLabel(appVersion.getVersionLabel());
            xmlVersionApp.setRequirements(appVersion.getRequirements());
            xmlVersionApp.setCapabilities(appVersion.getCapabilities());

            List<DeployableXML> sortedDeployableList = Util.buildSortedDeployableList(appVersion.getSortedDeployablesList());
            xmlVersionApp.setSortedDeployableList(sortedDeployableList);

            List<LinkXML> links = new ArrayList<LinkXML>();
            for (ApplicationVersionInstance instance : appVersion.getListApplicationVersionInstance()) {
                links.add(buildLink(LinkXML.DOWN, MediaType.APPLICATION_XML, getApplicationVersionInstanceHref(baseUrl, instance.getAppId(), instance.getVersionId(), instance.getInstanceId())));
            }

            links.add(buildLink(LinkXML.UP, MediaType.APPLICATION_XML, getApplicationHref(baseUrl, appVersion.getAppId())));

            links.add(buildLink(LinkXML.ADD, MediaType.APPLICATION_XML, getApplicationVersionHref(baseUrl, appVersion.getAppId(), appVersion.getVersionId()) + "/instance"));
            links.add(buildLink(LinkXML.REMOVE, MediaType.APPLICATION_XML, getApplicationVersionHref(baseUrl, appVersion.getAppId(), appVersion.getVersionId())));

            xmlVersionApp.setLinks(links);

        }

        return xmlVersionApp;
    }


    /**
     * builds the XML representation for applicationVersionInstance
     *
     * @param applicationVersionInstance
     * @return ApplicationVersionInstanceXML
     */
    public static ApplicationVersionInstanceXML buildApplicationVersionInstance(ApplicationVersionInstance applicationVersionInstance, final String baseUrl) {

        ApplicationVersionInstanceXML instanceXML = null;

        if (applicationVersionInstance != null) {
            instanceXML = new ApplicationVersionInstanceXML();
            instanceXML.setType(MediaType.APPLICATION_XML);
            instanceXML.setHref(getApplicationVersionInstanceHref(baseUrl, applicationVersionInstance.getAppId(),applicationVersionInstance.getVersionId(),applicationVersionInstance.getInstanceId()));

            Map<DeployableXML, NodeXML> deployableTopologyMap = Util.buildDeployableTopologyMap(applicationVersionInstance);
            List<DeployableXML> sortedDeployableList = Util.buildSortedDeployableList(applicationVersionInstance.getSortedDeployablesList());

            instanceXML.setAppId(applicationVersionInstance.getAppId());
            instanceXML.setCapabilities(applicationVersionInstance.getCapabilities());
            instanceXML.setDeployableTopologyMapping(deployableTopologyMap);
            instanceXML.setInstanceId(applicationVersionInstance.getInstanceId());
            instanceXML.setInstanceName(applicationVersionInstance.getInstanceName());
            instanceXML.setRequirements(applicationVersionInstance.getRequirements());
            instanceXML.setSortedDeployableList(sortedDeployableList);
            instanceXML.setState(applicationVersionInstance.getStateStr());
            instanceXML.setTargetEnvId(applicationVersionInstance.getTargetEnvId());
            instanceXML.setUrlList(applicationVersionInstance.getUrlList());
            instanceXML.setVersionId(applicationVersionInstance.getVersionId());

            List<LinkXML> links = new ArrayList<LinkXML>();

            links.add(buildLink(LinkXML.UP, MediaType.APPLICATION_XML, getApplicationHref(baseUrl, applicationVersionInstance.getAppId())));
            links.add(buildLink(LinkXML.UP, MediaType.APPLICATION_XML, getApplicationVersionHref(baseUrl, applicationVersionInstance.getAppId(), applicationVersionInstance.getVersionId())));
            if (applicationVersionInstance.getTargetEnvId() != null) {
                links.add(buildLink(LinkXML.UP, MediaType.APPLICATION_XML, getEnvironmentHref(baseUrl, applicationVersionInstance.getTargetEnvId())));
            }

            links.add(buildLink(LinkXML.REMOVE, MediaType.APPLICATION_XML, getApplicationVersionInstanceHref(baseUrl, applicationVersionInstance.getAppId(), applicationVersionInstance.getVersionId(), applicationVersionInstance.getInstanceId())));

            links.add(buildLink(LinkXML.ACTION, MediaType.APPLICATION_XML, getApplicationVersionInstanceHref(baseUrl, applicationVersionInstance.getAppId(), applicationVersionInstance.getVersionId(), applicationVersionInstance.getInstanceId()) + "/action/start"));
            links.add(buildLink(LinkXML.ACTION, MediaType.APPLICATION_XML, getApplicationVersionInstanceHref(baseUrl, applicationVersionInstance.getAppId(), applicationVersionInstance.getVersionId(), applicationVersionInstance.getInstanceId()) + "/action/stop"));

            instanceXML.setLinks(links);
        }

        return instanceXML;
    }




    /**
     * Load a xml from a String
     * @param xml the xml String
     * @return the XML Root Element of the xml String
     */
    public static Element loadXml(String xml) throws Exception {
        Element result = null;
        try {
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(is);
            result = document.getDocumentElement();
        } catch (ParserConfigurationException e) {
            throw new Exception("Cannot parse the String " + xml, e);
        } catch (SAXException e) {
            throw new Exception("Cannot parse the String " + xml, e);
        }
        return result;
    }

    /**
     * builds the XML representation for Deployable
     *
     * @param dep
     * @return DeployableXML
     */
    public static DeployableXML buildDeployable(Deployable dep) {

        DeployableXML depXML = null;
        if (dep != null) {

            depXML = new DeployableXML();
            depXML.setDeployableId(dep.getDeployabledId());
            depXML.setDeployableName(dep.getDeployableName());
            depXML.setLocationURL(dep.getLocationUrl());
            depXML.setRequirements(dep.getRequirements());
            depXML.setSlaEnforcement(dep.getSlaEnforcement());
            depXML.setUploaded(dep.getUploaded());
        }
        return depXML;
    }

    /**
     * builds the XML representation for an Environment
     *
     * @param env The {@link org.ow2.jonas.jpaas.manager.api.Environment} object
     * @return an {@link org.ow2.jonas.jpaas.manager.api.Environment} XML representation
     */
    public static EnvironmentXML buildEnvironment(final org.ow2.jonas.jpaas.manager.api.Environment env, final String baseUrl) {
        EnvironmentXML xmlEnv = new EnvironmentXML();

        TopologyXML xmlTopo = buildTopology(env);

        xmlEnv.setType(MediaType.APPLICATION_XML);
        xmlEnv.setHref(getEnvironmentHref(baseUrl,env.getEnvId()));

        xmlEnv.setEnvDesc(env.getEnvDesc());
        xmlEnv.setEnvId(env.getEnvId());
        xmlEnv.setEnvName(env.getEnvName());
        xmlEnv.setEnvState(env.getState());
        xmlEnv.setEnvTopology(xmlTopo);

        List<LinkXML> links = new ArrayList<LinkXML>();
        for (ApplicationVersionInstance instance : env.getListApplicationVersionInstance()) {
            links.add(buildLink(LinkXML.DOWN, MediaType.APPLICATION_XML, getApplicationVersionInstanceHref(baseUrl, instance.getAppId(), instance.getVersionId(), instance.getInstanceId())));
        }

        links.add(buildLink(LinkXML.REMOVE, MediaType.APPLICATION_XML, getEnvironmentHref(baseUrl, env.getEnvId())));
        links.add(buildLink(LinkXML.ACTION, MediaType.APPLICATION_XML, getEnvironmentHref(baseUrl, env.getEnvId() + "/action/start")));
        links.add(buildLink(LinkXML.ACTION, MediaType.APPLICATION_XML, getEnvironmentHref(baseUrl, env.getEnvId() + "/action/stop")));

        xmlEnv.setLinks(links);

        return xmlEnv;
    }

    /**
     * builds the XML representation for SortedDeployableList
     *
     * @param depList
     * @return List<DeployableXML>
     */
    public static List<DeployableXML> buildSortedDeployableList(List<Deployable> depList) {
        List<DeployableXML> depListXML = null;

        if (depList != null) {
            depListXML = new ArrayList<DeployableXML>();

            for (Deployable dep : depList) {
                    DeployableXML depXML = buildDeployable(dep);
                    if (depXML != null) {
                        depListXML.add(depXML);
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
    public static Map<DeployableXML, NodeXML> buildDeployableTopologyMap(
            ApplicationVersionInstance applicationVersionInstance) {
        // TODO Auto-generated method stub
        Map<DeployableXML, NodeXML> deployableTopologyMap = new HashMap<DeployableXML, NodeXML>();
        if (applicationVersionInstance == null) {
            logger.error("No applicationVersionInstance specified!");
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
     * builds the XML representation for Topology
     *
     * @param env
     * @return TopologyXML
     */
    public static TopologyXML buildTopology(final org.ow2.jonas.jpaas.manager.api.Environment env) {
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
    public static List<RelationshipXML> buildRelationShipList(final Topology topo) {
        List<RelationshipXML> topoRelationShipListXML = new ArrayList<RelationshipXML>();

        if (topo == null) {
            logger.error("The topology element is not defined in the Environment!!");
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
    public static RelationshipXML buildRelationShip(Relationship relation) {
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
    public static List<NodeXML> buildListNode(final Topology topo) {
        List<NodeXML> topoListNodeXML = new ArrayList<NodeXML>();
        if (topo == null) {
            logger.error("The topology element is not defined in the Environment!!");
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
    public static NodeXML buildNode(Node node) {
        NodeXML nodeXML = new NodeXML();
        nodeXML.setNodeCurrentSize(node.getCurrentSize());
        nodeXML.setNodeId(node.getId());
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

    public static String getApplicationHref(String baseUrl, String appId) {
        return baseUrl + "app/" + appId;
    }

    public static String getApplicationVersionHref(String baseUrl, String appId, String versionId) {
        return baseUrl + "app/" + appId + "/version/" + versionId;
    }

    public static String getApplicationVersionInstanceHref(String baseUrl, String appId, String versionId, String instanceId) {
        return baseUrl + "app/" + appId + "/version/" + versionId + "/instance/" + instanceId;
    }

    public static String getEnvironmentHref(String baseUrl, String envId) {
        return baseUrl + "environment/" + envId;
    }


}
