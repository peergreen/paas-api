package org.ow2.jonas.jpaas.api.resources.manager.taskpool;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("task")
public interface RestTaskPool {

	/**
	 * Get the Task with the ID=taskid <br>
	 * Command: GET /task/{taskid}
	 * 
	 * @param taskid
	 *            The Task's ID.
	 * @return the XML Task description
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("{taskid}")
	Response getTask(@PathParam("taskid") String taskid);

	/**
	 * Finds the list of the available Tasks <br>
	 * Command: GET /task/
	 * 
	 * @return a list of the available XML Task descriptions.
	 */
	@GET
	@Produces(MediaType.APPLICATION_XML)
	Response getTasks();

	/**
	 * Cancel a Task
	 * 
	 * @param taskId
	 *            Id of the task to cancel
	 * 
	 */
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("{taskid}/action/cancel")
	Response cancelTask(@PathParam("taskid") final String taskid);

}