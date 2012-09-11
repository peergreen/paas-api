/**
 * JOnAS: Java(TM) Open Application Server
 * Copyright (C) 2012 Bull S.A.S.
 * Contact: jonas-team@ow2.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * --------------------------------------------------------------------------
 * $Id$
 * --------------------------------------------------------------------------
 */
package org.ow2.jonas.jpaas.server.task;


import java.util.Date;

/**
 * Work task
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
     * The XML representation of a server
     */
    private String environmentTemplateDescriptor;
    
    /**
     * Default constructor
     * @param task task to execute
     */
    public WorkTask(final ITask task, final Long idTask, final String environmentTemplateDescriptor) {
        this.task = task;
        this.idTask = idTask;
        this.status = Status.QUEUED;
        this.environmentTemplateDescriptor = environmentTemplateDescriptor;
    }

    /**
     * {@inheritDoc}
     */
    public void release() {
        //never called?
        //this.task.execute();
        //this.status = Status.SUCCESS;
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
        }  finally {
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
}
