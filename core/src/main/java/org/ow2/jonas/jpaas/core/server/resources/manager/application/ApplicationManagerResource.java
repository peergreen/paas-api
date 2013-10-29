/**
 *
 */
package org.ow2.jonas.jpaas.core.server.resources.manager.application;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ow2.jonas.jpaas.api.rest.ApplicationRest;
import org.ow2.jonas.jpaas.application.api.ApplicationManager;
import org.ow2.jonas.jpaas.application.api.ApplicationManagerBeanException;
import org.ow2.jonas.jpaas.core.ejb.client.ApplicationManagerClient;
import org.ow2.jonas.jpaas.api.xml.ApplicationVersionInstanceXML;
import org.ow2.jonas.jpaas.api.xml.ApplicationVersionXML;
import org.ow2.jonas.jpaas.api.xml.ApplicationXML;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersion;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.api.xml.ErrorXML;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
public class ApplicationManagerResource implements ApplicationRest {

    private Log logger = LogFactory.getLog(ApplicationManagerResource.class);

    private ApplicationManager appManager;
    private ErrorXML error = new ErrorXML();

    public ApplicationManagerResource() {
        appManager = ApplicationManagerClient.getProxy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response createApplication(String cloudApplicationDescriptor) {

        logger.info("Create application with Desc=" + cloudApplicationDescriptor);
        org.ow2.jonas.jpaas.manager.api.Application app = null;
        try {
            app = appManager.createApplication(cloudApplicationDescriptor);
        } catch (ApplicationManagerBeanException e) {
            error.setValue("Failed to create the Application: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
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
            logger.info("Failed to create the Application!");

            error.setValue("Failed to create the Application!");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }
     }

    /**
     * {@inheritDoc}
     */
    public Response createApplicationVersion(String appId, String applicationVersionDescriptor) {
        logger.info("Create application version with Desc=" + applicationVersionDescriptor);

        ApplicationVersion appVer=null;
        try {
            appVer = appManager.createApplicationVersion(appId, applicationVersionDescriptor);
        } catch (ApplicationManagerBeanException e) {
            error.setValue("Failed to create the Application Version : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        if (appVer != null) {
            ApplicationVersionXML appVerXML = new ApplicationVersionXML();
            appVerXML.setAppId(appVer.getAppId());
            appVerXML.setAppVerId(appVer.getVersionId());
            appVerXML.setAppVerLabel(appVer.getVersionLabel());
            return Response.status(Response.Status.OK)
                    .entity(new GenericEntity<ApplicationVersionXML>(appVerXML) {
                    }).type(MediaType.APPLICATION_XML_TYPE).build();
        } else {
            logger.info("Failed to create the Application Version!");

            error.setValue("Failed to create the Application Version!");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }
    }

    @Override
    public Response createApplicationVersionInstance(String appId, String versionId, String applicationVersionInstanceDescriptor) {
        logger.info("Creation application version instance with appId="+ appId + ", versionId=" + versionId, "desc=" + applicationVersionInstanceDescriptor);

        ApplicationVersionInstance appVerIns=null;

        try {
            Element appXml = null;
            DOMImplementationRegistry registry = null;
            appXml = loadXml(applicationVersionInstanceDescriptor);
            Element cloudApplicationNode = (Element) appXml.getElementsByTagName("cloud-application").item(0);
            Element deploymentNode = (Element) appXml.getElementsByTagName("deployment").item(0);

            //Add the NameSpace to fix a bug
            //cloudApplicationNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns",
            //        "http://jonas.ow2.org/ns/cloud/application/1.0");
            //deploymentNode.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns",
            //        "http://jonas.ow2.org/ns/cloud/deployment/1.0");

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

            stringWriter = new StringWriter();
            lsOutput.setCharacterStream(stringWriter);
            writer.write(deploymentNode, lsOutput);
            String deployment = stringWriter.toString();

            appVerIns = appManager.createApplicationVersionInstance(appId, versionId, cloudApplication, deployment);

        } catch (Exception e) {
            logger.info("Failed to create the Application Version instance ! - " + e.getMessage());
            error.setValue("Failed to create the Application Version Instance : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        // TODO add the other application attributes
        if (appVerIns != null) {
            ApplicationVersionInstanceXML appVerInsXML = new ApplicationVersionInstanceXML();

            appVerInsXML.setAppId(appVerIns.getAppId());
            appVerInsXML.setVersionID(appVerIns.getVersionId());
            appVerInsXML.setInstanceId(appVerIns.getInstanceId());
            appVerInsXML.setInstanceName(appVerIns.getInstanceName());
            return Response.status(Response.Status.OK)
                    .entity(new GenericEntity<ApplicationVersionInstanceXML>(appVerInsXML) {
                    }).type(MediaType.APPLICATION_XML_TYPE).build();
        } else {
            logger.info("Failed to create the Application Version instance !");
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
        logger.info("Get all applications");
        List<org.ow2.jonas.jpaas.manager.api.Application> listApp = new ArrayList<org.ow2.jonas.jpaas.manager.api.Application>();
        listApp = appManager.findApplications();
        List<ApplicationXML> listAppsXML = new ArrayList<ApplicationXML>();
        if (listApp != null) {
            for (org.ow2.jonas.jpaas.manager.api.Application app : listApp) {
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
    public Response findApplicationVersions(String appId) {
        logger.info("Get applications version with appId="+ appId);
        List<ApplicationVersion> listAppVer = new ArrayList<ApplicationVersion>();
        listAppVer = appManager.findApplicationVersion(appId);
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
    public Response findApplicationVersionInstances(String appId, String versionId) {
        logger.info("Get applications version instance with appId=" + appId + ", versionId=" + versionId);
        List<ApplicationVersionInstance> listAppVerInstances = new ArrayList<ApplicationVersionInstance>();
        listAppVerInstances = appManager.findApplicationVersionsInstances(appId, versionId);
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
    public Response startApplicationVersionInstance(String appId, String versionId, String instanceId) {
        logger.info("Start application with appId="+ appId + ", versionId=" + versionId + ", instanceId=" + instanceId);

        Future<ApplicationVersionInstance> instance = null;
        try {
            instance = appManager.startApplicationVersionInstance(appId, versionId, instanceId);
        } catch (ApplicationManagerBeanException e) {
            error.setValue("Failed to start the Application Instance : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }
        ApplicationVersionInstanceXML appVersionInstanceXML=null;
        try {
            appVersionInstanceXML = buildApplicationVersionInstance(instance.get());
        } catch (InterruptedException | ExecutionException e) {
            error.setValue("Failed to start the Application Instance : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        return Response.status(Response.Status.OK)
                .entity(appVersionInstanceXML).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    @Override
    public Response stopApplicationVersionInstance(String appId, String versionId, String instanceId) {
        logger.info("Stop application with appId="+ appId + ", versionId=" + versionId, "instanceId=" + instanceId);

        Future<ApplicationVersionInstance> instance = null;
        try {
            instance = appManager.stopApplicationVersionInstance(appId, versionId, instanceId);
        } catch (ApplicationManagerBeanException e) {
            error.setValue("Failed to stop the Application Instance : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }
        ApplicationVersionInstanceXML appVersionInstanceXML=null;
        try {
            appVersionInstanceXML = buildApplicationVersionInstance(instance.get());
        } catch (InterruptedException | ExecutionException e) {
            error.setValue("Failed to stop the Application Instance : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        return Response.status(Response.Status.OK)
                .entity(appVersionInstanceXML).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    @Override
    public Response describeApplication(String appId) {
        org.ow2.jonas.jpaas.manager.api.Application app = null;
        app = appManager.getApplication(appId);
        ApplicationXML appXML = buildApplication(app);
        return Response.status(Response.Status.OK)
                .entity(appXML).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    @Override
    public Response describeApplicationVersion(String appId, String versionId) {
        org.ow2.jonas.jpaas.manager.api.ApplicationVersion version = null;
        version = appManager.getApplicationVersion(appId, versionId);
        ApplicationVersionXML versionXML = buildApplicationVersion(version);
        return Response.status(Response.Status.OK)
                .entity(versionXML).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    @Override
    public Response describeApplicationVersionInstance(String appId, String versionId, String instanceId) {
        org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance instance = null;
        instance = appManager.getApplicationVersionInstance(appId, versionId, instanceId);
        ApplicationVersionInstanceXML instanceXML = buildApplicationVersionInstance(instance);
        return Response.status(Response.Status.OK)
                .entity(instanceXML).type(MediaType.APPLICATION_XML_TYPE).build();
    }


    @Override
    public Response deleteApplication(String appId) {
        logger.info("Delete application with appId="+ appId);

        try {
            appManager.deleteApplication(appId);
        } catch (ApplicationManagerBeanException e) {
            error.setValue("Failed to delete the Application : " + appId + " - " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }
        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    @Override
    public Response deleteApplicationVersion(String appId, String versionId) {
        logger.info("Delete application with appId="+ appId + ", versionId=" + versionId);

        try {
            appManager.deleteApplicationVersion(appId,versionId);
        } catch (ApplicationManagerBeanException e) {
            error.setValue("Failed to delete the Application : " + appId + "/" + versionId + " - " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    @Override
    public Response deleteApplicationVersionInstance(String appId, String versionId, String instanceId) {
        logger.info("Delete application with appId="+ appId + ", versionId=" + versionId, "instanceId=" + instanceId);
        try {
            appManager.deleteApplicationVersionInstance(appId,versionId,instanceId);
        } catch (ApplicationManagerBeanException e) {
            error.setValue("Failed to delete the Application : " + appId + "/" + versionId + "/" + instanceId + " - " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }
        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    /**
     * Scale up an application version instance Command: POST
     * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
     *
     * @param appId      The application's ID
     * @param versionId  The application version's ID
     * @param instanceId The application instance's ID
     * @return XML Task with TaskId
     */
    @Override
    public Response scaleUp(String appId, String versionId, String instanceId) {
        logger.info("Scale up application with appId="+ appId + ", versionId=" + versionId, "instanceId=" + instanceId);

        Future<ApplicationVersionInstance> instance = null;

        try {
            instance = appManager.scaleUp(appId, versionId, instanceId);
        } catch (ApplicationManagerBeanException e1) {
            e1.printStackTrace();
        }

        ApplicationVersionInstanceXML appVersionInstanceXML=null;
        try {
            appVersionInstanceXML = buildApplicationVersionInstance(instance.get());
        } catch (InterruptedException | ExecutionException e) {
            error.setValue("Failed to scale up the Application Instance : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        return Response.status(Response.Status.OK)
                .entity(appVersionInstanceXML).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    /**
     * Scale down an application version instance Command: POST
     * /app/{appId}/version/{versionId}/instance/{instanceId}/action/start
     *
     * @param appId      The application's ID
     * @param versionId  The application version's ID
     * @param instanceId The application instance's ID
     * @return XML Task with TaskId
     */
    @Override
    public Response scaleDown(String appId, String versionId, String instanceId) {
        logger.info("Scale down application with appId="+ appId + ", versionId=" + versionId, "instanceId=" + instanceId);

        Future<ApplicationVersionInstance> instance = null;

        try {
            instance = appManager.scaleDown(appId, versionId, instanceId);
        } catch (ApplicationManagerBeanException e1) {
            e1.printStackTrace();
        }

        ApplicationVersionInstanceXML appVersionInstanceXML=null;
        try {
            appVersionInstanceXML = buildApplicationVersionInstance(instance.get());
        } catch (InterruptedException | ExecutionException e) {
            error.setValue("Failed to scale up the Application Instance : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        return Response.status(Response.Status.OK)
                .entity(appVersionInstanceXML).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    /**
     * builds the XML representation for an Application
     *
     * @param app
     *            The {@link org.ow2.jonas.jpaas.manager.api.Application} object
     * @return an {@link org.ow2.jonas.jpaas.manager.api.Application} XML representation
     */
    private ApplicationXML buildApplication(final org.ow2.jonas.jpaas.manager.api.Application app) {
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
    private ApplicationVersionXML buildApplicationVersion(final ApplicationVersion appVersion) {
        ApplicationVersionXML xmlVersionApp = new ApplicationVersionXML();

        xmlVersionApp.setAppId(appVersion.getAppId());
        xmlVersionApp.setAppVerId(appVersion.getVersionId());
        xmlVersionApp.setAppVerLabel(appVersion.getVersionLabel());

        return xmlVersionApp;
    }


    /**
     * builds the XML representation for an ApplicationVersionInstance
     *
     * @param appVersionInstance
     *            The {@link org.ow2.jonas.jpaas.manager.api.Application} object
     * @return an {@link org.ow2.jonas.jpaas.manager.api.Application} XML representation
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
