
package org.ow2.jonas.jpaas.core.server.resources.manager.task;

import java.util.*;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.*;

import org.ow2.jonas.jpaas.api.rest.TaskRest;
import org.ow2.jonas.jpaas.api.task.Task;
import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.xml.ErrorXML;
import org.ow2.jonas.jpaas.api.xml.TaskXML;
import org.ow2.jonas.jpaas.core.server.task.TaskManager;
import org.ow2.util.log.Log;
import org.ow2.util.log.LogFactory;


public class TaskResource implements TaskRest {

    private Log logger = LogFactory.getLog(TaskResource.class);

    @Context
    private UriInfo uriInfo;

    public TaskResource() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getTask(String taskId) {

        logger.info(taskId);

        Task task = TaskManager.getSingleton().getTask(taskId);

        if (task != null) {
            TaskXML taskXML = TaskManager.getSingleton().buildTaskXml(task);
            return Response.status(Response.Status.OK).entity(taskXML).type(MediaType.APPLICATION_XML_TYPE).build();
        } else {
            logger.error("Cannot find a Task with ID: " + taskId);
            ErrorXML error = new ErrorXML();
            error.setValue("Cannot find a Task with ID: " + taskId + ".");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).type(MediaType.APPLICATION_XML_TYPE).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getTasks() {
        logger.info("Retrieve all tasks");

        List<Task> listTask = TaskManager.getSingleton().getTasks();
        List<TaskXML> listTaskXML = new ArrayList<TaskXML>();

        if (listTask != null) {
            for (Task task : listTask) {
                TaskXML taskXML = TaskManager.getSingleton().buildTaskXml(task);
                if (taskXML != null) {
                    listTaskXML.add(taskXML);
                }
            }
        }

        return Response.status(Response.Status.OK)
                .entity(new GenericEntity<List<TaskXML>>(listTaskXML) {
                }).type(MediaType.APPLICATION_XML_TYPE).build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response cancelTask(final String taskId) {
        Task task = TaskManager.getSingleton().getTask(taskId);
        if (task != null) {

            if (!task.getJob().isDone()) {
                task.getJob().cancel(true);
                task.setStatus(Status.CANCELED);
            }

            return Response.status(Response.Status.OK).build();
        } else {
            logger.error("Task " + taskId + " not found.");
            ErrorXML error = new ErrorXML();
            error.setValue("Task " + taskId + " not found.");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).type(MediaType.APPLICATION_XML_TYPE).build();
        }

    }
}
