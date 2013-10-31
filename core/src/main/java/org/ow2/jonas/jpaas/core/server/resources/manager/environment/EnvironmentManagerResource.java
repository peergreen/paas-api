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
import org.ow2.jonas.jpaas.core.server.resources.manager.common.Util;
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
                EnvironmentXML env = Util.buildEnvironment(listEnv);
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
        EnvironmentXML envXML = Util.buildEnvironment(env);
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


}