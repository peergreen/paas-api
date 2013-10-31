/**
 *
 */
package org.ow2.jonas.jpaas.core.server.resources.manager.application;

import org.ow2.jonas.jpaas.api.rest.ApplicationRest;
import org.ow2.jonas.jpaas.api.rest.DeployableRest;
import org.ow2.jonas.jpaas.api.xml.*;
import org.ow2.jonas.jpaas.application.api.ApplicationManager;
import org.ow2.jonas.jpaas.application.api.ApplicationManagerBeanException;
import org.ow2.jonas.jpaas.core.ejb.client.ApplicationManagerClient;
import org.ow2.jonas.jpaas.core.server.resources.manager.common.Util;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersion;
import org.ow2.jonas.jpaas.manager.api.ApplicationVersionInstance;
import org.ow2.jonas.jpaas.manager.api.Deployable;
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

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class DeployableManagerResource implements DeployableRest {

    private Log logger = LogFactory.getLog(DeployableManagerResource.class);

    private ApplicationManager appManager;
    private ErrorXML error = new ErrorXML();

    public DeployableManagerResource() {
        appManager = ApplicationManagerClient.getProxy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response uploadDeployable(String appId, String versionId, String deployableId, InputStream uploadedInputStream) {

        logger.info("Upload deployable with appId=" + appId + ", versionId=" + versionId + ", deployableId=" + deployableId);

        ApplicationVersion version = appManager.getApplicationVersion(appId,versionId);

        if (version == null) {
            error.setValue("Failed to get the application version: " + appId + "/" + versionId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        List<Deployable> deployables = version.getSortedDeployablesList();
        Deployable deployable = null;
        for (Deployable d:deployables) {
            if (d.getDeployabledId().equals(deployableId)) {
                deployable = d;
                break;
            }
        }

        if (deployable == null) {
            error.setValue("Unable to get the deployable: " + deployableId);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        String uploadedFileLocation = deployable.getLocationUrl();

        // save it
        try {
            writeToFile(uploadedInputStream, deployable.getLocationUrl());
        } catch (Exception e) {
            e.printStackTrace();
            error.setValue("Failed to write the file '" + uploadedInputStream + "' : " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }


        deployable.setLocationUrl(uploadedFileLocation);
        deployable.setUploaded(true);

        appManager.notifyArtefactUploades(appId, versionId, deployableId);
        version = appManager.getApplicationVersion(appId,versionId);

        DeployableXML deployableXML = Util.buildDeployable(deployable);


        return Response.status(Response.Status.OK)
                    .entity(new GenericEntity<DeployableXML>(deployableXML) {
                    }).type(MediaType.APPLICATION_XML_TYPE).build();

     }



    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) throws Exception {

        logger.info("load file to" + uploadedFileLocation);

        File file = new File(uploadedFileLocation);
        if (file.exists()) {
            logger.warn("The file '" + uploadedFileLocation +"' is going to be overwritten");
        }
        File parentDir = file.getAbsoluteFile().getParentFile();

        parentDir.mkdirs();

        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {

            e.printStackTrace();
            throw e;
        }

    }

}
