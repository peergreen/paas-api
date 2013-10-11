/**
 * 
 */
package org.ow2.jonas.jpaas.core.server.resources.manager.taskpool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.ow2.jonas.jpaas.api.resources.manager.taskpool.RestTaskPool;
import org.ow2.jonas.jpaas.api.task.IWorkTask;
import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.task.TaskPool;
import org.ow2.jonas.jpaas.core.server.xml.Error;
import org.ow2.jonas.jpaas.core.server.xml.Link;
import org.ow2.jonas.jpaas.core.server.xml.TaskXML;

/**
 * REST resource of type TaskPool
 * 
 * @author Mohamed Sellami (Telecom SudParis)
 * 
 */
@Path("task")
public class TaskPoolResource implements RestTaskPool {
	private TaskPool taskPool;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Response getTask(@PathParam("taskid") String taskid) {
		System.out.println("[CO-PaaS-API]: Call getTask(" + taskid + ")");

		IWorkTask task = taskPool.INSTANCE.getTask(taskid);
		if (task != null) {
			TaskXML taskXML = buildTask(Long.parseLong(taskid), task);
			return Response.status(Response.Status.OK).entity(taskXML).build();
		} else {
			System.out.println("Cannot find a Task with ID: " + taskid);
			Error error = new Error();
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
		System.out.println("[CO-PaaS-API]: Call getTasks()");

		/* call the getWorkTasks operation from the TaskPool */
		List<IWorkTask> listTask = taskPool.INSTANCE.getWorkTasks();
		List<TaskXML> listTaskXML = new ArrayList<TaskXML>();

		if (listTask != null) {
			for (IWorkTask task : listTask) {
				TaskXML taskXML = buildTask(task.getId(), task);
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
		IWorkTask task = taskPool.INSTANCE.getTask(taskid);
		if (task != null) {
			// TODO cancel the task
			task.updateStatus(Status.CANCELED);

			return Response.status(Response.Status.OK).build();
		} else {
			Error error = new Error();
			error.setValue("Task " + taskid + " not found.\n");

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(error).build();
		}

	}

	/**
	 * builds a generic Task
	 * 
	 * @param idTask
	 * @param workTask
	 * @return TaskXML
	 */
	private TaskXML buildTask(Long idTask, IWorkTask workTask) {
		TaskXML xmlTask = new TaskXML();
		xmlTask.setId(idTask);

		if (workTask != null) {
			Status status = workTask.getStatus();
			if (status != null) {
				xmlTask.setStatus(String.valueOf(status));
			}
			Date startTime = workTask.getStartTime();
			if (startTime != null) {
				xmlTask.setStartTime(startTime);
			}
			Date endTime = workTask.getEndTime();
			if (endTime != null) {
				xmlTask.setEndTime(endTime);
			}
			List<Link> links = workTask.getLinkList();
			if (links != null)
				xmlTask.setLink(links);
		}
		return xmlTask;
	}

}
