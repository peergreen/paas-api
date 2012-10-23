/**
 *
 */
package org.ow2.jonas.jpaas.core.server.ressources.manager.application;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.jonas.jpaas.api.ressources.manager.application.RestApplicationManager;
import org.ow2.jonas.jpaas.application.api.ApplicationManager;
import org.ow2.jonas.jpaas.application.api.ApplicationManagerBeanException;
import org.ow2.jonas.jpaas.core.ejb.client.ApplicationManagerClient;
import org.ow2.jonas.jpaas.core.server.ressources.exception.NotImplementedException;
import org.ow2.jonas.jpaas.core.server.xml.ApplicationVersionInstanceXML;
import org.ow2.jonas.jpaas.core.server.xml.ApplicationVersionXML;
import org.ow2.jonas.jpaas.core.server.xml.ApplicationXML;
import org.ow2.jonas.jpaas.manager.api.Application;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersion;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.core.server.xml.Error;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


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
    public Response createApplicationVersion(String appid, String applicationVersionDescriptor) {
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

    @Override
    public Response createApplicationVersionInstance(String appId, String versionId,
            String applicationVersionInstanceDescriptor) {
        System.out.println("[CO-PaaS-API]: Call createApplicationVersionInstance("
                + applicationVersionInstanceDescriptor
                + ") on the JPAAS-APPLICATION-MANAGER");
        /* call the createApplicationVersionInstance operation from the EJB */
        ApplicationManager envManager = ApplicationManagerClient.getProxy();
        ApplicationVersionInstance appVerIns=null;

        try {
            Element appXml = null;
            DOMImplementationRegistry registry = null;
            appXml = loadXml(applicationVersionInstanceDescriptor);
            Element cloudApplicationNode = (Element) appXml.getElementsByTagName("cloud-application").item(0);
            Element deploymentNode = (Element) appXml.getElementsByTagName("deployment").item(0);
            cloudApplicationNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns",
                    "http://jonas.ow2.org/ns/cloud/application/1.0");
            deploymentNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns",
                    "http://jonas.ow2.org/ns/cloud/deployment/1.0");
            registry = DOMImplementationRegistry.newInstance();
            DOMImplementationLS impl =
                    (DOMImplementationLS)registry.getDOMImplementation("LS");


            LSSerializer writer = impl.createLSSerializer();
            LSOutput lsOutput = impl.createLSOutput();
            lsOutput.setEncoding("UTF-8");
            Writer stringWriter = new StringWriter();
            lsOutput.setCharacterStream(stringWriter);
            writer.write(cloudApplicationNode, lsOutput);
            String cloudApplication = stringWriter.toString();
            System.out.println(cloudApplication);

            stringWriter = new StringWriter();
            lsOutput.setCharacterStream(stringWriter);
            writer.write(deploymentNode, lsOutput);
            String deployment = stringWriter.toString();
            System.out.println(deployment);

            appVerIns = envManager.createApplicationVersionInstance(cloudApplication, deployment);
        } catch (ApplicationManagerBeanException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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
     * {@inheritDoc}
     */
    @Override
    public Response findApplications() {
        System.out
                .println("[CO-PaaS-API]: Call findApplications() on the JPAAS-APPLICATION-MANAGER");
        List<Application> listApp = new ArrayList<Application>();
        /* call the findApplications operation from the EJB */
        ApplicationManager envManager = ApplicationManagerClient.getProxy();
        listApp = envManager.findApplications();
        throw new NotImplementedException();
        // TODO return Response.status(Response.Status.OK).entity(new
        // GenericEntity<List<Application>>(listApp){}).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response findApplicationVersions(@PathParam("appId") String appid) {
        System.out.println("[CO-PaaS-API]: Call findApplicationVersions("
                + appid + ") on the JPAAS-APPLICATION-MANAGER");
        List<ApplicationVersion> listAppVer = new ArrayList<ApplicationVersion>();
        /* call the findApplicationVersions(appid) operation from the EJB */
        ApplicationManager envManager = ApplicationManagerClient.getProxy();
        listAppVer = envManager.findApplicationVersion(appid);
        throw new NotImplementedException();
        // TODO return Response.status(Response.Status.OK).entity(new
        // GenericEntity<List<ApplicationVersion>>(listAppVer){}).build();
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
        ApplicationManager envManager = ApplicationManagerClient.getProxy();
        listAppVerInstances = envManager.findApplicationVersionsInstances(
                appid, versionid);
        throw new NotImplementedException();
        // TODO return Response.status(Response.Status.OK).entity(new
        // GenericEntity<List<ApplicationVersionInstance>>(listAppVerInstances){}).build();
    }

    @Override
    public Response startApplicationVersionInstance(String appid,
            String versionid, String instanceid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Response describeApplication(String appId) {
        // TODO Auto-generated method stub
        return null;
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

    /**
     * Load a xml from a String
     * @param xml the xml String
     * @return the XML Root Element of the xml String
     */
    private Element loadXml(String xml) throws Exception {
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

}
