
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


@Path("task")
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
    public Response getTask(@PathParam("taskid") String taskid) {

        logger.info("[CO-PaaS-API]: Call getTask(" + taskid + ")");

        TaskManager.getSingleton().clean();
        TaskManager.getSingleton().refresh();


        Task task = TaskManager.getSingleton().getTask(taskid);

        if (task != null) {
            TaskXML taskXML = TaskManager.getSingleton().buildTaskXml(task);
            return Response.status(Response.Status.OK).entity(taskXML).build();
        } else {
            logger.error("Cannot find a Task with ID: " + taskid);
            ErrorXML error = new ErrorXML();
            error.setValue("Cannot find a Task with ID: " + taskid + ".");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Response getTasks() {
        logger.info("[CO-PaaS-API]: Call getTasks()");

        TaskManager.getSingleton().clean();
        TaskManager.getSingleton().refresh();


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
    public Response cancelTask(@PathParam("taskid") final String taskid) {
        Task task = TaskManager.getSingleton().getTask(taskid);
        if (task != null) {

            if (!task.getJob().isDone()) {
                task.getJob().cancel(true);
                task.setStatus(Status.CANCELED);
            }

            return Response.status(Response.Status.OK).build();
        } else {
            ErrorXML error = new ErrorXML();
            error.setValue("Task " + taskid + " not found.\n");

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(error).build();
        }

    }




}
