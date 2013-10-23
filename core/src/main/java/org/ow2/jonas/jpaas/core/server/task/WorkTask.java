/**
 * JPaaS
 * Copyright 2012 Bull S.A.S.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * $Id:$
 */ 

package org.ow2.jonas.jpaas.core.server.task;

import java.util.Date;
import java.util.List;

import org.ow2.jonas.jpaas.api.task.ITask;
import org.ow2.jonas.jpaas.api.task.IWorkTask;
import org.ow2.jonas.jpaas.api.task.Status;
import org.ow2.jonas.jpaas.api.xml.Link;

/**
 * Work task
 * 
 * @author Jeremy Cazaux
 */
public class WorkTask implements IWorkTask {

	/**
	 * Task to execute
	 */
	private ITask task;

	/**
	 * Id of the task to set
	 */
	private Long idTask;

	/**
	 * Status of the task
	 */
	private Status status;

	/**
	 * Date that the system start the execution of the task
	 */
	private Date startTime;

	/**
	 * Date that the task has been executed
	 */
	private Date endTime;

	/**
	 * The XML representation of an environment
	 */
	private String environmentTemplateDescriptor;

	/**
     * 
     */
	private List<Link> linkList;

	/**
	 * Default constructor
	 * 
	 * @param task
	 *            task to execute
	 */
	public WorkTask(final ITask task, final Long idTask,
			final String environmentTemplateDescriptor,
			final List<Link> linkList) {
		this.task = task;
		this.idTask = idTask;
		this.status = Status.QUEUED;
		this.environmentTemplateDescriptor = environmentTemplateDescriptor;
		this.linkList = linkList;
	}

	/**
	 * {@inheritDoc}
	 */
	public void release() {
		// never called?
		// this.task.execute();
		// this.status = Status.SUCCESS;
	}

	/**
	 * {@inheritDoc}
	 */
	public void run() {
		this.startTime = new Date();
		this.status = Status.RUNNING;
		this.task.preExecution();
		try {
			this.task.execute();
			this.status = Status.SUCCESS;
		} catch (Exception e) {
			System.out.println("Cannot execute task " + this.idTask);
			this.status = Status.ERROR;
		} finally {
			this.task.postExecution();
			this.endTime = new Date();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long getId() {
		return this.idTask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ITask getTask() {
		return this.task;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Status getStatus() {
		return this.status;
	}

	/**
	 * {@inheritDoc}
	 */
	public synchronized void updateStatus(final Status status) {
		this.status = status;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getStartTime() {
		return this.startTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getEndTime() {
		return this.endTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEnvironmentXML() {
		return this.environmentTemplateDescriptor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Link> getLinkList() {
		return linkList;
	}

	public void setLinkList(List<Link> linkList) {
		this.linkList = linkList;
	}

}
