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
    public static ApplicationXML buildApplication(final org.ow2.jonas.jpaas.manager.api.Application app) {
        ApplicationXML xmlApp = null;
        if (app != null) {
            xmlApp = new ApplicationXML();
            xmlApp.setAppId(app.getAppId());
            xmlApp.setAppName(app.getName());

        }

        return xmlApp;

    }

    /**
     * builds the XML representation for an ApplicationVersion
     *
     * @param appVersion
     *            The {@link org.ow2.jonas.jpaas.manager.api.Application} object
     * @return an {@link org.ow2.jonas.jpaas.manager.api.Application} XML representation
     */
    public static ApplicationVersionXML buildApplicationVersion(final ApplicationVersion appVersion) {

        ApplicationVersionXML xmlVersionApp = null;
        if (appVersion != null) {
            xmlVersionApp = new ApplicationVersionXML();

            xmlVersionApp.setAppId(appVersion.getAppId());
            xmlVersionApp.setAppVerId(appVersion.getVersionId());
            xmlVersionApp.setAppVerLabel(appVersion.getVersionLabel());
            xmlVersionApp.setRequirements(appVersion.getRequirements());
            xmlVersionApp.setCapabilities(appVersion.getCapabilities());

            List<DeployableXML> sortedDeployableList = Util.buildSortedDeployableList(appVersion.getSortedDeployablesList());
            xmlVersionApp.setSortedDeployableList(sortedDeployableList);

        }

        return xmlVersionApp;
    }


    /**
     * builds the XML representation for applicationVersionInstance
     *
     * @param applicationVersionInstance
     * @return ApplicationVersionInstanceXML
     */
    public static ApplicationVersionInstanceXML buildApplicationVersionInstance(ApplicationVersionInstance applicationVersionInstance) {

        ApplicationVersionInstanceXML app = null;

        if (applicationVersionInstance != null) {
            app = new ApplicationVersionInstanceXML();
            Map<DeployableXML, NodeXML> deployableTopologyMap = Util.buildDeployableTopologyMap(applicationVersionInstance);
            List<DeployableXML> sortedDeployableList = Util.buildSortedDeployableList(applicationVersionInstance.getSortedDeployablesList());

            app.setAppId(applicationVersionInstance.getAppId());
            app.setCapabilities(applicationVersionInstance.getCapabilities());
            app.setDeployableTopologyMapping(deployableTopologyMap);
            app.setInstanceId(applicationVersionInstance.getInstanceId());
            app.setInstanceName(applicationVersionInstance.getInstanceName());
            app.setRequirements(applicationVersionInstance.getRequirements());
            app.setSortedDeployableList(sortedDeployableList);
            app.setState(applicationVersionInstance.getStateStr());
            app.setTargetEnvId(applicationVersionInstance.getTargetEnvId());
            app.setUrlList(applicationVersionInstance.getUrlList());
            app.setVersionID(applicationVersionInstance.getVersionId());
        }

        return app;
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
    public static EnvironmentXML buildEnvironment(final org.ow2.jonas.jpaas.manager.api.Environment env) {
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
    public static List<ApplicationVersionInstanceXML> buildlistApplicationVersionInstance(
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
