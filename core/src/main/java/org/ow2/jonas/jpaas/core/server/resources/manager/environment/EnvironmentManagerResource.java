/**
 *
 */
package org.ow2.jonas.jpaas.core.server.resources.manager.environment;

import org.ow2.jonas.jpaas.api.rest.EnvironmentRest;
import org.ow2.jonas.jpaas.api.task.Task;
import org.ow2.jonas.jpaas.api.task.TaskException;
import org.ow2.jonas.jpaas.api.xml.*;
import org.ow2.jonas.jpaas.core.ejb.client.EnvironmentManagerClient;
import org.ow2.jonas.jpaas.core.server.resources.manager.common.Util;
import org.ow2.jonas.jpaas.core.server.task.*;
import org.ow2.jonas.jpaas.api.xml.ErrorXML;
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

    private ErrorXML error = new ErrorXML();

    @Context
    private UriInfo uriInfo;

    private EnvironmentManager envManager;


    public EnvironmentManagerResource() {
        envManager = EnvironmentManagerClient.getProxy();
    }

    @Override
    public Response createEnvironment(String environmentTemplateDescriptor) {

        logger.info("Create environment, desc=" + environmentTemplateDescriptor);

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
    public Response deleteEnvironment(String envId) {
        logger.info("Delete environment, envId=" + envId);
        try {
            envManager.deleteEnvironment(envId);
        } catch (EnvironmentManagerBeanException e) {
            error.setValue("Failed to delete the environment : " + envId + " - " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();

        }
        return Response.status(Response.Status.OK).type(MediaType.APPLICATION_XML_TYPE).build();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getEnvironment(String envId) {
        logger.info("Get env: " + envId);
        Environment env = envManager.getEnvironment(envId);
        EnvironmentXML envXML = Util.buildEnvironment(env,uriInfo.getBaseUri().toString() );
        return Response.status(Response.Status.OK)
                .entity(envXML).type(MediaType.APPLICATION_XML_TYPE).build();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response findEnvironments() {
        logger.info("Get all environments");

        List<EnvironmentXML> listEnvXML = new ArrayList<EnvironmentXML>();
        List<org.ow2.jonas.jpaas.manager.api.Environment> listEnv = envManager.findEnvironments();

        if (listEnv != null) {
            for (org.ow2.jonas.jpaas.manager.api.Environment env : listEnv) {
                EnvironmentXML envXML = Util.buildEnvironment(env, uriInfo.getBaseUri().toString());
                if (envXML != null) {
                    listEnvXML.add(envXML);
                }
            }
        }
        return Response.status(Response.Status.OK)
                .entity(new GenericEntity<List<EnvironmentXML>>(listEnvXML) {
                }).type(MediaType.APPLICATION_XML_TYPE).build();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getDeployedApplicationVersionInstance(
            @PathParam("envId") String envid) {
        logger.info("Get list of deployed application version instance, envId="
                + envid);

        List<ApplicationVersionInstance> listAppVerInstances = envManager.getDeployedApplicationVersionInstance(envid);

        List<ApplicationVersionInstanceXML> listAppVersionInstancesXML = new ArrayList<ApplicationVersionInstanceXML>();

        if (listAppVersionInstancesXML != null) {
            for (ApplicationVersionInstance appVersionInstance : listAppVerInstances) {
                ApplicationVersionInstanceXML appVersionInstanceXML = Util.buildApplicationVersionInstance(appVersionInstance, uriInfo.getBaseUri().toString());
                if (appVersionInstanceXML != null) {
                    listAppVersionInstancesXML.add(appVersionInstanceXML);
                }
            }
        }
        return Response.status(Response.Status.OK)
                .entity(new GenericEntity<List<ApplicationVersionInstanceXML>>(listAppVersionInstancesXML) {
                }).type(MediaType.APPLICATION_XML_TYPE).build();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response startEnvironment(String envid) {
        logger.info("Start environment, envId=" + envid);

        Future<Environment> env = null;

        try {
            env = envManager.startEnvironment(envid);
        } catch (EnvironmentManagerBeanException e1) {
            logger.error("Cannot start environment: " + e1.getMessage());
            ErrorXML error = new ErrorXML();
            error.setValue("Cannot start environment:" + e1);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        Task task = new StartEnvironmentTask(env, uriInfo.getBaseUri().toString());

        TaskXML xmlTask = TaskManager.getSingleton().buildTaskXml(task);

        return Response.status(Response.Status.ACCEPTED).entity(xmlTask)
                .type(MediaType.APPLICATION_XML_TYPE).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response stopEnvironment(String envid) {
        logger.info("Stop environment, envId=" + envid);

        Future<Environment> env = null;

        try {
            env = envManager.stopEnvironment(envid);
        } catch (EnvironmentManagerBeanException e1) {
            logger.error("Cannot start environment: " + e1.getMessage());
            ErrorXML error = new ErrorXML();
            error.setValue("Cannot start environment:" + e1);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

        Task task = new StopEnvironmentTask(env, uriInfo.getBaseUri().toString());

        TaskXML xmlTask = TaskManager.getSingleton().buildTaskXml(task);

        return Response.status(Response.Status.ACCEPTED).entity(xmlTask)
                .type(MediaType.APPLICATION_XML_TYPE).build();
    }

}